package org.godotengine.plugin.android.notification;

import static org.godotengine.plugin.android.notification.NotificationConstants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ResultActivity extends AppCompatActivity {
    private static final String LOG_TAG = "godot::" + ResultActivity.class.getSimpleName();

    private static final String GODOT_APP_MAIN_ACTIVITY_CLASSPATH = "com.godot.game.GodotApp";
    private static Class<?> godotAppMainActivityClass = null;

    static {
        try {
            godotAppMainActivityClass = Class.forName(GODOT_APP_MAIN_ACTIVITY_CLASSPATH);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "could not find " + GODOT_APP_MAIN_ACTIVITY_CLASSPATH);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent godotIntent = new Intent(getApplicationContext(), godotAppMainActivityClass);
        godotIntent.putExtras(getIntent());
        godotIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.i(LOG_TAG, "Starting activity with intent: " + godotIntent);
        startActivity(godotIntent);

        Bundle bundle = getIntent().getExtras();
        if (GodotAndroidNotificationSchedulerPlugin.instance != null && bundle != null && bundle.containsKey(NOTIFICATION_ID_LABEL)) {
            // TODO: Handle in Godot app (check data on app resume/restart)
            GodotAndroidNotificationSchedulerPlugin.instance.handleNotificationOpened(bundle.getInt(NOTIFICATION_ID_LABEL));
        } else {
            Log.w(LOG_TAG, "Ignoring notification.  Reason: " + (GodotAndroidNotificationSchedulerPlugin.instance == null ? "instance null" : bundle == null ? "bundle null" : "bundle empty"));
        }
    }
}