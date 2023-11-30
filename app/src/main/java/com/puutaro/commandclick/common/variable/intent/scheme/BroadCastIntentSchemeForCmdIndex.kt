package com.puutaro.commandclick.common.variable.intent.scheme

enum class BroadCastIntentSchemeForCmdIndex(
    val action: String,
    val scheme: String
) {
    UPDATE_FANNEL_LIST(
        "com.puutaro.commandclick.cmd_index.install_fannel",
        "install_fannel",
    ),
    UPDATE_INDEX_FANNEL_LIST(
    "com.puutaro.commandclick.cmd_index.update_index_fannel_list",
    "update_index_fannel_list",
    )
}