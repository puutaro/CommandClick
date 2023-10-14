package com.puutaro.commandclick.common.variable.intent

enum class TextToSpeechIntentExtra(
    val scheme: String
) {
    listFilePath("list_file_path"),
    playMode("play_mode"),
    onRoop("on_roop"),
    playNumber("play_number"),
    transMode("english"),
    onTrack("on_track"),
    speed("speed"),
    pitch("pitch"),
    currentAppDirName("currentAppDirName"),
    scriptRawName("scriptRawName"),
}