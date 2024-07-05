package com.puutaro.commandclick.common.variable.broadcast.scheme

enum class BroadCastIntentSchemeForEdit(
    val action: String,
    val scheme: String
) {
    UPDATE_INDEX_LIST(
        "com.puutaro.commandclick.edit_frag.update_index_list",
        "update_index_list",
    ),
}