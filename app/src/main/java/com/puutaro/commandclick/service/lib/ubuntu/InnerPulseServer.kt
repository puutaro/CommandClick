package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.proccess.ubuntu.UbuntuExtraSystemShells
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServerForUbuntu
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuProcessManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object InnerPulseServer {
    fun launch(
        ubuntuService: UbuntuService
    ){
        val isNotPulseSet = !UbuntuExtraSystemShells.isMacro(
            UbuntuExtraSystemShells.UbuntuExtraSystemShellMacro.PULSE
        )
        if(
            isNotPulseSet
        ) return
        val context = ubuntuService.applicationContext
        val pulseaudioSetupTypeName = UbuntuProcessManager.UbuntuExtraSystemProcessType.PULSE_AUDIO_SETUP.type
        ubuntuService.ubuntuCoroutineJobsHashMap[pulseaudioSetupTypeName]?.cancel()
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
            pulseaudioSetupTypeName,
            pulseaudioSetUpJob
        )
    }
}