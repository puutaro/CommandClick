package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuProcessManager
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File
import java.time.LocalDateTime

object UbuntuInitProcess {
    fun launch(
        ubuntuService: UbuntuService,
        ){
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuProcessManager.UbuntuRunningSystemProcessType.SetUp.name
        ]?.cancel()
        FileSystems.updateFile(
            File(
                ubuntuService.cmdclickMonitorDirPath,
                ubuntuService.cmdclickMonitorFileName
            ).absolutePath,
            "### ${LocalDateTime.now()} proot"
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