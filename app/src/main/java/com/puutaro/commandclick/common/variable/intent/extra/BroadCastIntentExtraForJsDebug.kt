package com.puutaro.commandclick.common.variable.intent.extra


object BroadCastIntentExtraForJsDebug {
    enum class BroadcastSchema(
        val scheme: String
    ) {
        NOTI_LEVEL("debug_level"),
        DEBUG_GENRE("debug_type"),
        DATETIME("datetime"),
    }

    enum class NotiLevelType(
        val level: String
    ){
        LOW("low"),
        HIGH("high"),
    }

    private val clickBellowButtonMsg = "click bellow button"

    enum class DebugGenre(
        val type: String,
        val label: String,
        val buttonName: String,
    ){
        JS_ERR("js","JS ERR", "jsLog"),
        SYS_ERR("sys","SYS ERR", "sysLog"),
        ERR("err","ERR", clickBellowButtonMsg),
        JS_DEBUG("debug","DEBUG", clickBellowButtonMsg),
    }
}

