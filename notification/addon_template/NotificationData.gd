class_name NotificationData
extends Object

const DATA_KEY_ID = "id"
const DATA_KEY_CHANNEL_ID = "channel_id"
const DATA_KEY_TITLE = "title"
const DATA_KEY_CONTENT = "content"
const DATA_KEY_DEEPLINK = "deeplink"
const DATA_KEY_SMALL_ICON_NAME = "small_icon_name"

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


func set_deeplink(a_deeplink: String) -> NotificationData:
	_data[DATA_KEY_DEEPLINK] = a_deeplink
	return self


func set_small_icon_name(a_small_icon_name: String) -> NotificationData:
	_data[DATA_KEY_SMALL_ICON_NAME] = a_small_icon_name
	return self


func get_raw_data() -> Dictionary:
	return _data
