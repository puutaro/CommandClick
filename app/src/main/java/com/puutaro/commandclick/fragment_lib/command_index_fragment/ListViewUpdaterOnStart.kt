package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.state.FannelInfoTool

object ListViewUpdaterOnStart {
    fun update(
        cmdIndexFragment: CommandIndexFragment
    ){
        val binding = cmdIndexFragment.binding
//        val fannelInfoMap = cmdIndexFragment.fannelInfoMap
        val cmdList = binding.cmdList
        CommandListManager.execListUpdateForCmdIndex(
//            FannelInfoTool.getCurrentAppDirPath(
//                fannelInfoMap
//            ),
            cmdList,
        )
    }
}