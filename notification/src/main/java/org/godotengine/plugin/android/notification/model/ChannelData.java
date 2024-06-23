//
// © 2024-present https://github.com/cengiz-pz
//

package org.godotengine.plugin.android.notification.model;

import org.godotengine.godot.Dictionary;

public class ChannelData {

	private static String DATA_KEY_ID = "id";
	private static String DATA_KEY_NAME = "name";
	private static String DATA_KEY_DESCRIPTION = "description";
	private static String DATA_KEY_IMPORTANCE = "importance";

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

	public boolean isValid() {
		return data.containsKey(DATA_KEY_ID) &&
				data.containsKey(DATA_KEY_NAME) &&
				data.containsKey(DATA_KEY_DESCRIPTION) &&
				data.containsKey(DATA_KEY_IMPORTANCE);
	}

}
