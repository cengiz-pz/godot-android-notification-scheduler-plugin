//
// © 2024-present https://github.com/cengiz-pz
//

package org.godotengine.plugin.android.notification.model;

import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.godotengine.godot.Dictionary;

public class ChannelData {

	private static String DATA_KEY_ID = "id";
	private static String DATA_KEY_NAME = "name";
	private static String DATA_KEY_DESCRIPTION = "description";
	private static String DATA_KEY_IMPORTANCE = "importance";
	private static String DATA_KEY_BADGE_ENABLED = "badge_enabled";

	private Dictionary data;

	public ChannelData(Dictionary data) {
		this.data = data;
	}

	public String getId() {
		return (String) data.get(DATA_KEY_ID);
	}

	public String getName() {
		return (String) data.get(DATA_KEY_NAME);
	}

	public String getDescription() {
		return (String) data.get(DATA_KEY_DESCRIPTION);
	}

	public int getImportance() {
		return (int) data.get(DATA_KEY_IMPORTANCE);
	}

	public boolean getBadgeEnabled() {
		return (boolean) data.get(DATA_KEY_BADGE_ENABLED);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
    public boolean validate() {
		if (data.containsKey(DATA_KEY_IMPORTANCE) == false) {
			data.put(DATA_KEY_IMPORTANCE, NotificationManager.IMPORTANCE_DEFAULT);
		}

		if (data.containsKey(DATA_KEY_BADGE_ENABLED) == false) {
			data.put(DATA_KEY_BADGE_ENABLED, true);
		}

		return data.containsKey(DATA_KEY_ID) &&
				data.containsKey(DATA_KEY_NAME) &&
				data.containsKey(DATA_KEY_DESCRIPTION);
	}

}
