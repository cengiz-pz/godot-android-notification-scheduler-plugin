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
import android.util.Log;
import android.view.View;

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

import java.util.Set;

public class GodotAndroidNotificationSchedulerPlugin extends GodotPlugin {
    private static final String LOG_TAG = "godot::" + GodotAndroidNotificationSchedulerPlugin.class.getSimpleName();

    static GodotAndroidNotificationSchedulerPlugin instance;

    private static final String PERMISSION_GRANTED_SIGNAL_NAME = "permission_granted";
    private static final String PERMISSION_DENIED_SIGNAL_NAME = "permission_denied";
    private static final String NOTIFICATION_OPENED_SIGNAL_NAME = "notification_opened";

    private static final String GODOT_DATA_KEY_ID = "id";
    private static final String GODOT_DATA_KEY_CHANNEL_ID = "channel_id";
    private static final String GODOT_DATA_KEY_TITLE = "title";
    private static final String GODOT_DATA_KEY_CONTENT = "content";
    private static final String GODOT_DATA_KEY_DEEPLINK = "deeplink";
    private static final String GODOT_DATA_KEY_SMALL_ICON_NAME = "small_icon_name";

    private static final int POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 11803;

    private Activity activity;

    public GodotAndroidNotificationSchedulerPlugin(Godot godot) {
        super(godot);
    }

