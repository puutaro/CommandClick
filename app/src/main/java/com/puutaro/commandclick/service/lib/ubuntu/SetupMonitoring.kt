package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.NetworkTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SetupMonitoring {
    fun launch(
        ubuntuService: UbuntuService,
        ){
        val context = ubuntuService.applicationContext
        val ubuntuFiles = ubuntuService.ubuntuFiles
        val ubuntuSetupCompFile = ubuntuFiles?.ubuntuSetupCompFile
        ubuntuService.ubuntuCoroutineJobsHashMap[ProcessManager.UbuntuInitProcessType.SetUpMonitoring.name]?.cancel()
        val ubuntuLaunchCompFile = ubuntuFiles?.ubuntuLaunchCompFile
            ?: return
        if(
            ubuntuSetupCompFile?.isFile != true
            && !NetworkTool.isWifi(context)
        ) {
            return
        }
        val ubuntuSupportDir =
            ubuntuLaunchCompFile.parent
                ?: return
        val ubuntuLaunchCompFileName =
            ubuntuLaunchCompFile.name
                ?: return
        try {
            FileSystems.removeFiles(
                ubuntuSupportDir,
                ubuntuLaunchCompFileName
            )
        } catch (e: Exception){
            print("pass")
        }
        val setupMonitoringJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                while(true) {
                    delay(2000)
                    if(
                        ubuntuFiles.ubuntuLaunchCompFile.isFile
                    ) break
                    val onSetupQuizNotificationIntent = Intent()
                    onSetupQuizNotificationIntent.action =
                        BroadCastIntentSchemeUbuntu.ON_UBUNTU_SETUP_QUIZ_NOTIFICATION.action
                    ubuntuService.sendBroadcast(onSetupQuizNotificationIntent)
                }
            }
            withContext(Dispatchers.IO){
                delay(500)
                val runningNotificationIntent = Intent()
                runningNotificationIntent.action =
                    BroadCastIntentSchemeUbuntu.ON_RUNNING_NOTIFICATION.action
                ubuntuService.sendBroadcast(runningNotificationIntent)
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[ProcessManager.UbuntuInitProcessType.SetUpMonitoring.name] =
            setupMonitoringJob
    }
}