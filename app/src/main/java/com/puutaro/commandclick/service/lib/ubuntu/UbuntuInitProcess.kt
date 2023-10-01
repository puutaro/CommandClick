package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.FileSystems
import java.time.LocalDateTime

object UbuntuInitProcess {
    fun launch(
        ubuntuService: UbuntuService
        ){
        val context = ubuntuService.applicationContext
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUp.name]?.cancel()
        FileSystems.updateFile(
            ubuntuService.cmdclickMonitorDirPath,
            ubuntuService.cmdclickMonitorFileName,
            "### ${LocalDateTime.now()} proot"
        )
        val setupUbuntuJob = UbuntuSetUp.set(
            context,
            ubuntuService.cmdclickMonitorFileName,
        )
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUp.name] = setupUbuntuJob
    }
}