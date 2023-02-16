package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.SharePreffrenceMethod

class ListViewUpdaterOnStart {
    companion object {
        fun update(
            cmdIndexFragment: CommandIndexFragment
        ){
            val binding = cmdIndexFragment.binding
            val readSharePreffernceMap = cmdIndexFragment.readSharePreffernceMap
            val makeListView = MakeListView(
                binding,
                cmdIndexFragment,
                readSharePreffernceMap
            )
            val cmdListAdapter = cmdIndexFragment.context?.let {
                makeListView.makeList(
                    it
                )
            } ?: return
            CommandListManager.execListUpdate(
                SharePreffrenceMethod.getReadSharePreffernceMap(
                    readSharePreffernceMap,
                    SharePrefferenceSetting.current_app_dir
                ),
                cmdListAdapter,
                binding.cmdList,
            )
        }
    }
}