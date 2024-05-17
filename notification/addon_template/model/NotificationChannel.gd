#
# © 2024-present https://github.com/cengiz-pz
#

class_name NotificationChannel
extends RefCounted

const DATA_KEY_ID = "id"
const DATA_KEY_NAME = "name"
const DATA_KEY_DESCRIPTION = "description"

var _data: Dictionary


func _init() -> void:
	_data = {}


func set_id(a_id: String) -> NotificationChannel:
	_data[DATA_KEY_ID] = a_id
	return self


func set_name(a_name: String) -> NotificationChannel:
	_data[DATA_KEY_NAME] = a_name
	return self


func set_description(a_description: String) -> NotificationChannel:
	_data[DATA_KEY_DESCRIPTION] = a_description
	return self


func get_raw_data() -> Dictionary:
	return _data
