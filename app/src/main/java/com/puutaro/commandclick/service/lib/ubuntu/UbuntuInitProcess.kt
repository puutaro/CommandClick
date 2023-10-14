package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import com.puutaro.commandclick.util.FileSystems
import java.time.LocalDateTime

object UbuntuInitProcess {
    fun launch(
        ubuntuService: UbuntuService,
        isUbuntuRestore: Boolean,
        ){
        ubuntuService.ubuntuCoroutineJobsHashMap[ProcessManager.UbuntuRunningSystemProcessType.SetUp.name]?.cancel()
        FileSystems.updateFile(
            ubuntuService.cmdclickMonitorDirPath,
            ubuntuService.cmdclickMonitorFileName,
            "### ${LocalDateTime.now()} proot"
        )
        val setupUbuntuJob = UbuntuSetUp.set(
            ubuntuService,
            ubuntuService.cmdclickMonitorFileName,
            isUbuntuRestore
        )
        ubuntuService.ubuntuCoroutineJobsHashMap[ProcessManager.UbuntuRunningSystemProcessType.SetUp.name] = setupUbuntuJob
    }
}