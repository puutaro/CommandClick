package com.puutaro.commandclick.proccess.broadcast

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.util.CmdClickMap

object BroadcastSender {

    fun send(
        context: Context?,
        broadcastMap: Map<String, String>,
        keySeparator: String,
    ){
        val broadcastIntent = Intent()
        val action = broadcastMap.get(
            BroadCastSenderSchemaForCommon.action.name
        ) ?: return
        broadcastIntent.action = action
        val extraPairList = broadcastMap.get(
            BroadCastSenderSchemaForCommon.extras.name
        )?.let {
            CmdClickMap.createMap(
                it,
                keySeparator
            )
        }
        extraPairList?.forEach {
            broadcastIntent.putExtra(
                it.first,
                it.second
            )
        }
        context?.sendBroadcast(broadcastIntent)
    }
}

enum class BroadCastSenderSchemaForCommon {
    action,
    extras,
}