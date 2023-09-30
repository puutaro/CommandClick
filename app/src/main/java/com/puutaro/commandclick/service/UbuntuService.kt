package com.puutaro.commandclick.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.RESTART_OR_KILL_FRONT_SYSTEM
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.NotificationChanel
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServer
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServerForUbuntu
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.service.lib.ubuntu.ButtonLabel
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuProcessType
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuSetUp
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuStateType
import com.puutaro.commandclick.service.lib.ubuntu.WaitQuiz
import com.puutaro.commandclick.service.variable.ServiceNotificationId
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


class UbuntuService:
    Service() {

    private val languageType = LanguageTypeSelects.JAVA_SCRIPT
    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    private var isStartup = false
    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private val cmdclickMonitorFileName = UsePath.cmdClickMonitorFileName_2
    private var ubuntuFiles: UbuntuFiles? = null
    private var isTaskKill = false
    private var monitorScreenJob: Job? = null
    private var notificationBuilder:  NotificationCompat.Builder? = null
    private var ubuntuCoroutineJobsHashMap = HashMap<String, Job?>()
    private val notificationId = NotificationChanel.UBUNTU_NOTIFICATION.id
    private val chanelId = ServiceNotificationId.ubuntuServer
    private var notificationManager: NotificationManagerCompat? = null
    private var screenOffKill = false
    private val screenStatusReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Receive screen off
            when(intent.action) {
                Intent.ACTION_SCREEN_OFF
                -> monitorScreen()
                Intent.ACTION_SCREEN_ON
                -> launchRestartBroadcast()
            }
        }
    }

    private var broadcastReceiverForUbuntuServerIsActive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentScheme.IS_ACTIVE_UBUNTU_SERVICE.action
            ) return
            FileSystems.writeFile(
                UsePath.cmdclickTempUbuntuServiceDirPath,
                UsePath.cmdclickTmpUbuntuServiceActiveFileName,
                String()
            )
        }
    }
    private var broadcastReceiverForRestartOrKillSubFrontSystem: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentScheme.RESTART_OR_KILL_SUB_FRONT_SYSTEM.action
            ) return
            val startOrStop = intent.getStringExtra(
                UbuntuServerIntentExtra.restart_or_stop_front_system.schema
            )
            if(
                ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            ) return
            when(startOrStop){
                RESTART_OR_KILL_FRONT_SYSTEM.START.name
                -> {
                    startFrontProcess()
                }
                RESTART_OR_KILL_FRONT_SYSTEM.KILL.name
                -> {
                    killFrontProcess()
                }
            }
        }
    }
    private var broadcastReceiverForUbuntuServerStop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action
            ) return
            killAllProot()
            finishProcess()
            stopSelf()
            reLaunchUbuntuService()
        }
    }
    private var broadcastReceiverForUpdateProcessNum: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            ) return
            if(
                intent.action
                != BroadCastIntentScheme.UPDATE_PROCESS_NUM_UBUNTU_SERVICE.action
            ) return
            updateNotificationForProcessNum()
        }
    }
    private var broadcastReceiverForOpenFannel: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            ) return
            if(
                intent.action
                != BroadCastIntentScheme.OPEN_FANNEL.action
            ) return
            val fannelDirPath = intent.getStringExtra(
                UbuntuServerIntentExtra.fannelDirPath.schema
            ) ?: return
            val fannelName = intent.getStringExtra(
                UbuntuServerIntentExtra.fannelName.schema
            ) ?: String()
            launchFannelIntent(
                context,
                fannelDirPath,
                fannelName,
            )
        }
    }
    private var broadcastReceiverForUbuntuServerStart: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentScheme.START_UBUNTU_SERVICE.action
            ) return
            val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
                applicationContext,
                BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
            )
            notificationBuilder?.setContentTitle(UbuntuStateType.ON_SETUP.title)
            notificationBuilder?.setContentText(UbuntuStateType.ON_SETUP.message)
            notificationBuilder?.clearActions()
            notificationBuilder?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                ButtonLabel.RESTART.label,
                cancelUbuntuServicePendingIntent
            )
            notificationBuilder?.build()?.let {
                notificationManager?.notify(
                    chanelId,
                    it
                )
            }
            launchSetupMonitoring()
            launchInnerPulseServer()
            launchSetupUbuntu()
        }
    }
    private var broadcastReceiverForBackgroundCmdKill: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            ) return
            if(
                intent.action
                != BroadCastIntentScheme.BACKGROUND_CMD_KILL.action
            ) return
            val ubuntuCroutineJobType = intent.getStringExtra(
                UbuntuServerIntentExtra.ubuntuCroutineJobType.schema
            ) ?: return
            ubuntuCoroutineJobsHashMap.get(ubuntuCroutineJobType)?.cancel()
            ubuntuFiles?.let {
                BusyboxExecutor(applicationContext, it).executeKillProcess(
                    ubuntuCroutineJobType,
                    cmdclickMonitorFileName
                )
            }
            updateNotificationForProcessNum()
        }
    }
    private var broadcastReceiverForBackgroundCmdStart: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
            ) return
            if(
                intent.action
                != BroadCastIntentScheme.BACKGROUND_CMD_START.action
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
                    listOf("su","-", "cmdclick", "-c" ,"bash '$backgroundShellPath' $backgroundArgsTabSepaStr"),
                    monitorFileName = monitorFileName,
                )
            }
            ubuntuCoroutineJobsHashMap.get(
                backgroundShellPath,
            )?.cancel()
            ubuntuCoroutineJobsHashMap.put(
                backgroundShellPath,
                backgroundShellJob
            )
            updateNotificationForProcessNum()
        }
    }

    override fun onCreate() {
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerStop,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerStart,
            BroadCastIntentScheme.START_UBUNTU_SERVICE.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerIsActive,
            BroadCastIntentScheme.IS_ACTIVE_UBUNTU_SERVICE.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForBackgroundCmdKill,
            BroadCastIntentScheme.BACKGROUND_CMD_KILL.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForBackgroundCmdStart,
            BroadCastIntentScheme.BACKGROUND_CMD_START.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForUpdateProcessNum,
            BroadCastIntentScheme.UPDATE_PROCESS_NUM_UBUNTU_SERVICE.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForOpenFannel,
            BroadCastIntentScheme.OPEN_FANNEL.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForRestartOrKillSubFrontSystem,
            BroadCastIntentScheme.RESTART_OR_KILL_SUB_FRONT_SYSTEM.action
        )
        BroadcastManagerForService.registerScreenOnOffReceiver(
            this,
            screenStatusReceiver,
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if(
            isStartup
        ) {
            isStartup = true
            return START_STICKY
        }
        if(
            intent?.getStringExtra(
                UbuntuServerIntentExtra.ubuntuStartCommand.schema
            ).isNullOrEmpty()
        ) return START_STICKY
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        if(isTaskKill) {
            isTaskKill = false
            notificationBuilder?.clearActions()
            notificationBuilder?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                ButtonLabel.RESTART.label,
                cancelUbuntuServicePendingIntent
            )
            addOpenTerminalButton(notificationBuilder)
            val notificationInstance = notificationBuilder?.build()
            notificationInstance?.let {
                notificationManager?.notify(
                    chanelId,
                    it
                )
            }
            return START_STICKY
        }
        ubuntuFiles = UbuntuFiles(
            applicationContext,
        )
        val channel = NotificationChannel(
            NotificationChanel.UBUNTU_NOTIFICATION.id,
            NotificationChanel.UBUNTU_NOTIFICATION.name,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.setSound(null, null)
        notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager?.createNotificationChannel(channel)
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationId
        )
            .setSmallIcon(com.puutaro.commandclick.R.drawable.ic_terminal)
            .setAutoCancel(true)
            .setContentTitle(UbuntuStateType.WAIT.title)
            .setContentText(UbuntuStateType.WAIT.message)
            .setDeleteIntent(
                cancelUbuntuServicePendingIntent
            )
        ubuntuFiles = UbuntuFiles(applicationContext)
        if(ubuntuFiles?.ubuntuSetupCompFile?.isFile == true) {
            notificationBuilder?.setContentTitle(UbuntuStateType.WAIT.title)
            notificationBuilder?.setContentText(UbuntuStateType.WAIT.message)
        } else {
            notificationBuilder?.setContentTitle(UbuntuStateType.UBUNTU_SETUP_WAIT.title)
            notificationBuilder?.setContentText(UbuntuStateType.UBUNTU_SETUP_WAIT.message)
        }
        if(
            ubuntuFiles?.ubuntuSetupCompFile?.isFile != true
            && !NetworkTool.isWifi(applicationContext)
        ) {
            notificationBuilder?.setContentTitle(UbuntuStateType.WIFI_WAIT.title)
            notificationBuilder?.setContentText(UbuntuStateType.WIFI_WAIT.message)
            notificationBuilder?.clearActions()
            notificationBuilder?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                ButtonLabel.RESTART.label,
                cancelUbuntuServicePendingIntent,
            )
            val notificationInstance = notificationBuilder?.build()
            notificationBuilder?.build()?.let {
                notificationManager?.notify(
                    ServiceNotificationId.ubuntuServer,
                    it
                )
            }
            startForeground(
                chanelId,
                notificationInstance
            )
            monitorProcessNum()
            return START_STICKY
        }
        if(
            ubuntuFiles?.ubuntuSetupCompFile?.isFile != true
        ) {
            val startUbuntuServicePendingIntent = PendingIntentCreator.create(
                applicationContext,
                BroadCastIntentScheme.START_UBUNTU_SERVICE.action,
            )
            notificationBuilder?.clearActions()
            notificationBuilder?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                ButtonLabel.SETUP.label,
                startUbuntuServicePendingIntent
            )
            val notificationInstance = notificationBuilder?.build()
            notificationInstance?.let {
                notificationManager?.notify(
                    chanelId,
                    it
                )
            }
            startForeground(
                chanelId,
                notificationInstance
            )
            monitorProcessNum()
            return START_STICKY
        }
        notificationBuilder?.clearActions()
        notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.icons8_cancel,
            ButtonLabel.RESTART.label,
            cancelUbuntuServicePendingIntent
        )
        val notificationInstance = notificationBuilder?.build()
        notificationInstance?.let {
            notificationManager?.notify(
                chanelId,
                it
            )
        }
        startForeground(
            chanelId,
            notificationInstance
        )
        monitorProcessNum()

        launchSetupMonitoring()
        launchInnerPulseServer()
        launchSetupUbuntu()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        finishProcess()
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        continueForeground()
//        finishProcess()
    }

    private fun finishProcess(){
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerStop,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerStart,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerIsActive,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForBackgroundCmdKill,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForBackgroundCmdStart,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForUpdateProcessNum,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForOpenFannel,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            screenStatusReceiver,
        )
        PcPulseSetServerForUbuntu.exit()
        UbuntuSetUp.killAllCoroutineJob(ubuntuCoroutineJobsHashMap)
        PcPulseSetServer.exit()
        notificationManager?.cancel(chanelId)
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun launchSetupUbuntu(){
        ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUp.name]?.cancel()
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_3,
            "### ${LocalDateTime.now()} proot"
        )
        val setupUbuntuJob = UbuntuSetUp.set(
            this@UbuntuService,
            UsePath.cmdClickMonitorFileName_2,
            notificationManager,
            notificationBuilder
        )
        ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUp.name] = setupUbuntuJob
    }

    private fun launchSetupMonitoring(){
        ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUpMonitoring.name]?.cancel()
        val ubuntuLaunchCompFile = ubuntuFiles?.ubuntuLaunchCompFile
            ?: return
        if(
            ubuntuFiles?.ubuntuSetupCompFile?.isFile != true
            && !NetworkTool.isWifi(applicationContext)
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
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        val waitQuiz = WaitQuiz(applicationContext)
        val setupMonitoringJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                while(true) {
                    delay(2000)
                    if(
                        ubuntuFiles?.ubuntuLaunchCompFile?.isFile == true
                    ) break
                    val monitorLastLine = ReadText(
                        cmdclickMonitorDirPath,
                        cmdclickMonitorFileName
                    ).textToList().lastOrNull() ?: continue
                    notificationBuilder?.setContentTitle("${UbuntuStateType.ON_SETUP.title}\t${monitorLastLine}")
                    notificationBuilder?.setContentText(waitQuiz.echoQorA())
                    notificationBuilder?.build()?.let {
                        notificationManager?.notify(
                            chanelId,
                            it
                        )
                    }
                }
            }
            withContext(Dispatchers.IO){
                delay(500)
                notificationBuilder?.setContentTitle(UbuntuStateType.RUNNING.title)
                val itSelfProcessNum = 1
                notificationBuilder?.setContentText(
                    UbuntuStateType.RUNNING.message.format(
                        processNumCalculator() - itSelfProcessNum
                    )
                )
                notificationBuilder?.clearActions()
                notificationBuilder?.addAction(
                    com.puutaro.commandclick.R.drawable.icons8_cancel,
                    ButtonLabel.RESTART.label,
                    cancelUbuntuServicePendingIntent
                )
                addOpenTerminalButton(notificationBuilder)
                notificationBuilder?.build()?.let {
                    notificationManager?.notify(
                        chanelId,
                        it
                    )
                }
                ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUpMonitoring.name]?.cancel()
            }
        }
        ubuntuCoroutineJobsHashMap[UbuntuProcessType.SetUpMonitoring.name] =
            setupMonitoringJob
    }

    private fun reLaunchUbuntuService(){
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = BroadCastIntentScheme.RESTART_UBUNTU_SERVICE_FROM_ACTIVITY.action
        sendBroadcast(intent)
    }

    private fun continueForeground(){
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        notificationBuilder?.clearActions()
        notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.icons8_cancel,
            ButtonLabel.RESTART.label,
            cancelUbuntuServicePendingIntent
        )
        addOpenTerminalButton(notificationBuilder)
        val notificationInstance = notificationBuilder?.build()
        notificationInstance?.let {
            notificationManager?.notify(
                chanelId,
                it
            )
        }
        val intent = Intent(
            this,
            this::class.java
        )
        startForegroundService(intent)
        isTaskKill = true
    }

    fun launchInnerPulseServer(){
        val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        val pulseaudioSetupName = UbuntuProcessType.PulseaudioSetUp.name
        ubuntuCoroutineJobsHashMap[pulseaudioSetupName]?.cancel()
        val pulseaudioSetUpJob = CoroutineScope(Dispatchers.IO).launch {
            notificationManager?.let {
                PcPulseSetServerForUbuntu.exit()
                delay(100)
                PcPulseSetServerForUbuntu.launch(
                    applicationContext,
                    "127.0.0.1",
                    UsePort.UBUNTU_PULSE_RECEIVER_PORT.num.toString(),
                    notificationId,
                    chanelId,
                    UsePort.UBUNTU_PC_PULSE_SET_SERVER_PORT.num.toString(),
                    it,
                    cancelUbuntuServicePendingIntent
                )
            }
        }
        ubuntuCoroutineJobsHashMap.put(
            pulseaudioSetupName,
            pulseaudioSetUpJob
        )
    }

    private fun updateNotificationForProcessNum(){
        notificationBuilder?.setContentText(
            UbuntuStateType.RUNNING.message.format(processNumCalculator())
        )
        notificationBuilder?.build()?.let {
            notificationManager?.notify(
                chanelId,
                it
            )
        }
    }

    private fun processNumCalculator(): Int {
        return ubuntuCoroutineJobsHashMap.filter {
            val job = it.value
            job != null && job.isActive
        }.size
    }

    private fun addOpenTerminalButton(
        currentNotificationBuilder: NotificationCompat.Builder?
    ){
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        currentNotificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.ic_terminal,
            ButtonLabel.TERMINAL.label,
            createOpenTerminalPendingIntent()
        )
    }

    private fun launchFannelIntent(
        context: Context?,
        fannelDirPath: String,
        fannelName: String,
    ){
        val execIntent = Intent(context, MainActivity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        execIntent.putExtra(
            SharePrefferenceSetting.current_app_dir.name,
            fannelDirPath
        )
        execIntent.putExtra(
            SharePrefferenceSetting.current_script_file_name.name,
            fannelName
        )
        context?.startActivity(execIntent)
    }

    private fun createOpenTerminalPendingIntent(): PendingIntent {
        return PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.OPEN_FANNEL.action,
            listOf(
                Pair(
                    UbuntuServerIntentExtra.fannelDirPath.schema,
                    UsePath.cmdclickSystemAppDirPath
                ),
                Pair(
                    UbuntuServerIntentExtra.fannelName.schema,
                    SystemFannel.cmdTerminal
                ),
            )
        )
    }

    private fun killAllProot(){
        ubuntuFiles?.let {
            BusyboxExecutor(applicationContext, it).executeKillAllProcess(
                cmdclickMonitorFileName
            )
        }
    }


    private fun startFrontProcess(){
        ubuntuFiles?.let {
            BusyboxExecutor(applicationContext, it).executeStartFrontProcess(
                cmdclickMonitorFileName
            )
        }
    }
    private fun killFrontProcess(){
        ubuntuFiles?.let {
            BusyboxExecutor(applicationContext, it).executeKillFrontProcess(
                cmdclickMonitorFileName
            )
        }
    }

    private fun killSubFrontProcess(){
        ubuntuFiles?.let {
            BusyboxExecutor(applicationContext, it).executeKillSubFrontProcess(
                cmdclickMonitorFileName
            )
        }
    }

    private fun launchRestartBroadcast(){
        if(!screenOffKill) return
        monitorScreenJob?.cancel()
        notificationBuilder?.setContentTitle("Sleeping...")
        notificationBuilder?.setContentText("...")
        notificationBuilder?.build()?.let {
            notificationManager?.notify(
                chanelId,
                it
            )
        }
//        killAllProot()
//        killAllProcess()
//        finishProcess()
//        stopSelf()
        screenOffKill = false
//        reLaunchUbuntuService()
    }

    private fun monitorScreen(){
        val systemProcessNum = UbuntuProcessType.values().size - 1
        monitorScreenJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val sleepDelayMinutes = makeSleepDelayMinutes()
                if(sleepDelayMinutes == 0L) return@withContext
                delay(sleepDelayMinutes)
                val processNum = processNumCalculator()
                if (
                    processNum > systemProcessNum
                ) return@withContext
                screenOffKill = true
                killFrontProcess()
                killSubFrontProcess()
//                killAllProot()
            }
        }
    }

    private fun monitorProcessNum(){
        var previousProcessNum = 0
        ubuntuCoroutineJobsHashMap[UbuntuProcessType.monitoringProcessNum.name]?.cancel()
        val processNumUpdateIntent = Intent()
        processNumUpdateIntent.action =
            BroadCastIntentScheme.UPDATE_PROCESS_NUM_UBUNTU_SERVICE.action
        val monitorProcessNumJob = CoroutineScope(Dispatchers.IO).launch {
            while(true){
                delay(500)
                if(
                    ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
                ) continue
                val currentProcessNum = processNumCalculator()
                if(
                    previousProcessNum == currentProcessNum
                ) continue
                previousProcessNum = currentProcessNum
                applicationContext.sendBroadcast(processNumUpdateIntent)
            }
        }
        ubuntuCoroutineJobsHashMap[
                UbuntuProcessType.monitoringProcessNum.name
        ] = monitorProcessNumJob
    }

    private fun makeSleepDelayMinutes(): Long {
        val defaultDelaySleepTime = 20L
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            CommandClickVariables.makeScriptContentsList(
                UsePath.cmdclickSystemAppDirPath,
                UsePath.cmdclickConfigFileName
            ),
            settingSectionStart,
            settingSectionEnd
        )
        val sleepDelayMinutesStr = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF,
            defaultDelaySleepTime.toString()
        )
        return try{
            convertMiliSecToMinutes(
                sleepDelayMinutesStr.toLong()
            )
        } catch(e: Exception){
            convertMiliSecToMinutes(
                defaultDelaySleepTime
            )
        }
    }

    private fun convertMiliSecToMinutes(miliTime: Long): Long {
        return miliTime * 1000 * 60
    }
}