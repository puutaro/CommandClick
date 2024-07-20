package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuProcessManager
import com.puutaro.commandclick.util.LogSystems
import java.time.LocalDateTime

object UbuntuInitProcess {
    fun launch(
        ubuntuService: UbuntuService,
        ){
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuProcessManager.UbuntuRunningSystemProcessType.SetUp.name
        ]?.cancel()
        LogSystems.stdSys(
            "start"
        )
        val setupUbuntuJob = UbuntuSetUp.set(
            ubuntuService,
            ubuntuService.cmdclickMonitorFileName,
        )
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuProcessManager.UbuntuRunningSystemProcessType.SetUp.name
        ] = setupUbuntuJob
    }

    fun cancel(
        ubuntuService: UbuntuService,
    ){
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuProcessManager.UbuntuRunningSystemProcessType.SetUp.name
        ]?.cancel()
    }
}