package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.BroadcastHtmlReceiveHandler
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.HtmlLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorBroadcastManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorTextLauncher

object BroadcastHandlerForTerm {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val action = intent.action
        when(action){
            BroadCastIntentSchemeTerm.HTML_LAUNCH.action
            ->  HtmlLauncher.launch(
                intent,
                terminalFragment,
            )
            BroadCastIntentSchemeTerm.ULR_LAUNCH.action
            -> BroadcastHtmlReceiveHandler.handle(
                terminalFragment,
                intent,
            )
            BroadCastIntentSchemeTerm.MONITOR_TEXT_PATH.action
            -> MonitorTextLauncher.handle(
                terminalFragment,
                intent,
            )
            BroadCastIntentSchemeTerm.MONITOR_MANAGER.action
            -> MonitorBroadcastManager.handle(
                terminalFragment,
                intent
            )
        }
    }
}