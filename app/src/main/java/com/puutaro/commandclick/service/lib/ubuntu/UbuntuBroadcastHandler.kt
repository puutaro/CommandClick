package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Intent
import com.anggrayudi.storage.file.getAbsolutePath
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.Shell2Http
import com.puutaro.commandclick.proccess.ubuntu.SshManager
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuProcessChecker
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.IntentManager
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuServiceButton
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuProcessManager
import com.puutaro.commandclick.service.lib.ubuntu.libs.RestoreLabel
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuServerServiceManager
import com.puutaro.commandclick.service.lib.ubuntu.variable.UbuntuNotiButtonLabel
import com.puutaro.commandclick.service.lib.ubuntu.variable.UbuntuStateType
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.SdCardTool
import com.puutaro.commandclick.util.file.SdFileSystems
import com.puutaro.commandclick.util.file.SdPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
            BroadCastIntentSchemeUbuntu.DOWN_LOAD_ERR_NOTI
            -> execDownloadErrNoti(
                ubuntuService,
                intent,
            )
            BroadCastIntentSchemeUbuntu.COPY_TO_SD_CARD
            -> execCopyDirToSdCard(
                ubuntuService,
                intent,
            )
            BroadCastIntentSchemeUbuntu.DELETE_FROM_SD_CARD
            -> execDeleteFromSdCard(
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
                UbuntuProcessManager.processNumCalculator(ubuntuService) - itSelfProcessNum
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
        ubuntuService.ubuntuCoroutineJobsHashMap[UbuntuProcessManager.UbuntuInitProcessType.SetUpMonitoring.name]?.cancel()
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
        UbuntuSetUp.exitDownloadMonitorProcess()
        UbuntuProcessManager.finishProcess(ubuntuService)
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
            UbuntuProcessManager.UbuntuRunningSystemProcessType.values().size
        ubuntuService.notificationBuilder?.setContentTitle(UbuntuStateType.RUNNING.title)
        val userProotProcessNum =
            UbuntuProcessManager.processNumCalculator(ubuntuService) - systemRunningProcessNum
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
        val backgroundResFilePath = intent.getStringExtra(
            UbuntuServerIntentExtra.backgroundResFilePath.schema
        )
        ubuntuService.ubuntuCoroutineJobsHashMap.get(
            backgroundShellPath,
        )?.cancel()
        val useMonitorFilename = when(
            backgroundResFilePath.isNullOrEmpty()
        ){
            true -> backgroundMonitorFileName
            else -> null
        }
        val backgroundShellJob = CoroutineScope(Dispatchers.IO).launch {
            val output = withContext(Dispatchers.IO) {
                execBackgroundCmdStartHandler(
                    ubuntuService,
                    backgroundShellPath,
                    backgroundArgsTabSepaStr,
                    useMonitorFilename,
                )
            }
            if (
                backgroundResFilePath.isNullOrEmpty()
            ) return@launch
            withContext(Dispatchers.IO) {
                FileSystems.writeFile(
                    backgroundResFilePath,
                    output
                )
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
        useMonitorFileName: String?,
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
                    noTimeout,
                    useMonitorFileName
                )
                else -> Shell2Http.runScript(
                    context,
                    shellPath,
                    argsTabSepaStr,
                    noTimeout,
                    useMonitorFileName
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
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                Shell2Http.runScript(
                    context,
                    foregroundShellPath,
                    foregroundArgsTabSepaStr,
                    timeout,
                    null,
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

    private fun execDownloadErrNoti(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        ubuntuService.notificationBuilder?.setContentTitle(
            UbuntuStateType.DOWNLOAD_ERR.title
        )
        ubuntuService.notificationBuilder?.setContentText(
            UbuntuStateType.DOWNLOAD_ERR.message
        )
        ubuntuService.notificationBuilder?.clearActions()
        ubuntuService.notificationBuilder?.addAction(
            R.drawable.icons8_cancel,
            UbuntuNotiButtonLabel.RESTART.label,
            ubuntuService.cancelUbuntuServicePendingIntent
        )
        ubuntuService.notificationBuilder?.build()?.let {
            ubuntuService.notificationManager?.notify(
                ubuntuService.chanelId,
                it
            )
        }
    }

    private fun execCopyDirToSdCard(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val context = ubuntuService.applicationContext
        val ubuntuBackupSharePref = SdCardTool.getSharePref(context)
        val watchFilePathObj =  intent.getStringExtra(
            UbuntuServerIntentExtra.copyToSdCardWatchFilePath.schema
        )?.let { File(it) } ?: return
        if(
            ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            || !SdCardTool.isAvailable(context)
        ) {
            FileSystems.removeFiles(watchFilePathObj.absolutePath)
            return
        }
        val fromPathObj = intent.getStringExtra(
            UbuntuServerIntentExtra.copyToSdCardFromRelativePath.schema
        )?.let {
            if(
                !it.startsWith("/")
            ) return@let File(
                SdPath.getSdUseRootPath(),
                it
            )
            val ubuntuFromPath = CcPathTool.convertUbuntuPath(
                context,
                it,
            )
            File(ubuntuFromPath)
        } ?: let {
            FileSystems.removeFiles(
                watchFilePathObj.absolutePath
            )
            return
        }
        val toRelativeDirPath = intent.getStringExtra(
            UbuntuServerIntentExtra.copyToSdCardTo.schema
        )?.let {
            val sdTreePath = SdCardTool.getTreeUri(
                context,
                ubuntuBackupSharePref,
            )?.getAbsolutePath(context)
                ?: return@let it
            it.replace(
                sdTreePath,
                String()
            ).removePrefix("/")
        } ?: let {
            FileSystems.removeFiles(
                watchFilePathObj.absolutePath
            )
            return
        }
        val cpDirToSdJobName = "${CommandClickScriptVariable.makeRndPrefix()}_cpDirToSdJob"
        ubuntuService.ubuntuCoroutineJobsHashMap[cpDirToSdJobName]?.cancel()
        val cpDirToSdJob = CoroutineScope(Dispatchers.IO).launch {
            val watchJob = launch {
                while(true){
                    if(
                        !watchFilePathObj.isFile
                    ) {
                        ubuntuService.ubuntuCoroutineJobsHashMap[cpDirToSdJobName]?.cancel()
                        ubuntuService.ubuntuCoroutineJobsHashMap.remove(cpDirToSdJobName)
                        break
                    }
                    delay(1000)
                }
            }
            launch {
                when(true) {
                    fromPathObj.isDirectory ->
                    SdFileSystems.CopyDirRecursively.copy(
                        context,
                        ubuntuBackupSharePref,
                        fromPathObj.absolutePath,
                        toRelativeDirPath,
                        watchFilePathObj,
                        watchJob,
                    )
                    fromPathObj.isFile ->
                        SdFileSystems.Copy.copy(
                            context,
                            ubuntuBackupSharePref,
                            fromPathObj.absolutePath,
                            toRelativeDirPath,
                            watchFilePathObj,
                        )
                    else -> FileSystems.removeFiles(
                        watchFilePathObj.absolutePath
                    )
                }
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.put(cpDirToSdJobName, cpDirToSdJob)
    }

    private fun execDeleteFromSdCard(
        ubuntuService: UbuntuService,
        intent: Intent,
    ){
        val context = ubuntuService.applicationContext
        val ubuntuBackupSharePref = SdCardTool.getSharePref(context)
        val watchFilePathObj =  intent.getStringExtra(
            UbuntuServerIntentExtra.deleteFromSdWatchFilePath.schema
        )?.let { File(it) } ?: return
        if(
            ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            || !SdCardTool.isAvailable(context)
        ) {
            FileSystems.removeFiles(watchFilePathObj.absolutePath)
            return
        }
        val fromRelativePath = intent.getStringExtra(
            UbuntuServerIntentExtra.copyToSdCardFromRelativePath.schema
        )?.let {
            val sdTreePath = SdCardTool.getTreeUri(
                context,
                ubuntuBackupSharePref,
            )?.getAbsolutePath(context)
                ?: return@let it
            it.replace(
                sdTreePath,
                String()
            ).removePrefix("/")
        } ?: return
        val relativePathObj = File(fromRelativePath)
        val relativeDirPath = relativePathObj.parent
        val deleteName = relativePathObj.name
        val deleteFromSdJobName = "${CommandClickScriptVariable.makeRndPrefix()}_deleteFromSd"
        ubuntuService.ubuntuCoroutineJobsHashMap[deleteFromSdJobName]?.cancel()
        val cpDirToSdJob = CoroutineScope(Dispatchers.IO).launch {
            launch {
                while(true){
                    if(
                        !watchFilePathObj.isFile
                    ) {
                        ubuntuService.ubuntuCoroutineJobsHashMap[deleteFromSdJobName]?.cancel()
                        ubuntuService.ubuntuCoroutineJobsHashMap.remove(deleteFromSdJobName)
                        break
                    }
                    delay(1000)
                }
            }
            launch {
                SdFileSystems.removeDirOrFile(
                    context,
                    ubuntuBackupSharePref,
                    relativeDirPath,
                    deleteName,
                    watchFilePathObj
                )
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap.put(deleteFromSdJobName, cpDirToSdJob)
    }

}
