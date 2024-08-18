//
// © 2024-present https://github.com/cengiz-pz
//

package org.godotengine.plugin.android.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.godotengine.plugin.android.notification.model.NotificationData;


public class CancelNotificationReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "godot::" + CancelNotificationReceiver.class.getSimpleName();

	private static final String ICON_RESOURCE_TYPE = "drawable";

	public CancelNotificationReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			Log.e(LOG_TAG, String.format("%s():: Received intent is null. Unable to generate notification.",
					"onReceive"));
		} else if (intent.hasExtra(NotificationData.DATA_KEY_ID)) {
			int notificationId = intent.getIntExtra(NotificationData.DATA_KEY_ID, 0);
			if (NotificationSchedulerPlugin.instance == null) {
				Log.e(LOG_TAG, String.format("%s():: Plugin instance not found!.", "onReceive"));
			} else {
				NotificationSchedulerPlugin.instance.handleNotificationDismissed(notificationId);
			}
		} else {
			Log.e(LOG_TAG, String.format("%s():: %s extra not found in intent. Unable to generate notification.",
					"onReceive", NotificationData.DATA_KEY_ID));
		}
	}
}
