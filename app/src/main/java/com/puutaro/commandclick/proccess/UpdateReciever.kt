package com.puutaro.commandclick.proccess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message


class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val bundle = intent.extras
        val message = bundle!!.getString("message")
        if (handler != null) {
            val msg = Message()
            val data = Bundle()
            data.putString("message", message)
            msg.setData(data)
            handler?.sendMessage(msg)
        }
    }

    /**
     * メイン画面の表示を更新
     */
    fun registerHandler(locationUpdateHandler: Handler?) {
        handler = locationUpdateHandler
    }

    companion object {
        var handler: Handler? = null
    }
}