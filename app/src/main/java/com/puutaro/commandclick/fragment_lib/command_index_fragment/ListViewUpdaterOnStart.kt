package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.state.FannelPrefGetter

object ListViewUpdaterOnStart {
    fun update(
        cmdIndexFragment: CommandIndexFragment
    ){
        val binding = cmdIndexFragment.binding
        val readSharePreffernceMap = cmdIndexFragment.readSharePreferenceMap
        val cmdList = binding.cmdList
        CommandListManager.execListUpdateForCmdIndex(
            FannelPrefGetter.getCurrentAppDirPath(
                readSharePreffernceMap
            ),
            cmdList,
        )
    }
}