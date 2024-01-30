package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.intent.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.Shell2Http
import com.puutaro.commandclick.proccess.ubuntu.SshManager
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuInfo
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.IntentManager
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuServiceButton
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import com.puutaro.commandclick.service.lib.ubuntu.libs.RestoreLabel
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuServerServiceManager
import com.puutaro.commandclick.service.lib.ubuntu.variable.UbuntuNotiButtonLabel
import com.puutaro.commandclick.service.lib.ubuntu.variable.UbuntuStateType
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UbuntuBroadcastHandler {
    fun handle(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val action = intent.action
        when(action){
            BroadCastIntentSchemeUbuntu.START_UBUNTU_SERVICE.action
            -> execStartUbuntuService(
                ubuntuService,
                intent,
            )
            BroadCastIntentSchemeUbuntu.ON_RUNNING_NOTIFICATION.action
            -> execRunningNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.WIFI_WAIT_NITIFICATION.action
            -> execWifiNotification(
                ubuntuService,
            )
            BroadCastIntentSchemeUbuntu.ON_UBUNTU_SETUP_NOTIFICATION.action
            -> execOnUbuntuSetupNotification(
                    ubuntuService
                )
            BroadCastIntentSchemeUbuntu.ON_UBUNTU_SETUP_QUIZ_NOTIFICATION.action
            -> execOnUbuntuSetupQuizNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.IS_ACTIVE_UBUNTU_SERVICE.action
            -> execIsActiveUbuntuService()
            BroadCastIntentSchemeUbuntu.STOP_UBUNTU_SERVICE.action
            -> execStopUbuntuService(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.UPDATE_PROCESS_NUM_NOTIFICATION.action
            -> execUpdateProcessNumNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.ON_SLEEPING_NOTIFICATION.action
            -> execSleepingNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.OPEN_FANNEL.action
            -> execOpenFannel(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.ADMIN_CMD_START.action
            -> execAdminCmdStart(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START.action
            -> execBackGroundCmdStart(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START.action
            -> execSell2Http(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.CMD_KILL_BY_ADMIN.action
            -> execCmdKillByAdmin(
                ubuntuService,
                intent,
            )
        }

    }

    private fun execStartUbuntuService(
        ubuntuService: UbuntuService,
        intent: Intent
    ){
        ubuntuService.isUbuntuRestore = !intent.getStringExtra(
            UbuntuServerIntentExtra.ubuntuRestoreSign.schema
        ).isNullOrEmpty()
        val onSetUpNotificationIntent = Intent()
        onSetUpNotificationIntent.action = BroadCastIntentSchemeUbuntu.ON_UBUNTU_SETUP_NOTIFICATION.action
        ubuntuService.sendBroadcast(onSetUpNotificationIntent)
        SetupMonitoring.launch(
            ubuntuService,
        )
        InnerPulseServer.launch(
            ubuntuService,
        )
        UbuntuInitProcess.launch(
            ubuntuService,
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
            UbuntuNotiButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent,
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                ServiceChannelNum.ubuntuServer,
                it
            )
        }
    }

    private fun execOnUbuntuSetupNotification(
        ubuntuService: UbuntuService
    ) {
        val chanelId = ubuntuService.chanelId
        val title = RestoreLabel.decide(
            ubuntuService,
            UbuntuStateType.ON_SETUP.title
        )
        ubuntuService.notificationBuilder?.setContentTitle(title)
        ubuntuService.notificationBuilder?.setContentText(UbuntuStateType.ON_SETUP.message)
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            UbuntuNotiButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                chanelId,
                it
            )
        }
    }

    private fun execOnUbuntuSetupQuizNotification(
        ubuntuService: UbuntuService
    ) {
        val chanelId = ubuntuService.chanelId
        val cmdclickMonitorDirPath = ubuntuService.cmdclickMonitorDirPath
        val cmdclickMonitorFileName = ubuntuService.cmdclickMonitorFileName
        val monitorLastLine = ReadText(
            cmdclickMonitorDirPath,
            cmdclickMonitorFileName
        ).textToList().lastOrNull() ?: return
        val titleEntry = "${UbuntuStateType.ON_SETUP.title}\t${monitorLastLine}"
        val title = RestoreLabel.decide(
            ubuntuService,
            titleEntry
        )
        ubuntuService.notificationBuilder?.setContentTitle(title)
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
            UbuntuNotiButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent
        )
        UbuntuServiceButton.addOpenTerminal(ubuntuService)
        UbuntuServiceButton.addManager(ubuntuService)
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                chanelId,
                it
            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[ProcessManager.UbuntuInitProcessType.SetUpMonitoring.name]?.cancel()
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
//        ProcessManager.killAllProot(ubuntuService)
        LinuxCmd.killProcess(ubuntuService.packageName)
        ProcessManager.finishProcess(ubuntuService)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                ubuntuService.stopSelf()
                UbuntuServerServiceManager.reLaunchUbuntuService(ubuntuService)
            }
        }
    }

    private fun execUpdateProcessNumNotification(
        ubuntuService: UbuntuService
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        val systemRunningProcessNum =
            ProcessManager.UbuntuRunningSystemProcessType.values().size
        ubuntuService.notificationBuilder?.setContentTitle(UbuntuStateType.RUNNING.title)
        val userProotProcessNum =
            ProcessManager.processNumCalculator(ubuntuService) - systemRunningProcessNum
        ubuntuService.notificationBuilder?.setContentText(
            UbuntuStateType.RUNNING.message.format(
                userProotProcessNum
            )
        )
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            UbuntuNotiButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent
        )
        UbuntuServiceButton.addOpenTerminal(ubuntuService)
        UbuntuServiceButton.addManager(ubuntuService)
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                ubuntuService.chanelId,
                it
            )
        }
    }

    private fun execOpenFannel(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        if(
            ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    ubuntuService.applicationContext,
                    "Launch ubuntu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
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

    private fun execAdminCmdStart(
        ubuntuService: UbuntuService,
        intent: Intent
    ){
        val context = ubuntuService.applicationContext
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    ubuntuService.applicationContext,
                    "Launch ubuntu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        val adminShellPath = intent.getStringExtra(
            UbuntuServerIntentExtra.adminShellPath.schema
        ) ?: return
        val adminArgsTabSepaStr = intent.getStringExtra(
            UbuntuServerIntentExtra.adminArgsTabSepaStr.schema
        ) ?: String()
        val adminMonitorFileName = intent.getStringExtra(
            UbuntuServerIntentExtra.adminMonitorFileName.schema
        ) ?: return
        val adminArgsTabSepaStrWithQuote = adminArgsTabSepaStr.split("\t").map {
            "\"$it\""
        }.joinToString("\t")
        val adminShellJob = CoroutineScope(Dispatchers.IO).launch {
            BusyboxExecutor(
                context,
                UbuntuFiles(context)
            ).executeProotCommand(
                listOf("su","-", UbuntuInfo.user, "-c" ,"bash '$adminShellPath' $adminArgsTabSepaStrWithQuote"),
                monitorFileName = adminMonitorFileName,
            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.get(
            adminShellPath,
        )?.cancel()
        ubuntuService.ubuntuCoroutineJobsHashMap.put(
            adminShellPath,
            adminShellJob
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
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                   context,
                    "Launch ubuntu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        val backgroundShellPath = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundShellPath.schema
        ) ?: return
        val backgroundArgsTabSepaStr = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema
        ) ?: String()
        val backgroundMonitorFileName = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundMonitorFileName.schema
        ) ?: UsePath.cmdClickMonitorFileName_2
        ubuntuService.ubuntuCoroutineJobsHashMap.get(
            backgroundShellPath,
        )?.cancel()
        val backgroundShellJob = CoroutineScope(Dispatchers.IO).launch {
            execBackGroundCmdStartHandler(
                ubuntuService,
                backgroundShellPath,
                backgroundArgsTabSepaStr,
                backgroundMonitorFileName,
            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.put(
            backgroundShellPath,
            backgroundShellJob
        )
    }

    private suspend fun execBackGroundCmdStartHandler(
        ubuntuService: UbuntuService,
        backgroundShellPath: String,
        backgroundArgsTabSepaStr: String,
        backgroundMonitorFileName: String,
    ){
        val context = ubuntuService.applicationContext
        if(
            !LinuxCmd.isBasicProcess()
        ){
            withContext(Dispatchers.Main){
                Toast.makeText(
                    context,
                    "Restart proc: lost base proc",
                    Toast.LENGTH_LONG
                ).show()
            }
            val processRestartDelayTime = 3000L
            withContext(Dispatchers.IO){
                LinuxCmd.killProcess(context.packageName)
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeUbuntu.START_UBUNTU_SERVICE.action
                )
                delay(processRestartDelayTime)
            }
        }
        withContext(Dispatchers.IO) {
            when (
                LinuxCmd.isProcessCheck(backgroundMonitorFileName)
            ) {
                true -> SshManager.execScriptAfterKill(
                    backgroundShellPath,
                    backgroundArgsTabSepaStr,
                    backgroundMonitorFileName,
                    false,
                )
                else -> SshManager.execScript(
                    backgroundShellPath,
                    backgroundArgsTabSepaStr,
                    backgroundMonitorFileName,
                    false,
                )
            }
        }
    }

    private fun execSell2Http(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val context = ubuntuService.applicationContext
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuLaunchCompFile.isFile
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Launch ubuntu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        val defaultTimeoutMiliSec = 2000
        val foregroundShellPath = intent.getStringExtra(
            UbuntuServerIntentExtra.foregroundShellPath.schema
        ) ?: return
        val args = intent.getStringExtra(
            UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema
        ) ?: String()
        val timeout = try {
            intent.getStringExtra(
                UbuntuServerIntentExtra.foregroundTimeout.schema
            )?.toInt() ?: defaultTimeoutMiliSec
        } catch (e: Exception){
            defaultTimeoutMiliSec
        }
        CoroutineScope(Dispatchers.IO).launch {
            Shell2Http.runCmd(
                foregroundShellPath,
                args,
                timeout
            )
        }
    }

    private fun execCmdKillByAdmin(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        val ubuntuCroutineJobTypeListForKill = intent.getStringExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobTypeListForKill.schema
        ) ?: return
        ubuntuService.ubuntuCoroutineJobsHashMap.get(ubuntuCroutineJobTypeListForKill)?.cancel()
        ubuntuFiles.let {
            BusyboxExecutor(ubuntuService.applicationContext, it).executeKillProcessFromList(
                ubuntuCroutineJobTypeListForKill.split("\t"),
                ubuntuService.cmdclickMonitorFileName
            )
        }
    }
}
