package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment

object MonitorToast {

    fun launch(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val message = intent.getStringExtra(
            BroadCastIntentSchemeTerm.MONITOR_TOAST.scheme
        )
        if(
            message.isNullOrEmpty()
        ) return
        Toast.makeText(
            terminalFragment.context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}