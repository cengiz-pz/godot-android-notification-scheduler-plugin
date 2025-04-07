//
// © 2024-present https://github.com/cengiz-pz
//

package org.godotengine.plugin.android.notification;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.provider.Settings;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;
import org.godotengine.plugin.android.notification.model.ChannelData;
import org.godotengine.plugin.android.notification.model.NotificationData;

import java.util.Set;

public class NotificationSchedulerPlugin extends GodotPlugin {
	private static final String LOG_TAG = "godot::" + NotificationSchedulerPlugin.class.getSimpleName();

	static NotificationSchedulerPlugin instance;

	private static final SignalInfo PERMISSION_GRANTED_SIGNAL = new SignalInfo("permission_granted", String.class);
	private static final SignalInfo PERMISSION_DENIED_SIGNAL = new SignalInfo("permission_denied", String.class);
	private static final SignalInfo NOTIFICATION_OPENED_SIGNAL = new SignalInfo("notification_opened", Integer.class);
	private static final SignalInfo NOTIFICATION_DISMISSED_SIGNAL = new SignalInfo("notification_dismissed", Integer.class);

	private static final int POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 11803;

	private static final int NOTIFICATION_NOT_FOUND = -1;

	private Activity activity;

	public NotificationSchedulerPlugin(Godot godot) {
		super(godot);
	}

	/**
	 * Creates a notification channel with given ID. If a channel already exists with the given ID,
	 * then the call will be ignored.
	 *
	 * @param data dictionary containing channel ID, channel name, and channel description
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	@UsedByGodot
	public void create_notification_channel(Dictionary data) {
		ChannelData channelData = new ChannelData(data);
		if (channelData.isValid()) {
			NotificationChannel channel = new NotificationChannel(channelData.getId(), channelData.getName(),
					channelData.getImportance());
			channel.setDescription(channelData.getDescription());
			NotificationManager manager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
			manager.createNotificationChannel(channel);
			Log.d(LOG_TAG, String.format("%s():: channel id: %s, name: %s, description: %s",
					"create_notification_channel", channelData.getId(), channelData.getName(), channelData.getDescription()));
		} else {
			Log.e(LOG_TAG, "create_notification_channel(): invalid channel data object");
		}
	}

	/**
	 * Schedule single, non-repeating notification
	 *
	 * @param data dictionary containing notification data, including delaySeconds that specifies
	 *				how many seconds from now to schedule the notification.
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	@UsedByGodot
	public void schedule(Dictionary data) {
		NotificationData notificationData = new NotificationData(data);
		if (notificationData.isValid()) {
			@SuppressWarnings("ConstantConditions") int notificationId = notificationData.getId();

			Intent intent = new Intent(activity.getApplicationContext(), NotificationReceiver.class);
			intent.putExtra(NotificationData.DATA_KEY_ID, notificationId);
			intent.putExtra(NotificationData.DATA_KEY_CHANNEL_ID, notificationData.getChannelId());
			intent.putExtra(NotificationData.DATA_KEY_TITLE, notificationData.getTitle());
			intent.putExtra(NotificationData.DATA_KEY_CONTENT, notificationData.getContent());
			intent.putExtra(NotificationData.DATA_KEY_SMALL_ICON_NAME, notificationData.getSmallIconName());

			if (notificationData.hasDeeplink()) {
				intent.putExtra(NotificationData.DATA_KEY_DEEPLINK, notificationData.getDeeplink());
			}

			if (notificationData.hasRestartAppOption()) {
				intent.putExtra(NotificationData.OPTION_KEY_RESTART_APP, true);
			}

			if (notificationData.hasInterval()) {
				scheduleRepeatingNotification(activity, notificationId, intent, notificationData.getDelay(), notificationData.getInterval());
			} else {
				scheduleNotification(activity, notificationId, intent, notificationData.getDelay());
			}
		} else {
			Log.e(LOG_TAG, "schedule(): invalid notification data object");
		}
	}

	/**
	 * Cancel notification with given ID
	 *
	 * @param notificationId ID of notification to cancel
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	@UsedByGodot
	public void cancel(int notificationId) {
		cancelNotification(activity, notificationId);
		Log.d(LOG_TAG, "cancel():: notification id: " + notificationId);
	}

	/**
	 * Return notification ID if it exists in current intent, else return {@code defaultValue}
	 *
	 * @param defaultValue value to return if notification ID does not exist
	 */
	@UsedByGodot
	public int get_notification_id(int defaultValue) {
		int notificationId = defaultValue;
		Activity activity = getActivity();
		if (activity != null) {
			Intent intent = getActivity().getIntent();
			if (intent.hasExtra(NotificationData.DATA_KEY_ID)) {
				notificationId = intent.getIntExtra(NotificationData.DATA_KEY_ID, defaultValue);
				Log.i(LOG_TAG, "get_notification_id():: intent with notification id: " + notificationId);
			} else {
				Log.i(LOG_TAG, "get_notification_id():: notification id not found");
			}
		}
		return notificationId;
	}

