package org.godotengine.plugin.android.notification;

import static org.godotengine.plugin.android.notification.NotificationConstants.CHANNEL_ID_LABEL;
import static org.godotengine.plugin.android.notification.NotificationConstants.ICON_RESOURCE_TYPE;
import static org.godotengine.plugin.android.notification.NotificationConstants.NOTIFICATION_CONTENT_LABEL;
import static org.godotengine.plugin.android.notification.NotificationConstants.NOTIFICATION_ID_LABEL;
import static org.godotengine.plugin.android.notification.NotificationConstants.NOTIFICATION_SMALL_ICON_NAME;
import static org.godotengine.plugin.android.notification.NotificationConstants.NOTIFICATION_TITLE_LABEL;
import static org.godotengine.plugin.android.notification.NotificationConstants.NOTIFICATION_URI_LABEL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class NotificationReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "godot::" + NotificationReceiver.class.getSimpleName();

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(LOG_TAG, String.format("%s():: Received intent is null. Unable to generate notification.",
                    "notify"));
        } else if (intent.hasExtra(NOTIFICATION_ID_LABEL)) {
            int notificationId = intent.getIntExtra(NOTIFICATION_ID_LABEL, 0);
            String channelId = intent.getStringExtra(CHANNEL_ID_LABEL);
            String title = intent.getStringExtra(NOTIFICATION_TITLE_LABEL);
            String content = intent.getStringExtra(NOTIFICATION_CONTENT_LABEL);
            String smallIconName = intent.getStringExtra(NOTIFICATION_SMALL_ICON_NAME);

            Intent notificationActionIntent = new Intent(context, ResultActivity.class);
            notificationActionIntent.putExtra(NOTIFICATION_ID_LABEL, notificationId);
            notificationActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (intent.hasExtra(NOTIFICATION_URI_LABEL)) {
                notificationActionIntent.setData(Uri.parse(intent.getStringExtra(NOTIFICATION_URI_LABEL)));
            }

            Log.i(LOG_TAG, String.format("%s():: received notification id:'%d' - channel id:%s - title:'%s' - content:'%s' - small icon name:'%s",
                    "notify", notificationId, channelId, title, content, smallIconName));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationActionIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                Resources resources = context.getResources();
                @SuppressLint("DiscouragedApi") NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(resources.getIdentifier(smallIconName, ICON_RESOURCE_TYPE, context.getPackageName()))
                        /* TODO: large icon not working.  It needs to be tested again in future versions.
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
                            "notify", Manifest.permission.POST_NOTIFICATIONS));
                }
            } else {
                Log.w(LOG_TAG, String.format("%s():: unable to process notification as current SDK is %d and required SDK is %d",
                        "notify", Build.VERSION.SDK_INT, Build.VERSION_CODES.M));
            }
        } else {
            Log.e(LOG_TAG, String.format("%s():: %s extra not found in intent. Unable to generate notification.",
                    "notify", NOTIFICATION_ID_LABEL));
        }
    }
}
