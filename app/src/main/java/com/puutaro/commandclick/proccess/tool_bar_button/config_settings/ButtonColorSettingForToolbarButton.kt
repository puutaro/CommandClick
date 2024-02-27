package com.puutaro.commandclick.proccess.tool_bar_button.config_settings

import android.widget.ImageButton
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey

object ButtonColorSettingForToolbarButton {

    fun set(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        toolbarButton: ImageButton?
    ){
        val context = editFragment.context
            ?: return
        if(
            toolbarButton == null
        ) return
        val colorId = editFragment.toolbarButtonConfigMap?.get(
            toolbarButtonBariantForEdit
        )?.get(
            SettingButtonConfigMapKey.COLOR.key
        ).let {
            colorStr ->
            CmdClickColor.values().firstOrNull {
                it.str == colorStr
            }?.id ?: CmdClickColor.DARK_GREEN.id
        }
        toolbarButton.imageTintList = context.getColorStateList(colorId)
    }
}