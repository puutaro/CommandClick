package com.puutaro.commandclick.service.lib

import android.app.Service
import android.content.BroadcastReceiver
import android.content.IntentFilter


object BroadcastManagerForService {
    fun registerBroadcastReceiver(
        service: Service,
        broadcastReceiver: BroadcastReceiver,
        actionName: String
    ){
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(actionName)
            service.registerReceiver(
                broadcastReceiver,
                intentFilter
            )
        } catch(e: Exception){
            return
        }
    }

    fun unregisterBroadcastReceiver(
        service: Service,
        broadcastReceiver: BroadcastReceiver
    ){
        try {
            service.unregisterReceiver(
                broadcastReceiver
            )
        } catch(e: Exception){
            return
        }
    }
}