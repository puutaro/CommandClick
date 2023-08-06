package com.puutaro.commandclick.proccess.broadcast

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.TerminalFragment

object BroadcastManager {
    fun registerBroadcastReceiver(
        fragment: Fragment,
        broadcastReceiver: BroadcastReceiver,
        actionName: String
    ){
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(actionName)
            fragment.activity?.registerReceiver(
                broadcastReceiver,
                intentFilter
            )
        } catch(e: Exception){
            return
        }
    }

    fun unregisterBroadcastReceiverForTerm(
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

    fun unregisterBroadcastReceiver(
        fragment: Fragment,
        broadcastReceiver: BroadcastReceiver
    ){
        try {
            fragment.activity?.unregisterReceiver(
                broadcastReceiver
            )
        } catch(e: Exception){
            return
        }
    }
}