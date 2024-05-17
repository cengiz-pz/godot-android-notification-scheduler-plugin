#
# © 2024-present https://github.com/cengiz-pz
#

class_name NotificationData
extends RefCounted

const DATA_KEY_ID = "notification_id"
const DATA_KEY_CHANNEL_ID = "channel_id"
const DATA_KEY_TITLE = "title"
const DATA_KEY_CONTENT = "content"
const DATA_KEY_SMALL_ICON_NAME = "small_icon_name"
const DATA_KEY_DELAY = "delay"
const DATA_KEY_DEEPLINK = "deeplink"
const DATA_KEY_INTERVAL = "interval"
const OPTION_KEY_RESTART_APP = "restart_app"

var _data: Dictionary


func _init() -> void:
	_data = {}


func set_id(a_id: int) -> NotificationData:
	_data[DATA_KEY_ID] = a_id
	return self


func set_channel_id(a_channel_id: String) -> NotificationData:
	_data[DATA_KEY_CHANNEL_ID] = a_channel_id
	return self


func set_title(a_title: String) -> NotificationData:
	_data[DATA_KEY_TITLE] = a_title
	return self


func set_content(a_content: String) -> NotificationData:
	_data[DATA_KEY_CONTENT] = a_content
	return self


func set_small_icon_name(a_small_icon_name: String) -> NotificationData:
	_data[DATA_KEY_SMALL_ICON_NAME] = a_small_icon_name
	return self


func set_delay(a_delay: int) -> NotificationData:
	_data[DATA_KEY_DELAY] = a_delay
	return self


func set_deeplink(a_deeplink: String) -> NotificationData:
	_data[DATA_KEY_DEEPLINK] = a_deeplink
	return self


func set_interval(a_interval: int) -> NotificationData:
	_data[DATA_KEY_INTERVAL] = a_interval
	return self


func set_restart_app_option() -> NotificationData:
	_data[OPTION_KEY_RESTART_APP] = true
	return self


func get_raw_data() -> Dictionary:
	return _data
