package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeMusicPlayer(
    val action: String,
    val scheme: String
) {
    NOTI_UPDATE(
        "com.puutaro.commandclick.music_player.noti_update",
        "uriTitle",
    ),
    PLAY_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.play",
        "play_uri_index",
    ),
    SEEK_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.seek",
        "seek_posi",
    ),
    DESTROY_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.release",
        "release"
    ),
    PUASE_OR_REPLAY_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.stop",
        "pause_or_replay",
    ),
    PREVIOUS_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.previous",
        "previous",
    ),
    FROM_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.from",
        "from",
    ),
    NEXT_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.next",
        "next",
    ),
    TO_MUSIC_PLAYER(
        "com.puutaro.commandclick.music_player.to",
        "to",
    ),
}