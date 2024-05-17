//
// © 2024-present https://github.com/cengiz-pz
//

package org.godotengine.plugin.android.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.godotengine.plugin.android.notification.model.NotificationData;


public class NotificationReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "godot::" + NotificationReceiver.class.getSimpleName();

	private static final String ICON_RESOURCE_TYPE = "drawable";

	public NotificationReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			Log.e(LOG_TAG, String.format("%s():: Received intent is null. Unable to generate notification.",
					"onReceive"));
		} else if (intent.hasExtra(NotificationData.DATA_KEY_ID)) {
			int notificationId = intent.getIntExtra(NotificationData.DATA_KEY_ID, 0);
			String channelId = intent.getStringExtra(NotificationData.DATA_KEY_CHANNEL_ID);
			String title = intent.getStringExtra(NotificationData.DATA_KEY_TITLE);
			String content = intent.getStringExtra(NotificationData.DATA_KEY_CONTENT);
			String smallIconName = intent.getStringExtra(NotificationData.DATA_KEY_SMALL_ICON_NAME);

			Intent notificationActionIntent = new Intent(context, ResultActivity.class);
			notificationActionIntent.putExtra(NotificationData.DATA_KEY_ID, notificationId);

			if (intent.hasExtra(NotificationData.DATA_KEY_DEEPLINK)) {
				notificationActionIntent.putExtra(NotificationData.DATA_KEY_DEEPLINK, intent.getStringExtra(NotificationData.DATA_KEY_DEEPLINK));
			}

			if (intent.hasExtra(NotificationData.OPTION_KEY_RESTART_APP)) {
				notificationActionIntent.putExtra(NotificationData.OPTION_KEY_RESTART_APP, true);
			}

			notificationActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

			Log.i(LOG_TAG, String.format("%s():: received notification id:'%d' - channel id:%s - title:'%s' - content:'%s' - small icon name:'%s",
					"onReceive", notificationId, channelId, title, content, smallIconName));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationActionIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

				Resources resources = context.getResources();
				@SuppressLint("DiscouragedApi") NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(resources.getIdentifier(smallIconName, ICON_RESOURCE_TYPE, context.getPackageName()))
						/* TODO: large icon not working. It needs to be tested again in future versions.
						.setLargeIcon(BitmapFactory.decodeResource(r, r.getIdentifier(LARGE_ICON_LABEL, LARGE_ICON_RESOURCE_TYPE, context.getPackageName())))
						.setStyle(new NotificationCompat.BigPictureStyle()
								.bigPicture(BitmapFactory.decodeResource(r, r.getIdentifier(LARGE_ICON_LABEL, LARGE_ICON_RESOURCE_TYPE, context.getPackageName())))
								.bigLargeIcon(BitmapFactory.decodeResource(r, r.getIdentifier(LARGE_ICON_LABEL, LARGE_ICON_RESOURCE_TYPE, context.getPackageName()))))
						 */
						.setContentTitle(title)
						.setContentText(content)
						.setPriority(NotificationCompat.PRIORITY_DEFAULT)
						.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
						.setContentIntent(pendingIntent)
						.setAutoCancel(true);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
						ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
					Notification notification = notificationBuilder.build();
					NotificationManagerCompat.from(context).notify(notificationId, notification);
				} else {
					Log.w(LOG_TAG, String.format("%s():: unable to process notification as %s permission is not granted",
							"onReceive", Manifest.permission.POST_NOTIFICATIONS));
				}
			} else {
				Log.w(LOG_TAG, String.format("%s():: unable to process notification as current SDK is %d and required SDK is %d",
						"onReceive", Build.VERSION.SDK_INT, Build.VERSION_CODES.M));
			}
		} else {
			Log.e(LOG_TAG, String.format("%s():: %s extra not found in intent. Unable to generate notification.",
					"onReceive", NotificationData.DATA_KEY_ID));
		}
	}
}
