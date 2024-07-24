package com.puutaro.commandclick.fragment_lib.command_index_fragment.broadcast.receiver

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.state.FannelInfoTool

object BroadcastReceiveHandlerForCmdIndex {
    fun handle(
        cmdIndexFragment: CommandIndexFragment,
        intent: Intent
    ){
        if(
            !cmdIndexFragment.isVisible
        ) return
        val action = intent.action
        when(action){
            BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
            -> {
                val startUpPref =
                    FannelInfoTool.getSharePref(cmdIndexFragment.context)
                val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
                    startUpPref,
                    FannelInfoSetting.current_app_dir
                )
                CommandListManager.execListUpdateForCmdIndex(
                    currentAppDirPath,
                    cmdIndexFragment.binding.cmdList,
                )
            }
        }

    }
}