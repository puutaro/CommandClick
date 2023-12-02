package com.puutaro.commandclick.fragment_lib.command_index_fragment.broadcast.receiver

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFannelList
import com.puutaro.commandclick.util.SharePreffrenceMethod

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
            BroadCastIntentSchemeForCmdIndex.UPDATE_FANNEL_LIST.action
            -> {
                val installFannelList = cmdIndexFragment.installFannelDialog?.findViewById<RecyclerView>(
                    R.id.install_fannel_recycler
                )
                InstallFannelList.updateInstallFannelList(
                    installFannelList,
                    InstallFannelList.makeFannelListForListView(),
                )
            }
            BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
            -> {
                val startUpPref =
                    cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
                    ?: return
                val currentAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
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