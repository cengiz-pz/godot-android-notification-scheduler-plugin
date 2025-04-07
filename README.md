
---
# ![](notification/addon_template/icon.png?raw=true) Notification Scheduler Plugin
Notification Scheduler Plugin allows scheduling of local notifications on the Android platform.

_For iOS version, visit https://github.com/cengiz-pz/godot-ios-notification-scheduler-plugin ._

## ![](notification/addon_template/icon.png?raw=true) Prerequisites
Follow instructions on the following page to create a custom Android gradle build
- [Create custom Android gradle build](https://docs.godotengine.org/en/stable/tutorials/export/android_gradle_build.html)

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
	- keep `Change Install Folder` setting pointing to your project's root directory
	- keep `Ignore asset root` checkbox checked
	- click `Install` button
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

### ![](notification/addon_template/icon.png?raw=true) Installing manually
Steps:
- download release archive from Github
- unzip the release archive
- copy to your Godot project's root directory
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

## ![](notification/addon_template/icon.png?raw=true) Notification icon
Copy your notification icon to your Godot project's `android/build/res` directory.

Alternatively, you could use `Android Studio`'s `Image Asset Studio` to generate your icon set.

_Note: the notification icon resource should be of type `drawable`_

### ![](notification/addon_template/icon.png?raw=true) Generating notification icons using Android Studio

Steps:

- Open your Godot project's `android/build` directory in Android Studio
- Wait for it to fully load
- Right click on `res` folder and select `New -> Image Asset` from the context menu
- On the `Asset Studio` wizard
	- Set `Icon type` to `Notification Icons`
	- Enter a the name of your notification icon (ie. `ic_my_notification`)
	- Configure your icon
	- Click `Next` button
	- Click `Finish` button to confirm
- The following new directories should have been created under your Godot project's `android/build/res` directory:
	- `drawable-anydpi`
	- `drawable-xxhdpi`
	- `drawable-xhdpi`
	- `drawable-hdpi`
	- `drawable-mdpi`
- Make sure you use the name you specified for your icons when initializing notifications:
	- `my_notification_data.set_small_icon_name("<your_notification_icon_name_here>")`


## ![](notification/addon_template/icon.png?raw=true) Usage
Add a `NotificationScheduler` node to your scene and follow the following steps:
- Register listeners for the following signals emitted from the `NotificationScheduler` node
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
> On Android, apps that target Android 13 or higher can ask for notification permission as many times as they want until the user explicitly denies the permission twice. If the user targets Android 12 or lower, the app can ask for permission as many times as it wants until the user denies the permission once. If the user denies the permission twice, the app can't ask again unless the user reinstalls the app
- After user has denied the request, you can ask to turn on notification permission manually and send them to App_Info screen using the `NotificationScheduler` node:(Best Practice: Don't promt users automatically, insted keep a button in settings to toggle notifications)
```
	$NotificationScheduler.open_app_info_settings()
```
- Create a notification channel using the `NotificationScheduler` node:
```
	$NotificationScheduler.create_notification_channel(
		NotificationChannel.new()
			.set_id("my_channel_id")
			.set_name("My Channel Name")
			.set_description("My channel description")
			.set_importance(NotificationChannel.Importance.DEFAULT))
```
- Build `NotificationData` object:
```
	var my_notification_data = NotificationData.new()
	my_notification_data.set_id(__notification_id).\
			set_channel_id("my_channel_id").\
			set_title("My Notification Title").\
			set_content("My notification content").\
			set_small_icon_name("ic_name_of_the_icon_that_you_generated").\
			set_delay(my_delay_in_seconds)
```
- Schedule notification using the `NotificationScheduler` node:
```
	$NotificationScheduler.schedule(
			my_notification_data
		)
```
- _`NotificationData`'s `set_interval(interval_in_seconds)` method can be used for scheduling repeating notifications._
- _`NotificationData`'s `set_deeplink(data)` method can be used for delivering URI data along with the notification._
	- _The [Deeplink Plugin](https://github.com/cengiz-pz/godot-android-deeplink-plugin) can then be used to process the URI data._

### ![](notification/addon_template/icon.png?raw=true) Other Available Methods
- `cancel(notification_id)`
- `get_notification_id()` - alternative way to get the ID of the last opened notification.

### ![](notification/addon_template/icon.png?raw=true) Demo
- Restore the following notification icons after `Android Build Template` is installed for the demo app.
	- `demo/android/build/res/drawable-anydpi-v24/ic_demo_notification.xml`
	- `demo/android/build/res/drawable-hdpi/ic_demo_notification.png`
	- `demo/android/build/res/drawable-mdpi/ic_demo_notification.png`
	- `demo/android/build/res/drawable-xhdpi/ic_demo_notification.png`
	- `demo/android/build/res/drawable-xxhdpi/ic_demo_notification.png`

## ![](notification/addon_template/icon.png?raw=true) Troubleshooting

### ADB logcat
`adb logcat` is one of the best tools for troubleshooting unexpected behavior
- use `$> adb logcat | grep 'godot'` on Linux
	- `adb logcat *:W` to see warnings and errors
	- `adb logcat *:E` to see only errors
	- `adb logcat | grep 'godot|somethingElse'` to filter using more than one string at the same time
- use `#> adb.exe logcat | select-string "godot"` on powershell (Windows)


### No small icon found for notification
For example, there will be an error similar to the following visible in `adb logcat` if small icon resource files have not been added to the project.

```
 E  FATAL EXCEPTION: main
 Process: org.godotengine.notification:godot_notification_receiver, PID: 12214
 java.lang.RuntimeException: Unable to start receiver org.godotengine.plugin.android
 notification.NotificationReceiver: java.lang.IllegalArgumentException: Invalid notification (no valid small icon)
```

Also check out:
https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#troubleshooting


### Android app settings
Some Android OS flavors will automatically adjust applications' power consumption settings.  Messages may not be delivered when this setting is set to "optimized" or "restricted," especially if the app is closed.

Check your application's power consumption settings at:

```
Settings -> Apps -> Your App -> Battery
```

### Other troubleshooting sources
Also check out:
https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#troubleshooting

<br/><br/><br/>

---
# ![](notification/addon_template/icon.png?raw=true) Credits
Developed by [Cengiz](https://github.com/cengiz-pz)

Original repository: [Godot Android Notification Scheduler Plugin](https://github.com/cengiz-pz/godot-android-notification-scheduler-plugin)

<br/><br/><br/>

---
# ![](notification/addon_template/icon.png?raw=true) All Plugins

| Plugin | Android | iOS |
| :---: | :--- | :--- |
| Notification Scheduler | https://github.com/cengiz-pz/godot-android-notification-scheduler-plugin | https://github.com/cengiz-pz/godot-ios-notification-scheduler-plugin |
| Admob | https://github.com/cengiz-pz/godot-android-admob-plugin | https://github.com/cengiz-pz/godot-ios-admob-plugin |
| Deeplink | https://github.com/cengiz-pz/godot-android-deeplink-plugin | https://github.com/cengiz-pz/godot-ios-deeplink-plugin |
| Share | https://github.com/cengiz-pz/godot-android-share-plugin | https://github.com/cengiz-pz/godot-ios-share-plugin |
| In-App Review | https://github.com/cengiz-pz/godot-android-inapp-review-plugin | https://github.com/cengiz-pz/godot-ios-inapp-review-plugin |
