package com.puutaro.commandclick.proccess.tool_bar_button.config_settings

import android.widget.ImageButton
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object ButtonStatusSettingsForToolbarButton {
    enum class ButtonDisableValue {
        ON
    }

    fun set(
        editFragment: EditFragment,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        toolbarButton: ImageButton?
    ){
        val disable = editFragment.toolbarButtonConfigMap?.get(
            toolbarButtonBariantForEdit
        )?.get(
            SettingButtonConfigMapKey.DISABLE.key
        ) == ButtonDisableValue.ON.name
        if(!disable) return
        toolbarButton?.isEnabled = false
    }
}