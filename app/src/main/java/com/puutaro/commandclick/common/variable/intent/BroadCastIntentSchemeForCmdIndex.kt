package com.puutaro.commandclick.common.variable.intent

enum class BroadCastIntentSchemeForCmdIndex(
    val action: String,
    val scheme: String
) {
    UPDATE_FANNEL_LIST(
        "com.puutaro.commandclick.install.fannel",
        "install",
    )
}