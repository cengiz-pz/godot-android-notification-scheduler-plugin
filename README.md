# ![](notification/addon_template/icon.png?raw=true) Notification Scheduler Plugin
Notification Scheduler Plugin allows scheduling of local notifications on the Android platform.

## ![](notification/addon_template/icon.png?raw=true) Prerequisites
Follow instructions on the following page to create a custom Android build
- [Create custom Android build](https://docs.godotengine.org/en/stable/tutorials/export/android_custom_build.html)

Upgrade your target Android SDK version to 33 via `Project->Export...->Android->Target SDK`

Prior to using this plugin, a notification icon should be generated. For instructions please visit the following link:
- https://developer.android.com/studio/write/create-app-icons#notification

## ![](notification/addon_template/icon.png?raw=true) Installation
There are 2 ways to install the `Notification Scheduler` plugin into your project:
- Through the Godot Editor's AssetLib
- Manually by downloading archives from Github

### ![](notification/addon_template/icon.png?raw=true) Installing via AssetLib
Steps:
- search for and select the `Android Notification Scheduler` plugin in Godot Editor
- click `Download` button
- on the installation dialog...
  - click `Change Install Folder` button and select your project's `addons` directory
  - uncheck `Ignore asset root` checkbox
  - click `Install` button
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

### ![](notification/addon_template/icon.png?raw=true) Installing manually
Steps:
- download release archive from Github
- unzip the release archive
- copy to your Godot project's `addons` directory
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

## ![](notification/addon_template/icon.png?raw=true) Notification icon
Copy your notification icon to your project's `android/build/res` directory.

Alternatively, you could use `Android Studio`'s `Image Asset Studio` to generate your icon set.

_Note: the notification icon resource should be of type `drawable`_

## ![](notification/addon_template/icon.png?raw=true) Usage
Add a `NotificationScheduler` node to your scene and follow the following steps:
- register listeners for the following signals emitted from the `NotificationScheduler` node
    - `notification_opened` - when user taps notification item
    - `permission_granted`
	- `permission_denied`
- At startup, using the `NotificationScheduler` node to check that the application has permissions to post notifications:
```
	$NotificationScheduler.has_post_notifications_permission()
```
- If the application doesn't have permissions to post notifications, then request permission using the `NotificationScheduler` node:
```
	$NotificationScheduler.request_post_notifications_permission()
```
- `permission_granted` signal will be emitted when the application receives the permissions

- Create a notification channel using the `NotificationScheduler` node:
```
	$NotificationScheduler.create_notification_channel("my_channel_id", "My Channel Name", "My channel description")
```
- Build `NotificationData` object:
```
	var my_notification_data = NotificationData.new()
	my_notification_data.set_id(__notification_id).\
			set_channel_id("my_channel_id").\
			set_title("My Notification Title").\
			set_content("My notification content").\
			set_small_icon_name("ic_name_of_the_icon_that_you_generated")
```
- Schedule notification using the `NotificationScheduler` node:
```
	$NotificationScheduler.schedule(
			my_notification_data,
			my_delay_in_seconds
		)
```

### ![](notification/addon_template/icon.png?raw=true) Other Available Methods
- `schedule_repeating(notification_data, delay, interval)`
- `schedule_with_deeplink(notification_data, delay)`
    - use `NotificationData`'s `set_deeplink()` method to set the deeplink value
- `schedule_repeating_with_deeplink(notification_data, delay, interval)`
    - use `NotificationData`'s `set_deeplink()` method to set the deeplink value
- `cancel(notification_id)`

## ![](notification/addon_template/icon.png?raw=true) Troubleshooting
`adb logcat` is one of the best tools for troubleshooting unexpected behavior
- use `$> adb logcat | grep 'godot'` on Linux
	- `adb logcat *:W` to see warnings and errors
	- `adb logcat *:E` to see only errors
	- `adb logcat | grep 'godot|somethingElse'` to filter using more than one string at the same time
- use `#> adb.exe logcat | select-string "godot"` on powershell (Windows)

Also check out:
https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#troubleshooting
