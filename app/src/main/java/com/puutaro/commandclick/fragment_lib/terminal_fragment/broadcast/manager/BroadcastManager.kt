package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.manager

import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.puutaro.commandclick.fragment.TerminalFragment

class BroadcastManager {
    companion object {

        fun registerBroadcastReceiver(
            terminalFragment: TerminalFragment,
            broadcastReceiver: BroadcastReceiver,
            actionName: String
        ){
            try {
                val intentFilter = IntentFilter()
                intentFilter.addAction(actionName)
                terminalFragment.activity?.registerReceiver(
                    broadcastReceiver,
                    intentFilter
                )
            } catch(e: Exception){
                return
            }
        }

        fun unregisterBroadcastReceiver(
            terminalFragment: TerminalFragment,
            broadcastReceiver: BroadcastReceiver
        ){
            try {
                terminalFragment.terminalViewhandler.removeCallbacksAndMessages(
                    null
                )
                terminalFragment.activity?.unregisterReceiver(
                    broadcastReceiver
                )
            } catch(e: Exception){
                return
            }
        }
    }
}