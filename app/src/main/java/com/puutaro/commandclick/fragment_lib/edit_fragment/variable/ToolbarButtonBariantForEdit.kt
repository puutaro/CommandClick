package com.puutaro.commandclick.fragment_lib.edit_fragment.variable

import com.puutaro.commandclick.R


enum class ToolbarButtonBariantForEdit(
    val str: String,
    val drawbleIconInt: Int
) {
    CANCEL("edit_cancel", R.drawable.icons8_cancel),
    HISTORY("edit_history", R.drawable.icons8_history),
    OK("edit_ok", R.drawable.icons8_check_ok),
    EDIT("edit_edit", R.drawable.icons8_edit),
    SETTING("edit_setting", R.drawable.icons8_setting),
}