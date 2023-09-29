package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.sender

import android.content.Intent
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.RESTART_OR_KILL_FRONT_SYSTEM
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.fragment.TerminalFragment

object UbuntuFrontSystem {
    fun start(
        terminalFragment: TerminalFragment,
    ){
        startOrKill(
            terminalFragment,
            RESTART_OR_KILL_FRONT_SYSTEM.START.name
        )
    }

    fun kill(
        terminalFragment: TerminalFragment,
    ){
        startOrKill(
            terminalFragment,
            RESTART_OR_KILL_FRONT_SYSTEM.KILL.name
        )
    }

    private fun startOrKill(
        terminalFragment: TerminalFragment,
        startOrKill: String,
    ){
        val intent = Intent()
        intent.action = BroadCastIntentScheme.RESTART_OR_KILL_SUB_FRONT_SYSTEM.action
        intent.putExtra(
            UbuntuServerIntentExtra.restart_or_stop_front_system.schema,
            startOrKill,
        )
        terminalFragment.activity?.sendBroadcast(intent)
    }
}