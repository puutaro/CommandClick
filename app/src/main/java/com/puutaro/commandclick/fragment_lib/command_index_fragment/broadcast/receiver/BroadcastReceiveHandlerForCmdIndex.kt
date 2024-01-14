package com.puutaro.commandclick.fragment_lib.command_index_fragment.broadcast.receiver

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

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
                    cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
                    ?: return
                val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
                    startUpPref,
                    SharePrefferenceSetting.current_app_dir
                )
                CommandListManager.execListUpdateForCmdIndex(
                    currentAppDirPath,
                    cmdIndexFragment.binding.cmdList,
                )
            }
        }

    }
}