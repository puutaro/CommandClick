package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForMonitorManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object MonitorBroadcastManager {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        intent.getStringExtra(
            BroadCastIntentExtraForMonitorManager.IS_MONITOR_SCROLL.scheme
        )?.let {
            try {
                val isMonitorScroll = it.toBoolean()
                terminalViewModel.onBottomScrollbyJs = isMonitorScroll
                LogSystems.stdSys("isMonitorScroll: $isMonitorScroll")
            } catch(e: Exception){
                print("pass")
            }
        }
        intent.getStringExtra(
            BroadCastIntentExtraForMonitorManager.IS_MONITOR_UPDATE.scheme
        )?.let {
            try {
                val isMonitorUpdate = it.toBoolean()
                terminalViewModel.onDisplayUpdate = isMonitorUpdate
                LogSystems.stdSys("isMonitorUpdate: $isMonitorUpdate")
            } catch(e: Exception){
                print("pass")
            }
        }
    }
}