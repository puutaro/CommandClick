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
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuProcessType
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ProcessManager {
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

    fun monitorProcessNum(
        ubuntuService: UbuntuService
    ){
        var previousProcessNum = 0
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuProcessType.monitoringProcessNum.name]?.cancel()
        val processNumUpdateIntent = Intent()
        processNumUpdateIntent.action =
            BroadCastIntentScheme.UPDATE_PROCESS_NUM_NOTIFICATION.action
        val monitorProcessNumJob = CoroutineScope(Dispatchers.IO).launch {
            while(true){
                delay(1000)
                if(
                    ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
                ) continue
                val currentProcessNum = processNumCalculator(
                    ubuntuService
                )
                FileSystems.writeFile(
                    UsePath.cmdclickDefaultAppDirPath,
                    "processMonitor-${previousProcessNum}-${currentProcessNum}.txt",
                    String()

                )
                if(
                    previousProcessNum == currentProcessNum
                ) continue
                previousProcessNum = currentProcessNum
                ubuntuService.sendBroadcast(processNumUpdateIntent)
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[
                UbuntuProcessType.monitoringProcessNum.name
        ] = monitorProcessNumJob
    }
}