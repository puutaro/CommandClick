package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.app.Service
import android.content.Intent
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServer
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServerForUbuntu
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ProcessManager {

    enum class UbuntuProcessType {
        SetUp,
        SetUpMonitoring,
        PulseaudioSetUp,
        monitoringProcessNum,
    }
    fun killAllProot(
        ubuntuService: UbuntuService
    ){
        val context = ubuntuService.applicationContext
        val cmdclickMonitorFileName = ubuntuService.cmdclickMonitorFileName
        ubuntuService.ubuntuFiles?.let {
            BusyboxExecutor(context, it).executeKillAllProcess(
                cmdclickMonitorFileName
            )
        }
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
        PcPulseSetServer.exit()
        ubuntuService.notificationManager?.cancel(ubuntuService.chanelId)
        ubuntuService.stopForeground(Service.STOP_FOREGROUND_DETACH)
        ubuntuService.stopSelf()
    }

    fun processNumCalculator(
        ubuntuService: UbuntuService
    ): Int {
        return ubuntuService.ubuntuCoroutineJobsHashMap.filter {
            val job = it.value
            job != null && job.isActive
        }.size
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
        var previousProcessNum = 0
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuProcessType.monitoringProcessNum.name]?.cancel()
        val processNumUpdateIntent = Intent()
        processNumUpdateIntent.action =
            BroadCastIntentScheme.UPDATE_PROCESS_NUM_NOTIFICATION.action
        val cmdclickTempProcessDirPath = UsePath.cmdclickTempProcessDirPath
        val cmdclickTempProcessesTxt = UsePath.cmdclickTempProcessesTxt
        FileSystems.writeFile(
            cmdclickTempProcessDirPath,
            cmdclickTempProcessesTxt,
            makeProcessTypeList(ubuntuService).joinToString("\n")
        )
        val monitorProcessNumJob = CoroutineScope(Dispatchers.IO).launch {
            while(true){
                delay(1000)
                if(
                    ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
                ) continue
                val currentProcessNum = processNumCalculator(
                    ubuntuService
                )
                if(
                    previousProcessNum == currentProcessNum
                ) continue
                previousProcessNum = currentProcessNum
                ubuntuService.sendBroadcast(processNumUpdateIntent)
                val processTypeList = makeProcessTypeList(ubuntuService)
                if(processTypeList.isEmpty()) continue
                FileSystems.writeFile(
                    cmdclickTempProcessDirPath,
                    cmdclickTempProcessesTxt,
                    processTypeList.joinToString("\n")
                )
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuProcessType.monitoringProcessNum.name
        ] = monitorProcessNumJob
    }

    private fun makeProcessTypeList(
        ubuntuService: UbuntuService
    ): List<String> {
        return ubuntuService.ubuntuCoroutineJobsHashMap.keys.filter {
                curProcessType ->
            val isRegularProcess = UbuntuProcessType.values().filter {
                curProcessType == it.name
            }.isNotEmpty()
            val isActive =
                ubuntuService.ubuntuCoroutineJobsHashMap[curProcessType]?.isActive == true
            !isRegularProcess && isActive
        }
    }
}