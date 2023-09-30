package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServerForUbuntu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object InnerPulseServer {
    fun launch(
        ubuntuService: UbuntuService
    ){
        val notificationId = ubuntuService.notificationId
        val chanelId = ubuntuService.chanelId
        val context = ubuntuService.applicationContext
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        val pulseaudioSetupName = UbuntuProcessType.PulseaudioSetUp.name
        ubuntuService.ubuntuCoroutineJobsHashMap[pulseaudioSetupName]?.cancel()
        val pulseaudioSetUpJob = CoroutineScope(Dispatchers.IO).launch {
            ubuntuService.notificationManager?.let {
                PcPulseSetServerForUbuntu.exit()
                delay(100)
                PcPulseSetServerForUbuntu.launch(
                    context,
                    "127.0.0.1",
                    UsePort.UBUNTU_PULSE_RECEIVER_PORT.num.toString(),
                    notificationId,
                    chanelId,
                    UsePort.UBUNTU_PC_PULSE_SET_SERVER_PORT.num.toString(),
                    it,
                    cancelUbuntuServicePendingIntent
                )
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.put(
            pulseaudioSetupName,
            pulseaudioSetUpJob
        )
    }
}