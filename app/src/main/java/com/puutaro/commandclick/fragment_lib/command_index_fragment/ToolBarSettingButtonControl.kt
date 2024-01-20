package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.setting_button.SettingButtonHandler

class ToolBarSettingButtonControl(
    binding: CommandIndexFragmentBinding,
    cmdIndexFragment: CommandIndexFragment,
){
    private val settingButtonView = binding.cmdindexSettingButton

    private val settingButtonHandler = SettingButtonHandler(
        cmdIndexFragment,
    )

    fun toolbarSettingButtonOnLongClick() {
        settingButtonView.setOnClickListener {
            settingButtonHandler.handle(
                false,
                ToolbarButtonBariantForEdit.SETTING,
                settingButtonView,
                null,
            )
        }
    }

    fun toolbarSettingButtonOnClick(){
        settingButtonView.setOnLongClickListener {
            settingButtonInnerView ->
            settingButtonHandler.handle(
                true,
                ToolbarButtonBariantForEdit.SETTING,
                settingButtonView,
                null,
            )
            true
        }
    }
}



