//
// © 2024-present https://github.com/cengiz-pz
//

package org.godotengine.plugin.android.notification.model;

import android.content.Intent;

import org.godotengine.godot.Dictionary;


public class NotificationData {

	public static String DATA_KEY_ID = "notification_id";
	public static String DATA_KEY_CHANNEL_ID = "channel_id";
	public static String DATA_KEY_TITLE = "title";
	public static String DATA_KEY_CONTENT = "content";
	public static String DATA_KEY_SMALL_ICON_NAME = "small_icon_name";
	public static String DATA_KEY_DELAY = "delay";
	public static String DATA_KEY_DEEPLINK = "deeplink";
	public static String DATA_KEY_INTERVAL = "interval";
	public static String DATA_KEY_BADGE_COUNT= "badge_count";
	public static String OPTION_KEY_RESTART_APP = "restart_app";

	private Dictionary data;

	public NotificationData(Dictionary data) {
		this.data = data;
	}

	public NotificationData(Intent intent) {
		this.data = new Dictionary();
		if (intent.hasExtra(DATA_KEY_ID)) {
			data.put(DATA_KEY_ID, intent.getIntExtra(DATA_KEY_ID, -1));
		}
		if (intent.hasExtra(DATA_KEY_CHANNEL_ID)) {
			data.put(DATA_KEY_CHANNEL_ID, intent.getStringExtra(DATA_KEY_CHANNEL_ID));
		}
		if (intent.hasExtra(DATA_KEY_TITLE)) {
			data.put(DATA_KEY_TITLE, intent.getStringExtra(DATA_KEY_TITLE));
		}
		if (intent.hasExtra(DATA_KEY_CONTENT)) {
			data.put(DATA_KEY_CONTENT, intent.getStringExtra(DATA_KEY_CONTENT));
		}
		if (intent.hasExtra(DATA_KEY_SMALL_ICON_NAME)) {
			data.put(DATA_KEY_SMALL_ICON_NAME, intent.getStringExtra(DATA_KEY_SMALL_ICON_NAME));
		}
		if (intent.hasExtra(DATA_KEY_DELAY)) {
			data.put(DATA_KEY_DELAY, intent.getIntExtra(DATA_KEY_DELAY, -1));
		}
		if (intent.hasExtra(DATA_KEY_DEEPLINK)) {
			data.put(DATA_KEY_DEEPLINK, intent.getStringExtra(DATA_KEY_DEEPLINK));
		}
		if (intent.hasExtra(DATA_KEY_INTERVAL)) {
			data.put(DATA_KEY_INTERVAL, intent.getIntExtra(DATA_KEY_INTERVAL, -1));
		}
		if (intent.hasExtra(OPTION_KEY_RESTART_APP)) {
			data.put(OPTION_KEY_RESTART_APP, intent.getBooleanExtra(OPTION_KEY_RESTART_APP, true));
		}
	}

	public Integer getId() {
		return (Integer) data.get(DATA_KEY_ID);
	}

	public String getChannelId() {
		return (String) data.get(DATA_KEY_CHANNEL_ID);
	}

	public String getTitle() {
		return (String) data.get(DATA_KEY_TITLE);
	}

	public String getContent() {
		return (String) data.get(DATA_KEY_CONTENT);
	}

	public String getSmallIconName() {
		return (String) data.get(DATA_KEY_SMALL_ICON_NAME);
	}

	/**
	 * How many seconds from now to schedule first notification
	 */
	public Integer getDelay() {
		return (Integer) data.get(DATA_KEY_DELAY);
	}

	public boolean hasDeeplink() {
		return data.containsKey(DATA_KEY_DEEPLINK);
	}

	/**
	 * URI to process as app link when notification opened
	 */
	public String getDeeplink() {
		return (String) data.get(DATA_KEY_DEEPLINK);
	}

	public boolean hasInterval() {
		return data.containsKey(DATA_KEY_INTERVAL);
	}

	/**
	 * Interval in seconds between each repeating notification
	 */
	public Integer getInterval() {
		return (Integer) data.get(DATA_KEY_INTERVAL);
	}

	public Integer getBadgeCount() {
		return (data.containsKey(DATA_KEY_BADGE_COUNT)) ? (Integer) data.get(DATA_KEY_BADGE_COUNT) : (Integer) 0;
	}

	/**
	 * If enabled, app will be restarted when notification is opened
	 */
	public boolean hasRestartAppOption() {
		return data.containsKey(OPTION_KEY_RESTART_APP);
	}

	public boolean isValid() {
		return data.containsKey(DATA_KEY_ID) &&
				data.containsKey(DATA_KEY_CHANNEL_ID) &&
				data.containsKey(DATA_KEY_TITLE) &&
				data.containsKey(DATA_KEY_CONTENT) &&
				data.containsKey(DATA_KEY_SMALL_ICON_NAME) &&
				data.containsKey(DATA_KEY_DELAY);
	}

}
