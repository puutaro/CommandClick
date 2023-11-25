package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeForCmdIndex(
    val action: String,
    val scheme: String
) {
    UPDATE_FANNEL_LIST(
        "com.puutaro.commandclick.install.fannel",
        "install",
    )
}