package com.puutaro.commandclick.common.variable.intent.extra

enum class TextToSpeechIntentExtra(
    val scheme: String
) {
    importance("importance"),
    listFilePath("list_file_path"),
    playMode("play_mode"),
    onRoop("on_roop"),
    playNumber("play_number"),
    transMode("english"),
    onTrack("on_track"),
    speed("speed"),
    pitch("pitch"),
    currentAppDirName("current_app_dir_name"),
    scriptRawName("script_raw_name"),
    shellPath("shell_path")
}