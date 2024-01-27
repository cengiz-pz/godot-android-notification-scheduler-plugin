package org.godotengine.plugin.android.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends IntentService {
    private static final String LOG_TAG = "godot::" + NotificationService.class.getSimpleName();

    private static final String ICON_RESOURCE_TYPE = "drawable";

    static final String NOTIFICATION_SERVICE_NAME = "GODOT_NOTIFICATION_SERVICE";
    static final String NOTIFICATION_ID_LABEL = "GODOT_NOTIFICATION_ID";
    static final String CHANNEL_ID_LABEL = "GODOT_CHANNEL_ID";
    static final String NOTIFICATION_TITLE_LABEL = "GODOT_NOTIFICATION_TITLE";
    static final String NOTIFICATION_CONTENT_LABEL = "GODOT_NOTIFICATION_CONTENT";
    static final String NOTIFICATION_URI_LABEL = "GODOT_NOTIFICATION_URI";
    static final String NOTIFICATION_SMALL_ICON_NAME = "GODOT_NOTIFICATION_SMALL_ICON_NAME";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * Parameter to super(), name, is used to name the worker thread, important only for debugging.
     */
    public NotificationService() {
        super(NOTIFICATION_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent receivedIntent) {
        if (receivedIntent == null) {
            Log.e(LOG_TAG, String.format("%s():: Received intent is null. Unable to generate notification.",
                    "onHandleIntent"));
        } else if (receivedIntent.hasExtra(NOTIFICATION_ID_LABEL)) {
            int notificationId = receivedIntent.getIntExtra(NOTIFICATION_ID_LABEL, 0);
            String channelId = receivedIntent.getStringExtra(CHANNEL_ID_LABEL);
            String title = receivedIntent.getStringExtra(NOTIFICATION_TITLE_LABEL);
            String content = receivedIntent.getStringExtra(NOTIFICATION_CONTENT_LABEL);
            String smallIconName = receivedIntent.getStringExtra(NOTIFICATION_SMALL_ICON_NAME);

            Context context = getApplicationContext();
            Intent notificationActionIntent = new Intent(context, ResultActivity.class);
            notificationActionIntent.putExtra(NOTIFICATION_ID_LABEL, notificationId);
            notificationActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (receivedIntent.hasExtra(NOTIFICATION_URI_LABEL)) {
                notificationActionIntent.setData(Uri.parse(receivedIntent.getStringExtra(NOTIFICATION_URI_LABEL)));
            }

            Log.i(LOG_TAG, String.format("%s():: received notification id:'%d' - channel id:%s - title:'%s' - content:'%s' - small icon name:'%s",
                    "onHandleIntent", notificationId, channelId, title, content, smallIconName));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notify(context, notificationActionIntent, notificationId, channelId, title, content, smallIconName);
            } else {
                Log.w(LOG_TAG, String.format("%s():: unable to process notification as current SDK is %d and required SDK is %d",
                        "onHandleIntent", Build.VERSION.SDK_INT, Build.VERSION_CODES.M));
            }
        } else {
            Log.e(LOG_TAG, String.format("%s():: %s extra not found in intent. Unable to generate notification.",
                    "onHandleIntent", NOTIFICATION_ID_LABEL.toString()));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void notify(Context context, Intent intent, int notificationId, String channelId, String title, String content, String smallIconName) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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
            NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
        } else {
            Log.w(LOG_TAG, String.format("%s():: unable to process notification as %s permission is not granted",
                    "notify", Manifest.permission.POST_NOTIFICATIONS));
        }
    }
}
