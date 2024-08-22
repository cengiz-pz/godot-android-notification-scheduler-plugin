#
# © 2024-present https://github.com/cengiz-pz
#

@tool
class_name NotificationScheduler
extends Node

signal notification_opened(notification_id: int)
signal notification_dismissed(notification_id: int)
signal permission_granted(permission_name: String)
signal permission_denied(permission_name: String)

const NOTIFICATION_OPENED_SIGNAL_NAME = "notification_opened"
const NOTIFICATION_DISMISSED_SIGNAL_NAME = "notification_dismissed"
const PERMISSION_GRANTED_SIGNAL_NAME = "permission_granted"
const PERMISSION_DENIED_SIGNAL_NAME = "permission_denied"

const PLUGIN_SINGLETON_NAME: String = "@pluginName@"
const DEFAULT_NOTIFICATION_ID: int = -1

var _plugin_singleton: Object


func _ready() -> void:
	if _plugin_singleton == null:
		if Engine.has_singleton(PLUGIN_SINGLETON_NAME):
			_plugin_singleton = Engine.get_singleton(PLUGIN_SINGLETON_NAME)
			_connect_signals()
		else:
			printerr("%s singleton not found!" % PLUGIN_SINGLETON_NAME)


func _connect_signals() -> void:
	_plugin_singleton.connect(NOTIFICATION_OPENED_SIGNAL_NAME, _on_notification_opened)
	_plugin_singleton.connect(NOTIFICATION_DISMISSED_SIGNAL_NAME, _on_notification_dismissed)
	_plugin_singleton.connect(PERMISSION_GRANTED_SIGNAL_NAME, _on_permission_granted)
	_plugin_singleton.connect(PERMISSION_DENIED_SIGNAL_NAME, _on_permission_denied)


func create_notification_channel(a_notification_channel: NotificationChannel) -> void:
	if _plugin_singleton:
		_plugin_singleton.create_notification_channel(a_notification_channel.get_raw_data())
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)


func schedule(a_notification_data: NotificationData) -> void:
	if _plugin_singleton:
		_plugin_singleton.schedule(a_notification_data.get_raw_data())
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
		__result = _plugin_singleton.get_notification_id(a_default_value)
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)
	return __result


func has_post_notifications_permission() -> bool:
	var __result: bool = false
	if _plugin_singleton:
		__result = _plugin_singleton.has_post_notifications_permission()
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)
	return __result


func request_post_notifications_permission() -> void:
	if _plugin_singleton:
		_plugin_singleton.request_post_notifications_permission()
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)

func open_app_info_settings() -> void:
	if _plugin_singleton:
		_plugin_singleton.open_app_info_settings()
	else:
		printerr("%s singleton not initialized!" % PLUGIN_SINGLETON_NAME)

func _on_notification_opened(a_notification_id: int) -> void:
	notification_opened.emit(a_notification_id)


func _on_notification_dismissed(a_notification_id: int) -> void:
	notification_dismissed.emit(a_notification_id)


func _on_permission_granted(a_permission_name: String) -> void:
	permission_granted.emit(a_permission_name)


func _on_permission_denied(a_permission_name: String) -> void:
	permission_denied.emit(a_permission_name)