	/**
	 * Returns true if app has already been granted POST_NOTIFICATIONS permissions
	 */
	@UsedByGodot
	public boolean has_post_notifications_permission() {
		boolean result = false;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
			if (NotificationManagerCompat.from(activity.getApplicationContext()).areNotificationsEnabled()) {
				result = true;
			}
		} else {
			result = true;
			Log.d(LOG_TAG, "has_post_notifications_permission():: API level is " + Build.VERSION.SDK_INT);
		}
		return result;
	}

	/**
	 * Sends a request to acquire POST_NOTIFICATIONS permission for the app
	 */
	@UsedByGodot
	public void request_post_notifications_permission() {
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
				ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.POST_NOTIFICATIONS },
						POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE);
			} else {
				Log.i(LOG_TAG, "request_post_notifications_permission():: can't request permission, because SDK version is " + Build.VERSION.SDK_INT);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "request_post_notifications_permission():: Failed to request permission due to " + e.getMessage());
		}
	}

	/**
	 * Opens APP INFO settings screen
	 */
	@UsedByGodot
	public void open_app_info_settings() {
		Log.d(LOG_TAG, "open_app_info_settings()");
		
		try {
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
			intent.setData(uri);
			activity.startActivity(intent);
		} catch (Exception e) {
			Log.e(LOG_TAG, "open_app_info_settings():: Failed due to "+ e.getMessage());
		}
	}

	@NonNull
	@Override
	public String getPluginName() {
		return this.getClass().getSimpleName();
	}

	@NonNull
	@Override
	public Set<SignalInfo> getPluginSignals() {
		Set<SignalInfo> signals = new ArraySet<>();
		signals.add(NOTIFICATION_OPENED_SIGNAL);
		signals.add(NOTIFICATION_DISMISSED_SIGNAL);
		signals.add(PERMISSION_GRANTED_SIGNAL);
		signals.add(PERMISSION_DENIED_SIGNAL);
		return signals;
	}

	@Nullable
	@Override
	public View onMainCreate(Activity activity) {
		this.activity = activity;
		instance = this;
		return super.onMainCreate(activity);
	}

	@Override
	public void onGodotSetupCompleted() {
		super.onGodotSetupCompleted();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
			Activity activity = getActivity();
			if (activity != null) {
				if (NotificationManagerCompat.from(activity.getApplicationContext()).areNotificationsEnabled()) {
					Log.i(LOG_TAG, "onGodotSetupCompleted():: POST_NOTIFICATIONS permission has already been granted");
				}
			} else {
				Log.e(LOG_TAG, "onGodotSetupCompleted():: can't check permission status due to null activity");
			}
		}

		int notificationId = checkIntent(this.activity.getIntent());
		if (notificationId != NOTIFICATION_NOT_FOUND) {
			handleNotificationOpened(notificationId);
		}
	}

	@Override
	public void onMainDestroy() {
		instance = null;
		super.onMainDestroy();
	}

	@Override
	public void onMainRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onMainRequestPermissionsResult(requestCode, permissions, grantResults);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
			if (requestCode == POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE) {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(LOG_TAG, "onMainRequestPermissionsResult():: permission request granted");
					emitSignal(getGodot(), getPluginName(), PERMISSION_GRANTED_SIGNAL, Manifest.permission.POST_NOTIFICATIONS);
				} else {
					Log.d(LOG_TAG, "onMainRequestPermissionsResult():: permission request denied");
					emitSignal(getGodot(), getPluginName(), PERMISSION_DENIED_SIGNAL, Manifest.permission.POST_NOTIFICATIONS);
				}
			}
		} else {
			Log.e(LOG_TAG, "onMainRequestPermissionsResult():: can't check permission result, because SDK version is " + Build.VERSION.SDK_INT);
		}
	}

	static int checkIntent(Intent intent) {
		int notificationId = NOTIFICATION_NOT_FOUND;

		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				if (bundle.containsKey(NotificationData.DATA_KEY_ID)) {
					notificationId = bundle.getInt(NotificationData.DATA_KEY_ID, NOTIFICATION_NOT_FOUND);
					Log.d(LOG_TAG, "checkIntent() received notification id " + notificationId);
				}
				else {
					Log.d(LOG_TAG, "checkIntent() bundle does not contain notification id");
				}
			}
			else {
				Log.d(LOG_TAG, "checkIntent() bundle is null for intent " + intent);
			}
		}
		else {
			Log.d(LOG_TAG, "checkIntent() intent is null.");
		}

		return notificationId;
	}

	void handleNotificationOpened(int notificationId) {
		emitSignal(getGodot(), getPluginName(), NOTIFICATION_OPENED_SIGNAL, notificationId);
	}

	void handleNotificationDismissed(int notificationId) {
		emitSignal(getGodot(), getPluginName(), NOTIFICATION_DISMISSED_SIGNAL, notificationId);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private long calculateTimeAfterDelay(int delaySeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, delaySeconds);
		return calendar.getTimeInMillis();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private void scheduleNotification(Activity activity, int notificationId, Intent intent, int delaySeconds) {
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
		long timeAfterDelay = calculateTimeAfterDelay(delaySeconds);
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeAfterDelay,
				PendingIntent.getBroadcast(activity.getApplicationContext(), notificationId, intent,
						PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
		Log.i(LOG_TAG, String.format("Scheduled notification '%d' to be delivered at %d.", notificationId, timeAfterDelay));
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private void scheduleRepeatingNotification(Activity activity, int notificationId, Intent intent, int delaySeconds, int intervalSeconds) {
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
		long timeAfterDelay = calculateTimeAfterDelay(delaySeconds);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeAfterDelay, intervalSeconds*1000L,
				PendingIntent.getBroadcast(activity.getApplicationContext(), notificationId, intent,
						PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
		Log.i(LOG_TAG, String.format("Scheduled notification '%d' to be delivered at %d with %ds interval.", notificationId, timeAfterDelay, intervalSeconds));
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void cancelNotification(Activity activity, int notificationId) {
		Context context = activity.getApplicationContext();

		// cancel alarm
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(context, NotificationReceiver.class);
		intent.putExtra(NotificationData.DATA_KEY_ID, notificationId);
		alarmManager.cancel(PendingIntent.getBroadcast(activity.getApplicationContext(), notificationId, intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

		// cancel notification
		NotificationManagerCompat.from(context).cancel(notificationId);
	}

}
