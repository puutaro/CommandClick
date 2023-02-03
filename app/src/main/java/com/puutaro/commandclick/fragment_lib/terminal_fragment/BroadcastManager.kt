package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.IntentFilter
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment

class BroadcastManager {
    companion object {

        fun registerBloadcastReciever(
            terminalFragment: TerminalFragment
        ){
            try {
                val intentFilter = IntentFilter()
                intentFilter.addAction(BroadCastIntentScheme.ULR_LAUNCH.action)
                terminalFragment.activity?.registerReceiver(terminalFragment.broadcastReceiver, intentFilter)
            } catch(e: Exception){
                return
            }
        }

        fun unregisterBloadcastReciever(
            terminalFragment: TerminalFragment
        ){
            try {
                terminalFragment.terminalViewhandler.removeCallbacksAndMessages(
                    null
                )
                terminalFragment.activity?.unregisterReceiver(
                    terminalFragment.broadcastReceiver
                );
            } catch(e: Exception){
                return
            }
        }
    }
}