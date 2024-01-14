package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object ListViewUpdaterOnStart {
    fun update(
        cmdIndexFragment: CommandIndexFragment
    ){
        val binding = cmdIndexFragment.binding
        val readSharePreffernceMap = cmdIndexFragment.readSharePreffernceMap
        val cmdList = binding.cmdList
        CommandListManager.execListUpdateForCmdIndex(
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            ),
            cmdList,
        )
    }
}