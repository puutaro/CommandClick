package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object MonitorScrollManager {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val isMonitorScroll = intent.getStringExtra(
            BroadCastIntentScheme.IS_MONITOR_SCROLL.scheme,
        ) != "false"
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.onBottomScrollbyJs = isMonitorScroll
    }
}