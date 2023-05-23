package com.puutaro.commandclick.service.lib

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object PendingIntentCreator {
    fun create(
        context: Context,
        action: String,
    ): PendingIntent {
        val intent = Intent()
        intent.action = action
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )
    }
}