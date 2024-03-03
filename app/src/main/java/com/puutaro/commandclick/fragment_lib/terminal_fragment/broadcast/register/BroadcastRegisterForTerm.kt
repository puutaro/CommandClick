package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.register

import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister

object BroadcastRegisterForTerm {
    fun register(
        terminalFragment: TerminalFragment
    ){
        BroadcastRegister.registerBroadcastReceiverMultiActions(
            terminalFragment,
            terminalFragment.broadcastReceiverForTerm,
            listOf(
                BroadCastIntentSchemeTerm.HTML_LAUNCH.action,
                BroadCastIntentSchemeTerm.ULR_LAUNCH.action,
                BroadCastIntentSchemeTerm.MONITOR_TEXT_PATH.action,
                BroadCastIntentSchemeTerm.MONITOR_MANAGER.action,
                BroadCastIntentSchemeTerm.MONITOR_TOAST.action,
                BroadCastIntentSchemeTerm.DEBUGGER_NOTI.action,
                BroadCastIntentSchemeTerm.DEBUGGER_JS_WATCH.action,
                BroadCastIntentSchemeTerm.DEBUGGER_SYS_WATCH.action,
                BroadCastIntentSchemeTerm.DEBUGGER_CLOSE.action,
            )
        )
    }
}