extends Node

const NOTIFICATION_ICON_NAME: String = "ic_demo_notification"
const NOTIFICATION_CHANNEL_ID: String = "my_channel_id"
const NOTIFICATION_CHANNEL_NAME: String = "My Demo Channel"

@onready var notification_scheduler: NotificationScheduler = $NotificationScheduler as NotificationScheduler
@onready var _label: RichTextLabel = $CanvasLayer/CenterContainer/VBoxContainer/RichTextLabel as RichTextLabel
@onready var _delay_slider: HSlider = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/HBoxContainer/HSlider as HSlider


func _ready() -> void:
	notification_scheduler.create_notification_channel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, "")


func _on_button_pressed() -> void:
	var __notification_data = NotificationData.new()

	_print_to_screen("Scheduling notification with a delay of %d seconds" % int(_delay_slider.value))
	notification_scheduler.schedule(__notification_data, _delay_slider.value)


func _print_to_screen(a_message: String, a_is_error: bool = false) -> void:
	_label.add_text("%s\n\n" % a_message)
	if a_is_error:
		printerr(a_message)
	else:
		print(a_message)
