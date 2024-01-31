package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.path.UsePath
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

object ProcessManager {

    enum class UbuntuInitProcessType {
        SetUpMonitoring,
    }

    enum class UbuntuRunningSystemProcessType {
        SetUp,
        PulseaudioSetUp,
        MonitoringProcessNum,
        IntentRequestMonitor,
    }

    fun finishProcess(
        ubuntuService: UbuntuService
    ){
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
        LinuxCmd.killProcess(ubuntuService.packageName)
        PcPulseSetServerForUbuntu.exit()
        killAllCoroutineJob(ubuntuService)
        PcPulseSetServer.exit()
        ubuntuService.intentMonitorServerSocket?.close()
    }

    fun processNumCalculator(
        ubuntuService: UbuntuService
    ): Int {
        return ubuntuService.ubuntuCoroutineJobsHashMap.filter {
            val job = it.value
            job != null && job.isActive
        }.size
    }

    private fun killAllCoroutineJob(
        ubuntuService: UbuntuService
    ){
        ubuntuService.ubuntuCoroutineJobsHashMap.forEach { t, u ->
            u?.cancel()
        }
    }

    fun monitorProcessAndNum(
        ubuntuService: UbuntuService
    ){
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuRunningSystemProcessType.MonitoringProcessNum.name]?.cancel()
        val processNumUpdateIntent = Intent()
        processNumUpdateIntent.action =
            BroadCastIntentSchemeUbuntu.UPDATE_PROCESS_NUM_NOTIFICATION.action
        val cmdclickTempProcessDirPath = UsePath.cmdclickTempProcessDirPath
        val cmdclickTempProcessesTxt = UsePath.cmdclickTempProcessesTxt
        FileSystems.writeFile(
            cmdclickTempProcessDirPath,
            cmdclickTempProcessesTxt,
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
                val currentProcessNum = processNumCalculator(
                    ubuntuService
                )
                val currentDisplayMessage = notificationManager.activeNotifications.filter {
                    it.id == channelId
                }.firstOrNull()?.notification?.extras?.getString("android.text")
                val shouldDisplayProcessNum = currentProcessNum - runningSystemProcessNum
                val shouldDisplayMessage = notiUbuntuRunningMessage.format(shouldDisplayProcessNum)
                if(
                    currentDisplayMessage == shouldDisplayMessage
                ) continue
                ubuntuService.sendBroadcast(processNumUpdateIntent)
                val processTypeList = makeProcessTypeList(ubuntuService)
                FileSystems.writeFile(
                    cmdclickTempProcessDirPath,
                    cmdclickTempProcessesTxt,
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
        return ubuntuService.ubuntuCoroutineJobsHashMap.keys.filter {
                curProcessType ->
            val isRegularProcess = UbuntuRunningSystemProcessType.values().filter {
                curProcessType == it.name
            }.isNotEmpty()
            val isActive =
                ubuntuService.ubuntuCoroutineJobsHashMap[curProcessType]?.isActive == true
            !isRegularProcess && isActive
        }
    }

    fun removeLaunchCompFile(
        ubuntuService: UbuntuService
    ) {
        val ubuntuLaunchCompFile = ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile
            ?:return
        val supportDirPath = ubuntuLaunchCompFile.parent ?: return
        val ubuntuLaunchCompFileName = ubuntuLaunchCompFile.name
        FileSystems.removeFiles(
            supportDirPath,
            ubuntuLaunchCompFileName
        )
    }
}