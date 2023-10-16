package com.puutaro.commandclick.service.lib

import android.app.NotificationManager

enum class NotificationIdToImportance(
    val id: String,
    val importance: Int,
    val isSound: Boolean
){
    LOW( "com.puutaro.commandclick.low", NotificationManager.IMPORTANCE_LOW, false),
    HIGH("com.puutaro.commandclick.high",NotificationManager.IMPORTANCE_HIGH, true),
}
