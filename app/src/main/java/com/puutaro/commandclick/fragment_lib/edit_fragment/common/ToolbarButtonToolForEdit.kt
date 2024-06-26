    package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment


object ToolbarButtonToolForEdit {

    fun createInitButtonDisableMap() = mutableMapOf(
        ToolbarButtonBariantForEdit.HISTORY to false,
        ToolbarButtonBariantForEdit.OK to false,
        ToolbarButtonBariantForEdit.EDIT to false,
        ToolbarButtonBariantForEdit.SETTING to false,
        ToolbarButtonBariantForEdit.EXTRA to false
    )

    fun createInitButtonIconMap() = mutableMapOf(
        ToolbarButtonBariantForEdit.HISTORY to R.drawable.icons8_history,
        ToolbarButtonBariantForEdit.OK to R.drawable.icons8_check_ok,
        ToolbarButtonBariantForEdit.EDIT to R.drawable.icons8_edit,
        ToolbarButtonBariantForEdit.SETTING to R.drawable.icons8_setting,
        ToolbarButtonBariantForEdit.EXTRA to R.drawable.icons8_setup,
    )

    fun culcButtonWeight(
        editFragment: EditFragment,
    ): Float {
        return editFragment.toolBarButtonVisibleMap.values.filter {
            it
        }.size.let { 1.0F / it }
    }
}


enum class ToolbarButtonBariantForEdit(
    val str: String,
) {
    CANCEL("cancel"),
    HISTORY("history"),
    OK("ok"),
    EDIT("edit"),
    SETTING("setting"),
    EXTRA("extra"),
}