package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.Shell2Http
import com.puutaro.commandclick.proccess.ubuntu.SshManager
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuInfo
import com.puutaro.commandclick.proccess.ubuntu.UbuntuProcessChecker
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
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object UbuntuBroadcastHandler {
    fun handle(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val action = intent.action
        val broadCastIntentSchemeUbuntu = BroadCastIntentSchemeUbuntu.values().firstOrNull {
            it.action == action
        } ?: return
        when(broadCastIntentSchemeUbuntu){
            BroadCastIntentSchemeUbuntu.START_UBUNTU_SERVICE
            -> execStartUbuntuService(
                ubuntuService,
                intent,
            )
            BroadCastIntentSchemeUbuntu.ON_RUNNING_NOTIFICATION
            -> execRunningNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.WIFI_WAIT_NITIFICATION
            -> execWifiNotification(
                ubuntuService,
            )
            BroadCastIntentSchemeUbuntu.ON_UBUNTU_SETUP_NOTIFICATION
            -> execOnUbuntuSetupNotification(
                    ubuntuService
                )
            BroadCastIntentSchemeUbuntu.ON_UBUNTU_SETUP_QUIZ_NOTIFICATION
            -> execOnUbuntuSetupQuizNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.IS_ACTIVE_UBUNTU_SERVICE
            -> execIsActiveUbuntuService()
            BroadCastIntentSchemeUbuntu.STOP_UBUNTU_SERVICE
            -> execStopUbuntuService(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.UPDATE_PROCESS_NUM_NOTIFICATION
            -> execUpdateProcessNumNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.ON_SLEEPING_NOTIFICATION
            -> execSleepingNotification(
                ubuntuService
            )
            BroadCastIntentSchemeUbuntu.OPEN_FANNEL
            -> execOpenFannel(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.RESTART_UBUNTU_SERVICE_FROM_ACTIVITY -> {}
//            BroadCastIntentSchemeUbuntu.ADMIN_CMD_START.action
//            -> execAdminCmdStart(
//                ubuntuService,
//                intent
//            )
            BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START
            -> execBackGroundCmdStart(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START
            -> execForegroundCmd(
                ubuntuService,
                intent
            )
            BroadCastIntentSchemeUbuntu.CMD_KILL_BY_ADMIN
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
            File(
                cmdclickMonitorDirPath,
                cmdclickMonitorFileName
            ).absolutePath
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
            File(
                UsePath.cmdclickTempUbuntuServiceDirPath,
                UsePath.cmdclickTmpUbuntuServiceActiveFileName
            ).absolutePath,
            String()
        )
    }

    private fun execStopUbuntuService(
        ubuntuService: UbuntuService
    ){
//        ProcessManager.killAllProot(ubuntuService)
        LinuxCmd.killProcess(
            ubuntuService.applicationContext,
        )
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
                ToastUtils.showShort("Launch ubuntu")
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

//    private fun execAdminCmdStart(
//        ubuntuService: UbuntuService,
//        intent: Intent
//    ){
//        val context = ubuntuService.applicationContext
//        val ubuntuFiles = ubuntuService.ubuntuFiles
//        if(
//            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
//        ) {
//            CoroutineScope(Dispatchers.Main).launch {
//                ToastUtils.showShort("Launch ubuntu")
//            }
//            return
//        }
//        val adminShellPath = intent.getStringExtra(
//            UbuntuServerIntentExtra.adminShellPath.schema
//        ) ?: return
//        val adminArgsTabSepaStr = intent.getStringExtra(
//            UbuntuServerIntentExtra.adminArgsTabSepaStr.schema
//        ) ?: String()
//        val adminMonitorFileName = intent.getStringExtra(
//            UbuntuServerIntentExtra.adminMonitorFileName.schema
//        ) ?: return
//        val adminArgsTabSepaStrWithQuote = adminArgsTabSepaStr.split("\t").map {
//            "\"$it\""
//        }.joinToString("\t")
//        val adminShellJob = CoroutineScope(Dispatchers.IO).launch {
//            BusyboxExecutor(
//                context,
//                UbuntuFiles(context)
//            ).executeProotCommand(
//                listOf("su","-", UbuntuInfo.user, "-c" ,"bash '$adminShellPath' $adminArgsTabSepaStrWithQuote"),
//                monitorFileName = adminMonitorFileName,
//            )
//        }
//        ubuntuService.ubuntuCoroutineJobsHashMap.get(
//            adminShellPath,
//        )?.cancel()
//        ubuntuService.ubuntuCoroutineJobsHashMap.put(
//            adminShellPath,
//            adminShellJob
//        )
//    }

    private fun execBackGroundCmdStart(
        ubuntuService: UbuntuService,
        intent: Intent
    ){
        if(
            !UbuntuProcessChecker.isExist(
                ubuntuService.applicationContext,
                ubuntuService.ubuntuFiles
            )
        ) return
        val backgroundShellPath = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundShellPath.schema
        ) ?: return
        val backgroundArgsTabSepaStr = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema
        ) ?: String()
        val backgroundMonitorFileName = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundMonitorFileName.schema
        ) ?: UsePath.cmdClickMonitorFileName_2
        val backgroundMonitorFilePath = File(
            UsePath.cmdclickMonitorDirPath,
            backgroundMonitorFileName
        ) .absolutePath
        val backgroundResFilePath = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundResFilePath.schema
        )
        ubuntuService.ubuntuCoroutineJobsHashMap.get(
            backgroundShellPath,
        )?.cancel()
        val backgroundShellJob = CoroutineScope(Dispatchers.IO).launch {
            val output = withContext(Dispatchers.IO) {
                execBackgroundCmdStartHandler(
                    ubuntuService,
                    backgroundShellPath,
                    backgroundArgsTabSepaStr,
                )
            }
            withContext(Dispatchers.IO) {
                when (
                    backgroundResFilePath.isNullOrEmpty()
                ) {
                    false -> FileSystems.writeFile(
                        backgroundResFilePath,
                        output
                    )
                    else -> FileSystems.updateFile(
                        backgroundMonitorFilePath,
                        output
                    )
                }
            }
//            execBackGroundCmdStartHandler2(
//                ubuntuService,
//                backgroundShellPath,
//                backgroundArgsTabSepaStr,
//                backgroundMonitorFileName,
//                backgroundResFilePath,
//            )
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.put(
            backgroundShellPath,
            backgroundShellJob
        )
    }

    private suspend fun execBackgroundCmdStartHandler(
        ubuntuService: UbuntuService,
        shellPath: String,
        argsTabSepaStr: String,
    ): String{
        val context = ubuntuService.applicationContext
        val noTimeout = 0
        return withContext(Dispatchers.IO) {
            when (
                LinuxCmd.isProcessCheck(
                    context,
                    shellPath
                )
            ) {
                true -> Shell2Http.runScriptAfterKill(
                    context,
                    shellPath,
                    argsTabSepaStr,
                    noTimeout
                )
                else -> Shell2Http.runScript(
                    context,
                    shellPath,
                    argsTabSepaStr,
                    noTimeout
                )
            }
        }
    }

    private suspend fun execBackGroundCmdStartHandler2(
        ubuntuService: UbuntuService,
        backgroundShellPath: String,
        backgroundArgsTabSepaStr: String,
        backgroundMonitorFileName: String,
        backgroundResFilePath: String?,
    ){
        val context = ubuntuService.applicationContext
        if(
            !LinuxCmd.isBasicProcess(context)
        ){
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeUbuntu.STOP_UBUNTU_SERVICE.action
            )
            return
        }
        val isOutput = !backgroundResFilePath.isNullOrEmpty()
        val output = withContext(Dispatchers.IO) {
            when (
                LinuxCmd.isProcessCheck(
                    context,
                    backgroundShellPath
                )
            ) {
                true -> SshManager.execScriptAfterKill(
                    context,
                    backgroundShellPath,
                    backgroundArgsTabSepaStr,
                    backgroundMonitorFileName,
                    isOutput,
                )
                else -> SshManager.execScript(
                    context,
                    backgroundShellPath,
                    backgroundArgsTabSepaStr,
                    backgroundMonitorFileName,
                    isOutput,
                )
            }
        }
        if (
            backgroundResFilePath.isNullOrEmpty()
        ) return
        withContext(Dispatchers.IO){
            FileSystems.writeFile(
                backgroundResFilePath,
                output
            )
        }
    }

    private fun execForegroundCmd(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val context = ubuntuService.applicationContext
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuLaunchCompFile.isFile
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                ToastUtils.showShort("Launch ubuntu")
            }
            return
        }
        val defaultTimeoutMiliSec = 2000
        val foregroundShellPath = intent.getStringExtra(
            UbuntuServerIntentExtra.foregroundShellPath.schema
        ) ?: return
        val foregroundArgsTabSepaStr = intent.getStringExtra(
            UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema
        ) ?: String()
        val timeout = try {
            intent.getStringExtra(
                UbuntuServerIntentExtra.foregroundTimeout.schema
            )?.toInt() ?: defaultTimeoutMiliSec
        } catch (e: Exception){
            defaultTimeoutMiliSec
        }
