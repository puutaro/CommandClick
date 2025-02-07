package com.puutaro.commandclick.service.lib

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object PendingIntentCreator {
    fun create(
        context: Context,
        action: String,
        extraList: Sequence<Pair<String, String>>? = null,
        requestCode: Int = 0
    ): PendingIntent {
        val intent = Intent()
        intent.action = action
        extraList?.forEach {
            intent.putExtra(
                it.first,
                it.second
            )
        }
        return PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )
    }
}