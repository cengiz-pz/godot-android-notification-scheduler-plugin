@tool
class_name NotificationScheduler
extends Node

signal notification_opened(notification_id)
signal permission_granted(permission_name)
signal permission_denied(permission_name)

const NOTIFICATION_OPENED_SIGNAL_NAME = "notification_opened"
const PERMISSION_GRANTED_SIGNAL_NAME = "permission_granted"
const PERMISSION_DENIED_SIGNAL_NAME = "permission_denied"

const PLUGIN_SINGLETON_NAME: String = "@pluginName@"
const DEFAULT_NOTIFICATION_ID: int = -1

var _plugin_singleton: Object


func init() -> void:
	if _plugin_singleton == null:
		if Engine.has_singleton(PLUGIN_SINGLETON_NAME):
			_plugin_singleton = Engine.get_singleton(PLUGIN_SINGLETON_NAME)
			_connect_signals()
		else:
			printerr("%s singleton not found!" % PLUGIN_SINGLETON_NAME)


func _connect_signals() -> void:
	_plugin_singleton.connect(NOTIFICATION_OPENED_SIGNAL_NAME, _on_notification_opened)
	_plugin_singleton.connect(PERMISSION_GRANTED_SIGNAL_NAME, _on_permission_granted)
	_plugin_singleton.connect(PERMISSION_DENIED_SIGNAL_NAME, _on_permission_denied)


func create_notification_channel(a_id: String, a_name: String, a_description: String) -> void:
	if _plugin_singleton:
		_plugin_singleton.createNotificationChannel(a_id, a_name, a_description)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func schedule(a_notification_data: NotificationData, a_delay_seconds: int) -> void:
	if _plugin_singleton:
		_plugin_singleton.schedule(a_notification_data.get_raw_data(), a_delay_seconds)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func schedule_repeating(a_notification_data: NotificationData, a_delay_seconds: int, a_interval_seconds: int) -> void:
	if _plugin_singleton:
		_plugin_singleton.scheduleRepeating(a_notification_data.get_raw_data(), a_delay_seconds, a_interval_seconds)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func schedule_with_deeplink(a_notification_data: NotificationData, a_delay_seconds: int) -> void:
	if _plugin_singleton:
		_plugin_singleton.scheduleWithDeeplink(a_notification_data.get_raw_data(), a_delay_seconds)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func schedule_repeating_with_deeplink(a_notification_data: NotificationData, a_delay_seconds: int, a_interval_seconds: int) -> void:
	if _plugin_singleton:
		_plugin_singleton.scheduleRepeatingWithDeeplink(a_notification_data.get_raw_data(), a_delay_seconds, a_interval_seconds)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func cancel(a_notification_id: int) -> void:
	if _plugin_singleton:
		_plugin_singleton.cancel(a_notification_id)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func get_notification_id(a_default_value: int = DEFAULT_NOTIFICATION_ID) -> int:
	var __result: int = a_default_value
	if _plugin_singleton:
		__result = _plugin_singleton.getNotificationId(a_default_value)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)
	return __result


func has_post_notifications_permission() -> bool:
	var __result: bool = false
	if _plugin_singleton:
		__result = _plugin_singleton.hasPostNotificationsPermission()
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)
	return __result


func request_post_notifications_permission() -> void:
	if _plugin_singleton:
		_plugin_singleton.requestPostNotificationsPermission()
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func _on_notification_opened(a_notification_id: int) -> void:
	emit_signal(NOTIFICATION_OPENED_SIGNAL_NAME, a_notification_id)


func _on_permission_granted(a_permission_name: String) -> void:
	emit_signal(PERMISSION_GRANTED_SIGNAL_NAME, a_permission_name)


func _on_permission_denied(a_permission_name: String) -> void:
	emit_signal(PERMISSION_DENIED_SIGNAL_NAME, a_permission_name)
