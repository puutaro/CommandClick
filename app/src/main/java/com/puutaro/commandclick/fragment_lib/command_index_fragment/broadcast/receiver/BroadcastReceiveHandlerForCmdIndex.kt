package com.puutaro.commandclick.fragment_lib.command_index_fragment.broadcast.receiver

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object BroadcastReceiveHandlerForCmdIndex {
//    fun handle(
//        cmdIndexFragment: CommandIndexFragment,
//        intent: Intent
//    ){
//        if(
//            !cmdIndexFragment.isVisible
//        ) return
//        val action = intent.action
//        when(action){
//            BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
//            -> {
////                val startUpPref =
////                    FannelInfoTool.getSharePref(cmdIndexFragment.context)
////                val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
////                    startUpPref,
////                    FannelInfoSetting.current_app_dir
////                )
////                CoroutineScope(Dispatchers.Main).launch {
////                    CommandListManager.execListUpdateForCmdIndex(
//////                        currentAppDirPath,
////                        cmdIndexFragment.binding.cmdList,
////                    )
////                }
//            }
//        }

//    }
}