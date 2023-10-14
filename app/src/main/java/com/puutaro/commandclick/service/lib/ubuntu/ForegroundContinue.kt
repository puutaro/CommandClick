package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.service.UbuntuService

object ForegroundContinue {
    fun launch(
        ubuntuService: UbuntuService
    ){
        val processNumUpdateIntent = Intent()
        processNumUpdateIntent.action =
            BroadCastIntentScheme.UPDATE_PROCESS_NUM_NOTIFICATION.action
        ubuntuService.sendBroadcast(processNumUpdateIntent)
        val intent = Intent(
            ubuntuService,
            ubuntuService::class.java
        )
        ubuntuService.startForegroundService(intent)
        ubuntuService.isTaskKill = true
    }
}