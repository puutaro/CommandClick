package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.ScreenMonitor

object BroadcastScreenSwitchHandler {
    fun handle(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        when(intent.action) {
            Intent.ACTION_SCREEN_OFF
            -> ScreenMonitor.killInMonitorOff(ubuntuService)
            Intent.ACTION_SCREEN_ON
            -> ScreenMonitor.launchScreenRestart(ubuntuService)
        }
    }
}