package com.puutaro.commandclick.proccess.broadcast

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.util.map.CmdClickMap

object BroadcastSender {

    fun send(
        context: Context?,
        broadcastMap: Map<String, String>,
        keySeparator: Char,
    ){
        val action = broadcastMap.get(
            BroadCastSenderSchemaForCommon.action.name
        )
        val extraMapStr = broadcastMap.get(
            BroadCastSenderSchemaForCommon.extras.name
        )
        val broadcastIntent = createBroadcastIntent(
            action,
            extraMapStr,
            keySeparator
        )?: return
        context?.sendBroadcast(broadcastIntent)
    }

    fun createBroadcastIntent(
        action: String?,
        extraMapStr: String?,
        keySeparator: Char,
    ): Intent? {
        if(
            action.isNullOrEmpty()
        ) return null
        val broadcastIntent = Intent()
        broadcastIntent.action = action
        val extraPairList = extraMapStr?.let {
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
        return broadcastIntent
    }

    fun normalSend(
        context: Context?,
        action: String,
        putStringExtraList: Sequence<Pair<String, String>>? = null,
    ){
        val broadcastIntent = Intent()
        broadcastIntent.action = action
        putStringExtraList?.forEach{
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