package com.puutaro.commandclick.fragment_lib.command_index_fragment.variable

import android.app.NotificationManager

enum class NotificationIdToImportance(
    val id: String,
    val importance: Int,
){
    LOW( "com.puutaro.commandclick.low", NotificationManager.IMPORTANCE_LOW),
    HIGH("com.puutaro.commandclick.high",NotificationManager.IMPORTANCE_HIGH),
}
