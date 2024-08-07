package com.puutaro.commandclick.common.variable.broadcast.extra

enum class MusicPlayerIntentExtra(
    val scheme: String
) {
    IMPORTANCE("importance"),
    LIST_FILE_PATH("listFilePath"),
    PLAY_MODE("playMode"),
    ON_LOOP("onLoop"),
    PLAY_NUMBER("play_Number"),
    ON_TRACK("onTrack"),
    CURRENT_APP_DIR_NAME("currentAppDirName"),
    SCRIPT_RAW_NAME("scriptRawName"),
    SHELL_PATH("shellPath"),
    SHELL_ARGS("shellArgs"),
}

enum class MusicPlayerPlayExtra(
    val scheme: String
){
    SHELL_PATH("shellPath"),
    SHELL_ARGS("shellArgs"),
}