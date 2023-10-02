package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuInfo
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.ubuntu.libs.IntentManager
import com.puutaro.commandclick.service.lib.ubuntu.libs.OpenTerminalButton
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuServerServiceManager
import com.puutaro.commandclick.service.variable.ServiceNotificationId
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UbuntuBroadcastHandler {
    fun handle(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val action = intent.action
        when(action){
            BroadCastIntentScheme.START_UBUNTU_SERVICE.action
            -> execStartUbuntuService(
                ubuntuService,
            )
            BroadCastIntentScheme.ON_RUNNING_NOTIFICATION.action
            -> execRunningNotification(
                ubuntuService
            )
            BroadCastIntentScheme.WIFI_WAIT_NITIFICATION.action
            -> execWifiNotification(
                ubuntuService,
            )
            BroadCastIntentScheme.ON_UBUNTU_SETUP_NOTIFICATION.action
            -> execOnUbuntuSetupNotification(
                    ubuntuService
                )
            BroadCastIntentScheme.ON_UBUNTU_SETUP_QUIZ_NOTIFICATION.action
            -> execOnUbuntuSetupQuizNotification(
                ubuntuService
            )
            BroadCastIntentScheme.IS_ACTIVE_UBUNTU_SERVICE.action
            -> execIsActiveUbuntuService()
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action
            -> execStopUbuntuService(
                ubuntuService
            )
            BroadCastIntentScheme.UPDATE_PROCESS_NUM_NOTIFICATION.action
            -> execUpdateProcessNumNotification(
                ubuntuService
            )
            BroadCastIntentScheme.ON_SLEEPING_NOTIFICATION.action
            -> execSleepingNotification(
                ubuntuService
            )
            BroadCastIntentScheme.BACKGROUND_CMD_KILL.action
            -> execBackGroundCmdKill(
                ubuntuService,
                intent
            )
            BroadCastIntentScheme.OPEN_FANNEL.action
            -> execOpenFannel(
                ubuntuService,
                intent
            )
            BroadCastIntentScheme.BACKGROUND_CMD_START.action
            -> execBackGroundCmdStart(
                ubuntuService,
                intent
            )
        }

    }

    private fun execStartUbuntuService(
        ubuntuService: UbuntuService,
    ){
        val onSetUpNotificationIntent = Intent()
        onSetUpNotificationIntent.action = BroadCastIntentScheme.ON_UBUNTU_SETUP_NOTIFICATION.action
        ubuntuService.sendBroadcast(onSetUpNotificationIntent)
        SetupMonitoring.launch(
            ubuntuService,
        )
        InnerPulseServer.launch(
            ubuntuService,
        )
        UbuntuInitProcess.launch(
            ubuntuService
        )
    }

    private fun execWifiNotification(
        ubuntuService: UbuntuService
    ){
        ubuntuService.notificationBuilder?.setContentTitle(UbuntuStateType.WIFI_WAIT.title)
        ubuntuService.notificationBuilder?.setContentText(UbuntuStateType.WIFI_WAIT.message)
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            ButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent,
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                ServiceNotificationId.ubuntuServer,
                it
            )
        }
    }

    fun execOnUbuntuSetupNotification(
        ubuntuService: UbuntuService
    ) {
        val context = ubuntuService.applicationContext
        val chanelId = ubuntuService.chanelId
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        ubuntuService.notificationBuilder?.setContentTitle(UbuntuStateType.ON_SETUP.title)
        ubuntuService.notificationBuilder?.setContentText(UbuntuStateType.ON_SETUP.message)
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            ButtonLabel.RESTART.label,
            cancelUbuntuServicePendingIntent
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                chanelId,
                it
            )
        }
    }

    fun execOnUbuntuSetupQuizNotification(
        ubuntuService: UbuntuService
    ) {
        val chanelId = ubuntuService.chanelId
        val cmdclickMonitorDirPath = ubuntuService.cmdclickMonitorDirPath
        val cmdclickMonitorFileName = ubuntuService.cmdclickMonitorFileName
        val monitorLastLine = ReadText(
            cmdclickMonitorDirPath,
            cmdclickMonitorFileName
        ).textToList().lastOrNull() ?: return
        ubuntuService.notificationBuilder?.setContentTitle("${UbuntuStateType.ON_SETUP.title}\t${monitorLastLine}")
        ubuntuService.notificationBuilder?.setContentText(
            ubuntuService.waitQuiz.echoQorA()
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                chanelId,
                it
            )
        }
    }

    private fun execRunningNotification(
        ubuntuService: UbuntuService
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            ubuntuService.applicationContext,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        val chanelId = ubuntuService.chanelId
        ubuntuService.notificationBuilder?.setContentTitle(UbuntuStateType.RUNNING.title)
        val itSelfProcessNum = 1
        ubuntuService.notificationBuilder?.setContentText(
            UbuntuStateType.RUNNING.message.format(
                ProcessManager.processNumCalculator(ubuntuService) - itSelfProcessNum
            )
        )
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            ButtonLabel.RESTART.label,
            cancelUbuntuServicePendingIntent
        )
        OpenTerminalButton.add(
            ubuntuService
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                chanelId,
                it
            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[ProcessManager.UbuntuProcessType.SetUpMonitoring.name]?.cancel()
    }

    private fun execSleepingNotification(
        ubuntuService: UbuntuService
    ){
        ubuntuService.notificationBuilder?.setContentTitle("Sleeping...")
        ubuntuService.notificationBuilder?.setContentText("...")
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                ubuntuService.chanelId,
                it
            )
        }
    }

    private fun execIsActiveUbuntuService(
    ){
        FileSystems.writeFile(
            UsePath.cmdclickTempUbuntuServiceDirPath,
            UsePath.cmdclickTmpUbuntuServiceActiveFileName,
            String()
        )
    }

    private fun execStopUbuntuService(
        ubuntuService: UbuntuService
    ){
        ProcessManager.killAllProot(ubuntuService)
        ProcessManager.finishProcess(ubuntuService)
        ubuntuService.stopSelf()
        UbuntuServerServiceManager.reLaunchUbuntuService(ubuntuService)
    }

    private fun execUpdateProcessNumNotification(
        ubuntuService: UbuntuService
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        ubuntuService.notificationBuilder?.setContentTitle(UbuntuStateType.RUNNING.title)
        ubuntuService.notificationBuilder?.setContentText(
            UbuntuStateType.RUNNING.message.format(
                ProcessManager.processNumCalculator(ubuntuService)
            )
        )
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            ButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent
        )
        OpenTerminalButton.add(ubuntuService)
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                ubuntuService.chanelId,
                it
            )
        }
    }

    private fun execBackGroundCmdKill(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        val ubuntuCroutineJobType = intent.getStringExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobType.schema
        ) ?: return
        ubuntuService.ubuntuCoroutineJobsHashMap.get(ubuntuCroutineJobType)?.cancel()
        ubuntuFiles.let {
            BusyboxExecutor(ubuntuService.applicationContext, it).executeKillProcess(
                ubuntuCroutineJobType,
                ubuntuService.cmdclickMonitorFileName
            )
        }
    }

    private fun execOpenFannel(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        if(
            ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        val fannelDirPath = intent.getStringExtra(
            UbuntuServerIntentExtra.fannelDirPath.schema
        ) ?: return
        val fannelName = intent.getStringExtra(
            UbuntuServerIntentExtra.fannelName.schema
        ) ?: String()
        IntentManager.launchFannelIntent(
            ubuntuService.applicationContext,
            fannelDirPath,
            fannelName,
        )
    }

    private fun execBackGroundCmdStart(
        ubuntuService: UbuntuService,
        intent: Intent
    ){
        val context = ubuntuService.applicationContext
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        val backgroundShellPath = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundShellPath.schema
        ) ?: return
        val backgroundArgsTabSepaStr = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema
        ) ?: String()
        val monitorFileName = intent.getStringExtra(
            UbuntuServerIntentExtra.monitorFileName.schema
        ) ?: return
        val backgroundShellJob = CoroutineScope(Dispatchers.IO).launch {
            BusyboxExecutor(
                context,
                UbuntuFiles(context)
            ).executeProotCommand(
                listOf("su","-", UbuntuInfo.user, "-c" ,"bash '$backgroundShellPath' $backgroundArgsTabSepaStr"),
                monitorFileName = monitorFileName,
            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.get(
            backgroundShellPath,
        )?.cancel()
        ubuntuService.ubuntuCoroutineJobsHashMap.put(
            backgroundShellPath,
            backgroundShellJob
        )
    }
}