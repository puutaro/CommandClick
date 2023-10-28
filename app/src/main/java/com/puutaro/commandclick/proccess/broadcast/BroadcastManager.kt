package com.puutaro.commandclick.proccess.broadcast

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment

object BroadcastManager {

    fun registerBroadcastReceiverForActivity(
        activity: MainActivity,
        broadcastReceiver: BroadcastReceiver,
        actionName: String
    ){
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(actionName)
            activity.registerReceiver(
                broadcastReceiver,
                intentFilter
            )
        } catch(e: Exception){
            return
        }
    }
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

    fun registerBroadcastReceiverMultiActions(
        fragment: Fragment,
        broadcastReceiver: BroadcastReceiver,
        actionNameList: List<String>
    ){
        try {
            val intentFilter = IntentFilter()
            actionNameList.forEach {
                intentFilter.addAction(it)
            }
            fragment.activity?.registerReceiver(
                broadcastReceiver,
                intentFilter
            )
        } catch(e: Exception){
            return
        }
    }


    fun unregisterBroadcastReceiverForActivity(
        activity: MainActivity,
        broadcastReceiver: BroadcastReceiver
    ){
        try {
            activity.unregisterReceiver(
                broadcastReceiver
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