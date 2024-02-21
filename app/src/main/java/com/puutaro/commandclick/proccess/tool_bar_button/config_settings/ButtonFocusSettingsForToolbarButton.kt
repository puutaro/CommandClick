package com.puutaro.commandclick.proccess.tool_bar_button.config_settings

import android.widget.ImageButton
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey

object ButtonFocusSettingsForToolbarButton {
    enum class ButtonFocusValue {
        ON,
    }

    fun set(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        toolbarButton: ImageButton?
    ){
        val isFocus = editFragment.toolbarButtonConfigMap?.get(
            toolbarButtonBariantForEdit
        )?.get(
            SettingButtonConfigMapKey.ON_FOCUS.key
        ) == ButtonFocusValue.ON.name
        if(!isFocus) return
        editFragment.context?.let {
            toolbarButton?.imageTintList =
                it.getColorStateList(R.color.web_icon_color)
        }
    }
}