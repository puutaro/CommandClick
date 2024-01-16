package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment


object ToolbarButtonToolForEdit {

    fun createInitButtonDisableMap() = mutableMapOf(
        ToolbarButtonBariantForEdit.HISTORY to false,
        ToolbarButtonBariantForEdit.OK to false,
        ToolbarButtonBariantForEdit.EDIT to false,
        ToolbarButtonBariantForEdit.SETTING to false,
    )

    fun createInitButtonIconMap() = mutableMapOf(
        ToolbarButtonBariantForEdit.HISTORY to R.drawable.icons8_history,
        ToolbarButtonBariantForEdit.OK to R.drawable.icons8_check_ok,
        ToolbarButtonBariantForEdit.EDIT to R.drawable.icons8_edit,
        ToolbarButtonBariantForEdit.SETTING to R.drawable.icons8_setting,
    )

    fun culcButtonWeight(
        editFragment: EditFragment,
    ): Float {
        return editFragment.toolBarButtonDisableMap.values.filter {
            !it
        }.size.let { 1.0F / it }
    }
}


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