#
# © 2024-present https://github.com/cengiz-pz
#

extends Node

const NOTIFICATION_ICON_NAME: String = "ic_demo_notification"

@export_category("Notification Channel")
@export var channel_id: String = "my_channel_id"
@export var channel_name: String = "My Demo Channel"
@export var channel_description: String = "My Channel Description"
@export var channel_importance: NotificationChannel.Importance = NotificationChannel.Importance.DEFAULT

@export_category("Notification Content")
@export var notification_title: String = "Godot Notification Scheduler Demo"
@export var notification_text: String = "This is a demo notification. Have you received it?"

@onready var notification_scheduler: NotificationScheduler = $NotificationScheduler as NotificationScheduler
@onready var _label: RichTextLabel = $CanvasLayer/CenterContainer/VBoxContainer/RichTextLabel as RichTextLabel
@onready var _delay_slider: HSlider = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/HBoxContainer/DelayHSlider as HSlider
@onready var _delay_value_label: Label = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/HBoxContainer/ValueLabel as Label
@onready var _interval_checkbox: CheckBox = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/IntervalHBoxContainer/IntervalCheckBox as CheckBox
@onready var _interval_slider: HSlider = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/IntervalHBoxContainer/IntervalHSlider as HSlider
@onready var _interval_value_label: Label = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/IntervalHBoxContainer/ValueLabel as Label
@onready var _permission_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/PermissionButton as Button
@onready var _restart_checkbox: CheckBox = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/RestartCheckBox as CheckBox

var _notification_id: int = 0


func _ready() -> void:
	_delay_value_label.text = str(int(_delay_slider.value))
	_interval_value_label.text = str(int(_interval_slider.value))

	if notification_scheduler.has_post_notifications_permission():
		notification_scheduler.create_notification_channel(
			NotificationChannel.new()
					.set_id(channel_id)
					.set_name(channel_name)
					.set_description(channel_description)
					.set_importance(channel_importance))
	else:
		_permission_button.disabled = false
		_print_to_screen("App does not have required notification permissions!")



func _on_button_pressed() -> void:
	var __notification_data = NotificationData.new()\
			.set_id(_get_next_notification_id())\
			.set_channel_id(channel_id)\
			.set_title(notification_title)\
			.set_content(notification_text)\
			.set_small_icon_name(NOTIFICATION_ICON_NAME)\
			.set_delay(_delay_slider.value)

	if _interval_checkbox.button_pressed:
		__notification_data.set_interval(_interval_slider.value)

	if _restart_checkbox.button_pressed:
		__notification_data.set_restart_app_option()

	_print_to_screen("Scheduling notification with a delay of %d seconds" % int(_delay_slider.value))

	notification_scheduler.schedule(__notification_data)


func _get_next_notification_id() -> int:
	_notification_id += 1
	return _notification_id


func _print_to_screen(a_message: String, a_is_error: bool = false) -> void:
	_label.add_text("%s\n\n" % a_message)
	if a_is_error:
		printerr(a_message)
	else:
		print(a_message)


func _on_delay_h_slider_value_changed(value: float) -> void:
	_delay_value_label.text = str(int(value))


func _on_interval_h_slider_value_changed(value: float) -> void:
	_interval_value_label.text = str(int(value))


func _on_permission_button_pressed() -> void:
	_permission_button.disabled = true
	notification_scheduler.request_post_notifications_permission()


func _on_notification_scheduler_permission_granted(permission_name: String) -> void:
	_print_to_screen("%s permission granted" % permission_name)

	notification_scheduler.create_notification_channel(
		NotificationChannel.new()
				.set_id(channel_id)
				.set_name(channel_name)
				.set_description(channel_description)
				.set_importance(channel_importance))


func _on_notification_scheduler_permission_denied(permission_name: String) -> void:
	_print_to_screen("%s permission denied" % permission_name)


func _on_notification_scheduler_notification_opened(notification_id: int) -> void:
	_print_to_screen("Notification %d opened" % notification_id)
