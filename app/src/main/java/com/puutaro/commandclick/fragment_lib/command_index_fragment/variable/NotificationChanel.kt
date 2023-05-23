package com.puutaro.commandclick.fragment_lib.command_index_fragment.variable

enum class NotificationChanel(
    val id: String,
    val cname: String,
) {
    GIT_CLONE_NOTIFICATION(
        "channel1",
        "clone"
    ),
    TEXT_TO_SPEECH_NOTIFICATION(
    "channel_text_to_speech",
    "text_to_speech"
    ),
    TEXT_TO_MP3_NOTIFICATION(
    "channel_text_to_mp3",
    "text_to_mp3"
    )
}