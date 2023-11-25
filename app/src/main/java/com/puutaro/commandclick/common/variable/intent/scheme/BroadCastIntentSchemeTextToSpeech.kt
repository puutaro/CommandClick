package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeTextToSpeech(
    val action: String,
    val scheme: String
) {
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
}