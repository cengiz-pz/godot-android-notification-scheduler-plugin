package org.godotengine.plugin.android.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


public class NotificationReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "godot::" + NotificationReceiver.class.getSimpleName();

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent receivedIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent notificationIntent = new Intent(context, NotificationService.class);
            notificationIntent.putExtras(receivedIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(LOG_TAG, String.format("%s():: starting notification service in foreground", "onReceive"));
                context.startForegroundService(notificationIntent);
            } else {
                Log.i(LOG_TAG, String.format("%s():: starting notification service", "onReceive"));
                context.startService(notificationIntent);
            }
        } else {
            Log.w(LOG_TAG, String.format("%s():: unable to process notification as current SDK is %d and required SDK is %d",
                    "onReceive", Build.VERSION.SDK_INT, Build.VERSION_CODES.M));
        }
    }
}
