package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.UbuntuExtraSystemShells
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServer
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServerForUbuntu
import com.puutaro.commandclick.service.lib.ubuntu.variable.UbuntuStateType
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.shell.LinuxCmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object UbuntuProcessManager {

    enum class UbuntuInitProcessType {
        SetUpMonitoring,
    }

    enum class UbuntuRunningSystemProcessType {
        SetUp,
        MonitoringProcessNum,
        IntentRequestMonitor,
    }

    enum class UbuntuExtraSystemProcessType(
        val type: String,
    ) {
        PULSE_AUDIO_SETUP(UbuntuExtraSystemShells.UbuntuExtraSystemShellMacro.PULSE.macro)
    }

    fun finishProcess(
        ubuntuService: UbuntuService
    ){
        LinuxCmd.killProcess(
            ubuntuService.applicationContext
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            ubuntuService,
            ubuntuService.broadcastReceiverForUbuntuServerProcess,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            ubuntuService,
            ubuntuService.screenStatusReceiver,
        )
        PcPulseSetServerForUbuntu.exit()
        killAllCoroutineJob(ubuntuService)
        removeLaunchCompFile(ubuntuService)
        PcPulseSetServer.exit()
        ubuntuService.intentMonitorServerSocket?.close()
        ubuntuService.notificationManager?.cancel(ubuntuService.chanelId)
        ubuntuService.stopForeground(Service.STOP_FOREGROUND_DETACH)
        ubuntuService.stopSelf()
    }

    fun finishProcessForSleep(
        ubuntuService: UbuntuService
    ){
        LinuxCmd.killProcess(
            ubuntuService.applicationContext
        )
        PcPulseSetServerForUbuntu.exit()
        killAllCoroutineJob(ubuntuService)
        PcPulseSetServer.exit()
        ubuntuService.intentMonitorServerSocket?.close()
    }

    fun processNumCalculator(
        ubuntuService: UbuntuService
    ): Int {
        val totalProcNum = ubuntuService.ubuntuCoroutineJobsHashMap.filter {
            val job = it.value
            job != null && job.isActive
        }.size
        val pulseaudioNum = ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuExtraSystemProcessType.PULSE_AUDIO_SETUP.type
        ].let {
            if(
                it != null
                && it.isActive
            ) return@let 1
            0
        }
        return totalProcNum - pulseaudioNum
    }

    fun killAllCoroutineJob(
        ubuntuService: UbuntuService
    ){
        ubuntuService.ubuntuCoroutineJobsHashMap.forEach { t, u ->
            u?.cancel()
        }
    }

    fun monitorProcessAndNum(
        ubuntuService: UbuntuService
    ){
        val context = ubuntuService.applicationContext
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuRunningSystemProcessType.MonitoringProcessNum.name]?.cancel()
        val processNumUpdateIntent = Intent()
        processNumUpdateIntent.action =
            BroadCastIntentSchemeUbuntu.UPDATE_PROCESS_NUM_NOTIFICATION.action
        val cmdclickTempProcessDirPath = UsePath.cmdclickTempProcessDirPath
        val cmdclickTempProcessesTxt = UsePath.cmdclickTempProcessesTxt
        FileSystems.writeFile(
            File(
                cmdclickTempProcessDirPath,
                cmdclickTempProcessesTxt
            ).absolutePath,
            makeProcessTypeList(ubuntuService).joinToString("\n")
        )
        val notificationManager = ubuntuService.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = ubuntuService.chanelId
        val runningSystemProcessNum = UbuntuRunningSystemProcessType.values().size
        val notiUbuntuRunningMessage =  UbuntuStateType.RUNNING.message
        val ubuntuLaunchCompFile = ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile
        val monitorProcessNumJob = CoroutineScope(Dispatchers.IO).launch {
            while(true){
                delay(1000)
                if(
                    ubuntuLaunchCompFile?.isFile != true
                ) continue
                var isStop = true
                withContext(Dispatchers.IO) {
                    for (i in 1..3) {
                        delay(300)
                        val currentProcessNum = processNumCalculator(
                            ubuntuService
                        )
                        if (
                            !LinuxCmd.isBasicProcess(context)
                            || currentProcessNum < 0
                        ) continue
                        isStop = false
                        break
                    }
                }
                if(isStop){
                    BroadcastSender.normalSend(
                        ubuntuService,
                        BroadCastIntentSchemeUbuntu.STOP_UBUNTU_SERVICE.action
                    )
                    return@launch
                }
                val currentDisplayMessage = notificationManager.activeNotifications.filter {
                    it.id == channelId
                }.firstOrNull()?.notification?.extras?.getString("android.text")
                val currentProcessNum = processNumCalculator(
                    ubuntuService
                )
                val shouldDisplayProcessNum = currentProcessNum - runningSystemProcessNum
                val shouldDisplayMessage = notiUbuntuRunningMessage.format(shouldDisplayProcessNum)
                if(
                    currentDisplayMessage == shouldDisplayMessage
                ) continue
                ubuntuService.sendBroadcast(processNumUpdateIntent)
                val processTypeList = makeProcessTypeList(ubuntuService)
                FileSystems.writeFile(
                    File(
                        cmdclickTempProcessDirPath,
                        cmdclickTempProcessesTxt
                    ).absolutePath,
                    processTypeList.joinToString("\n")
                )
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuRunningSystemProcessType.MonitoringProcessNum.name
        ] = monitorProcessNumJob
    }

    private fun makeProcessTypeList(
        ubuntuService: UbuntuService
    ): List<String> {
        val regularProcTypeList = UbuntuRunningSystemProcessType.values().map{
            it.name
        } + listOf(UbuntuExtraSystemProcessType.PULSE_AUDIO_SETUP.type)
        return ubuntuService.ubuntuCoroutineJobsHashMap.keys.filter {
                curProcessType ->
            val isNotRegularProcess = regularProcTypeList.filter { regProcTypeName ->
                curProcessType == regProcTypeName
            }.isEmpty()
            val isActive =
                ubuntuService.ubuntuCoroutineJobsHashMap[curProcessType]?.isActive == true
            isNotRegularProcess && isActive
        }
    }

    fun removeLaunchCompFile(
        ubuntuService: UbuntuService
    ) {
        val ubuntuLaunchCompFile =
            ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile
                ?:return
        FileSystems.removeFiles(
            ubuntuLaunchCompFile.absolutePath
        )
    }
}