//        val foregroundResFilePath =
//            intent.getStringExtra(
//                UbuntuServerIntentExtra.foregroundResFilePath.schema
//            )
//        val onForegroundAsProc =
//            !intent.getStringExtra(
//                UbuntuServerIntentExtra.foregroundAsProc.schema
//            ).isNullOrEmpty()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                Shell2Http.runScript(
                    context,
                    foregroundShellPath,
                    foregroundArgsTabSepaStr,
                    timeout
                )
            }
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
        val context = ubuntuService.applicationContext
        val ubuntuCroutineJobTypeListForKill = intent.getStringExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobTypeListForKill.schema
        ) ?: return
        ubuntuService.ubuntuCoroutineJobsHashMap.get(ubuntuCroutineJobTypeListForKill)?.cancel()
        val ubuntuCroutineJobTypeListForKillList =
            ubuntuCroutineJobTypeListForKill.split("\t")
        ubuntuCroutineJobTypeListForKillList.forEach {
            LinuxCmd.killCertainProcess(
                context,
                it,
            )
        }
//        ubuntuFiles.let {
//            BusyboxExecutor(ubuntuService.applicationContext, it).executeKillProcessFromList(
//                ubuntuCroutineJobTypeListForKill.split("\t"),
//                ubuntuService.cmdclickMonitorFileName
//            )
//        }
    }
}
