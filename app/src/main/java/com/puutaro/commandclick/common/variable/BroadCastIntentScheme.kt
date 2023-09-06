package com.puutaro.commandclick.common.variable

enum class BroadCastIntentScheme(
    val action: String,
    val scheme: String
) {
    MONITOR_TEXT_PATH(
        "com.puutaro.commandclick.url.monitor_text_path",
        "monitor_text_path"
    ),
    ULR_LAUNCH(
        "com.puutaro.commandclick.url.launch",
        "url",
    ),
    HTML_LAUNCH(
    "com.puutaro.commandclick.html.launch",
    "edit_path",
    ),
    FZHTML_LAUNCH(
        "com.puutaro.commandclick.fzhtml.launch",
        "edit_path",
    ),
    STOP_GIT_CLONE(
    "com.puutaro.commandclick.git_clone_stop.launch",
    "stop",
    ),
    STOP_TEXT_TO_MP3(
        "com.puutaro.commandclick.text_to_mp3.launch",
        "stop",
    ),
    STOP_TEXT_TO_SPEECH(
        "com.puutaro.commandclick.text_to_speech.stop",
        "stop",
    ),
    PREVIOUS_TEXT_TO_SPEECH(
        "com.puutaro.commandclick.text_to_speech.previous",
        "previous",
    ),
    FROM_TEXT_TO_SPEECH(
        "com.puutaro.commandclick.text_to_speech.from",
        "from",
    ),
    NEXT_TEXT_TO_SPEECH(
        "com.puutaro.commandclick.text_to_speech.next",
        "next",
    ),
    TO_TEXT_TO_SPEECH(
        "com.puutaro.commandclick.text_to_speech.to",
        "to",
    ),
    STOP_PULSE_RECIEVER(
        "com.puutaro.commandclick.pulse_server.stop",
        "stop",
    ),
    RESTART_PULSE_RECIEVER(
        "com.puutaro.commandclick.pulse_server.restart",
        "restart",
    ),
}