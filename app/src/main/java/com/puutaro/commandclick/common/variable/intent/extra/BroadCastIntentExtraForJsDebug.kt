package com.puutaro.commandclick.common.variable.intent.extra


object BroadCastIntentExtraForJsDebug {
    enum class BroadcastSchema(
        val scheme: String
    ) {
        DEBUG_LEVEL("debug_level"),
    }

    enum class DebugLevelType(
        val level: String
    ){
        LOW("low"),
        HIGH("high"),
    }
}