    /**
     * Creates a notification channel with given ID. If a channel already exists with the given ID,
     * then the call will be ignored.
     *
     * @param id channel ID
     * @param name channel name
     * @param description channel description
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @UsedByGodot
    public void createNotificationChannel(String id, String name, String description) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        NotificationManager manager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Log.d(LOG_TAG, String.format("%s():: channel id: %s, name: %s, description: %s",
                "createNotificationChannel", id, name, description));
    }

    /**
     * Schedule single, non-repeating notification
     *
     * @param notificationData map containing notification data
     * @param delaySeconds how many seconds from now to schedule notification
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @UsedByGodot
    public void schedule(Dictionary notificationData, int delaySeconds) {
        if (notificationData.containsKey(GODOT_DATA_KEY_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_CHANNEL_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_TITLE) &&
                notificationData.containsKey(GODOT_DATA_KEY_CONTENT) &&
                notificationData.containsKey(GODOT_DATA_KEY_SMALL_ICON_NAME)) {
            Intent intent = createNotificationIntent(activity.getApplicationContext(), notificationData);

            @SuppressWarnings("ConstantConditions") int notificationId = (int) notificationData.get(GODOT_DATA_KEY_ID);
            scheduleNotification(activity, notificationId, intent, delaySeconds);
            Log.d(LOG_TAG, String.format("%s():: notification id: %d, channelId: %s, title: %s, content: %s, small_icon_name: %s, delay: %d seconds",
                    "schedule", notificationId, notificationData.get(GODOT_DATA_KEY_CHANNEL_ID), notificationData.get(GODOT_DATA_KEY_TITLE),
                    notificationData.get(GODOT_DATA_KEY_CONTENT), notificationData.get(GODOT_DATA_KEY_SMALL_ICON_NAME), delaySeconds));
        } else {
            Log.e(LOG_TAG, "schedule(): invalid notification data object");
        }
    }

    /**
     * Schedule repeating notifications
     *
     * @param notificationData map containing notification data
     * @param delaySeconds how many seconds from now to schedule first notification
     * @param intervalSeconds interval in seconds between each repeating notification
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @UsedByGodot
    public void scheduleRepeating(Dictionary notificationData, int delaySeconds, int intervalSeconds) {
        if (notificationData.containsKey(GODOT_DATA_KEY_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_CHANNEL_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_TITLE) &&
                notificationData.containsKey(GODOT_DATA_KEY_CONTENT) &&
                notificationData.containsKey(GODOT_DATA_KEY_SMALL_ICON_NAME)) {
            Intent intent = createNotificationIntent(activity.getApplicationContext(), notificationData);

            @SuppressWarnings("ConstantConditions") int notificationId = (int) notificationData.get(GODOT_DATA_KEY_ID);
            scheduleRepeatingNotification(activity, notificationId, intent, delaySeconds, intervalSeconds);
            Log.d(LOG_TAG, String.format("%s():: notification id: %d, channelId: %s, title: %s, content: %s, small_icon_name: %s, delay: %d seconds, interval: %d seconds",
                    "scheduleRepeating", notificationId, notificationData.get(GODOT_DATA_KEY_CHANNEL_ID), notificationData.get(GODOT_DATA_KEY_TITLE),
                    notificationData.get(GODOT_DATA_KEY_CONTENT), notificationData.get(GODOT_DATA_KEY_SMALL_ICON_NAME), delaySeconds, intervalSeconds));
        } else {
            Log.e(LOG_TAG, "schedule(): invalid notification data object");
        }
    }

    /**
     * Schedule a single, non-repeating notification with action intent containing a URI
     *
     * @param notificationData map containing notification data
     * @param delaySeconds how many seconds from now to schedule notification
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @UsedByGodot
    public void scheduleWithDeeplink(Dictionary notificationData, int delaySeconds) {
        if (notificationData.containsKey(GODOT_DATA_KEY_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_CHANNEL_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_TITLE) &&
                notificationData.containsKey(GODOT_DATA_KEY_CONTENT) &&
                notificationData.containsKey(GODOT_DATA_KEY_SMALL_ICON_NAME) &&
                notificationData.containsKey(GODOT_DATA_KEY_DEEPLINK)) {
            Intent intent = createNotificationIntent(activity.getApplicationContext(), notificationData);
            intent.putExtra(NotificationService.NOTIFICATION_URI_LABEL, (String) notificationData.get(GODOT_DATA_KEY_DEEPLINK));

            @SuppressWarnings("ConstantConditions") int notificationId = (int) notificationData.get(GODOT_DATA_KEY_ID);
            scheduleNotification(activity, notificationId, intent, delaySeconds);
            Log.d(LOG_TAG, String.format("%s():: notification id: %d, channelId: %s, title: %s, content: %s, small_icon_name: %s, deeplink: %s, delay: %d seconds",
                    "scheduleWithDeeplink", notificationId, notificationData.get(GODOT_DATA_KEY_CHANNEL_ID), notificationData.get(GODOT_DATA_KEY_TITLE),
                    notificationData.get(GODOT_DATA_KEY_CONTENT), notificationData.get(GODOT_DATA_KEY_SMALL_ICON_NAME), notificationData.get(GODOT_DATA_KEY_DEEPLINK), delaySeconds));
        } else {
            Log.e(LOG_TAG, "schedule(): invalid notification data object");
        }
    }

    /**
     * Schedule repeating notifications with action intent containing a URI
     *
     * @param notificationData map containing notification data
     * @param delaySeconds how many seconds from now to schedule first notification
     * @param intervalSeconds interval in seconds between each repeating notification
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @UsedByGodot
    public void scheduleRepeatingWithDeeplink(Dictionary notificationData, int delaySeconds, int intervalSeconds) {
        if (notificationData.containsKey(GODOT_DATA_KEY_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_CHANNEL_ID) &&
                notificationData.containsKey(GODOT_DATA_KEY_TITLE) &&
                notificationData.containsKey(GODOT_DATA_KEY_CONTENT) &&
                notificationData.containsKey(GODOT_DATA_KEY_SMALL_ICON_NAME) &&
                notificationData.containsKey(GODOT_DATA_KEY_DEEPLINK)) {
            Intent intent = createNotificationIntent(activity.getApplicationContext(), notificationData);
            intent.putExtra(NotificationService.NOTIFICATION_URI_LABEL, (String) notificationData.get(GODOT_DATA_KEY_DEEPLINK));

            @SuppressWarnings("ConstantConditions") int notificationId = (int) notificationData.get(GODOT_DATA_KEY_ID);
            scheduleRepeatingNotification(activity, notificationId, intent, delaySeconds, intervalSeconds);
            Log.d(LOG_TAG, String.format("%s():: notification id: %d, channelId: %s, title: %s, content: %s, small_icon_name: %s, deeplink: %s, delay: %d seconds, interval: %d seconds",
                    "scheduleRepeatingWithDeeplink", notificationId, notificationData.get(GODOT_DATA_KEY_CHANNEL_ID), notificationData.get(GODOT_DATA_KEY_TITLE),
                    notificationData.get(GODOT_DATA_KEY_CONTENT), notificationData.get(GODOT_DATA_KEY_SMALL_ICON_NAME), notificationData.get(GODOT_DATA_KEY_DEEPLINK), delaySeconds, intervalSeconds));
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
    public int getNotificationId(int defaultValue) {
        int notificationId = defaultValue;
        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(NotificationService.NOTIFICATION_ID_LABEL)) {
                notificationId = intent.getIntExtra(NotificationService.NOTIFICATION_ID_LABEL, defaultValue);
                Log.i(LOG_TAG, "getNotificationId():: intent with notification id: " + notificationId);
            } else {
                Log.i(LOG_TAG, "getNotificationId():: notification id not found");
            }
        }
        return notificationId;
    }

    /**
     * Returns true if app has already been granted POST_NOTIFICATIONS permissions
     */
    @UsedByGodot
    public boolean hasPostNotificationsPermission() {
        boolean result = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            if (NotificationManagerCompat.from(activity.getApplicationContext()).areNotificationsEnabled()) {
                result = true;
            }
        } else {
            result = true;
            Log.d(LOG_TAG, "hasPostNotificationsPermission():: API level is " + Build.VERSION.SDK_INT);
        }
        return result;
    }

    /**
     * Sends a request to acquire POST_NOTIFICATIONS permission for the app
     */
    @UsedByGodot
    public void requestPostNotificationsPermission() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                        POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE);
            } else {
                Log.i(LOG_TAG, "requestPostNotificationsPermission():: can't request permission, because SDK version is " + Build.VERSION.SDK_INT);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "requestPostNotificationsPermission():: Failed to request permission due to " + e.getMessage());
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
        signals.add(new SignalInfo(NOTIFICATION_OPENED_SIGNAL_NAME, Integer.class));
        signals.add(new SignalInfo(PERMISSION_GRANTED_SIGNAL_NAME, String.class));
        signals.add(new SignalInfo(PERMISSION_DENIED_SIGNAL_NAME, String.class));
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
                    Log.d(LOG_TAG, "onGodotSetupCompleted():: POST_NOTIFICATIONS permission has already been granted");
                }
            } else {
                Log.e(LOG_TAG, "onGodotSetupCompleted():: can't check permission status due to null activity");
            }
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
                    emitSignal(PERMISSION_GRANTED_SIGNAL_NAME, Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    Log.d(LOG_TAG, "onMainRequestPermissionsResult():: permission request denied");
                    emitSignal(PERMISSION_DENIED_SIGNAL_NAME, Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        } else {
            Log.e(LOG_TAG, "onMainRequestPermissionsResult():: can't check permission result, because SDK version is " + Build.VERSION.SDK_INT);
        }
    }


    void handleNotificationOpened(int notificationId) {
        emitSignal(NOTIFICATION_OPENED_SIGNAL_NAME, notificationId);
    }

    @SuppressWarnings("ConstantConditions")
    private Intent createNotificationIntent(Context context, Dictionary data) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationService.NOTIFICATION_ID_LABEL, (int) data.get(GODOT_DATA_KEY_ID));
        intent.putExtra(NotificationService.CHANNEL_ID_LABEL, (String) data.get(GODOT_DATA_KEY_CHANNEL_ID));
        intent.putExtra(NotificationService.NOTIFICATION_TITLE_LABEL, (String) data.get(GODOT_DATA_KEY_TITLE));
        intent.putExtra(NotificationService.NOTIFICATION_CONTENT_LABEL, (String) data.get(GODOT_DATA_KEY_CONTENT));
        intent.putExtra(NotificationService.NOTIFICATION_SMALL_ICON_NAME, (String) data.get(GODOT_DATA_KEY_SMALL_ICON_NAME));
        return intent;
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
        Log.i(LOG_TAG, "Scheduled notification to be delivered at " + timeAfterDelay);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleRepeatingNotification(Activity activity, int notificationId, Intent intent, int delaySeconds, int intervalSeconds) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        long timeAfterDelay = calculateTimeAfterDelay(delaySeconds);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeAfterDelay, intervalSeconds*1000L,
                PendingIntent.getBroadcast(activity.getApplicationContext(), notificationId, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        Log.i(LOG_TAG, "Scheduled repeating notification to be delivered starting at " + timeAfterDelay + " with " + intervalSeconds + " seconds interval");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cancelNotification(Activity activity, int notificationId) {
        Context context = activity.getApplicationContext();

        // cancel alarm
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationService.NOTIFICATION_ID_LABEL, notificationId);
        alarmManager.cancel(PendingIntent.getBroadcast(activity.getApplicationContext(), notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

        // cancel notification
        NotificationManagerCompat.from(context).cancel(notificationId);
    }
}
