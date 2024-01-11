package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.setting_button.SettingButtonHandler

class ToolBarSettingButtonControl(
    binding: CommandIndexFragmentBinding,
    cmdIndexFragment: CommandIndexFragment,
    readSharePreffernceMap: Map<String, String>,
){
    private val settingButtonView = binding.cmdindexSettingButton

    private val settingButtonHandler = SettingButtonHandler(
        cmdIndexFragment,
        readSharePreffernceMap,
    )

    fun toolbarSettingButtonOnLongClick() {
        settingButtonView.setOnClickListener {
            settingButtonHandler.handle(
                false,
                settingButtonView,
            )
        }
    }

    fun toolbarSettingButtonOnClick(){
        settingButtonView.setOnLongClickListener {
            settingButtonInnerView ->
            settingButtonHandler.handle(
                true,
                settingButtonView,
            )
            true
        }
    }
}



