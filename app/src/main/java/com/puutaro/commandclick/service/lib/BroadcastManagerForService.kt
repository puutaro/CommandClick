package com.puutaro.commandclick.service.lib

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
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

    fun registerActionListBroadcastReceiver(
        service: Service,
        broadcastReceiver: BroadcastReceiver,
        actionNameList: List<String>
    ){
        try {
            val intentFilter = IntentFilter()
            actionNameList.forEach {
                intentFilter.addAction(it)
            }
            service.registerReceiver(
                broadcastReceiver,
                intentFilter
            )
        } catch(e: Exception){
            return
        }
    }

    fun registerScreenOnOffReceiver(
        service: Service,
        broadcastReceiver: BroadcastReceiver,
    ){
        try {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(Intent.ACTION_SCREEN_ON)
            service.registerReceiver(broadcastReceiver, filter)
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