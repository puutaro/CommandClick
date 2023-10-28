package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.BroadcastHtmlReceiveHandler
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.HtmlLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorScrollManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorTextLauncher

object BroadcastHandlerForTerm {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val action = intent.action
        when(action){
            BroadCastIntentScheme.HTML_LAUNCH.action
            ->  HtmlLauncher.launch(
                intent,
                terminalFragment,
            )
            BroadCastIntentScheme.ULR_LAUNCH.action
            -> BroadcastHtmlReceiveHandler.handle(
                terminalFragment,
                intent,
            )
            BroadCastIntentScheme.MONITOR_TEXT_PATH.action
            -> MonitorTextLauncher.handle(
                terminalFragment,
                intent,
            )
            BroadCastIntentScheme.IS_MONITOR_SCROLL.action
            -> MonitorScrollManager.handle(
                terminalFragment,
                intent
            )
        }
    }
}