package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServerForUbuntu
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object InnerPulseServer {
    fun launch(
        ubuntuService: UbuntuService
    ){
        val context = ubuntuService.applicationContext
        val pulseaudioSetupName = ProcessManager.UbuntuRunningSystemProcessType.PulseaudioSetUp.name
        ubuntuService.ubuntuCoroutineJobsHashMap[pulseaudioSetupName]?.cancel()
        val pulseaudioSetUpJob = CoroutineScope(Dispatchers.IO).launch {
            PcPulseSetServerForUbuntu.exit()
            delay(100)
            PcPulseSetServerForUbuntu.launch(
                context,
                "127.0.0.1",
                UsePort.UBUNTU_PULSE_RECEIVER_PORT.num.toString(),
                UsePort.UBUNTU_PC_PULSE_SET_SERVER_PORT.num.toString(),
            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.put(
            pulseaudioSetupName,
            pulseaudioSetUpJob
        )
    }
}