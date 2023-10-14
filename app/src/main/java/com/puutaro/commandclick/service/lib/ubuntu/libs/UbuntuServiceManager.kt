package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.service.UbuntuService

object UbuntuServerServiceManager {

    fun reLaunchUbuntuService(
        ubuntuService: UbuntuService
    ){
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = BroadCastIntentScheme.RESTART_UBUNTU_SERVICE_FROM_ACTIVITY.action
        ubuntuService.sendBroadcast(intent)
    }
}