package com.puutaro.commandclick.common.variable.broadcast.extra

enum class ErrLogExtraForTerm (
    val schema: String
) {
// errContents: String,
//        debugNotiJanre: String = BroadCastIntentExtraForJsDebug.DebugGenre.SYS_ERR.type,
//        notiLevelSrc: String = BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH.level
    ERR_CONTENTS("err_contents"),
    DEBUG_NOTI_JANRE("err_contents"),
    NOTI_LEVEL("noti_level"),
}