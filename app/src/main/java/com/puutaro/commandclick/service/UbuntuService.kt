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
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.NotificationChanel
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.ubuntu.BroadcastScreenSwitchHandler
import com.puutaro.commandclick.service.lib.ubuntu.ButtonLabel
import com.puutaro.commandclick.service.lib.ubuntu.ForegroundContinue
import com.puutaro.commandclick.service.lib.ubuntu.InnerPulseServer
import com.puutaro.commandclick.service.lib.ubuntu.SetupMonitoring
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuBroadcastHandler
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuInitProcess
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuStateType
import com.puutaro.commandclick.service.lib.ubuntu.WaitQuiz
import com.puutaro.commandclick.service.lib.ubuntu.libs.IntentRequestMonitor
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import com.puutaro.commandclick.service.variable.ServiceNotificationId
import com.puutaro.commandclick.util.NetworkTool
import kotlinx.coroutines.Job
import java.net.ServerSocket


class UbuntuService:
    Service() {

    val languageType = LanguageTypeSelects.JAVA_SCRIPT
    val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    var isStartup = false
    val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    val cmdclickMonitorFileName = UsePath.cmdClickMonitorFileName_2
    var ubuntuFiles: UbuntuFiles? = null
    var isTaskKill = false
    var monitorScreenJob: Job? = null
    var notificationBuilder:  NotificationCompat.Builder? = null
    var ubuntuCoroutineJobsHashMap = HashMap<String, Job?>()
    val notificationId = NotificationChanel.UBUNTU_NOTIFICATION.id
    val chanelId = ServiceNotificationId.ubuntuServer
    var notificationManager: NotificationManagerCompat? = null
    var cancelUbuntuServicePendingIntent: PendingIntent? = null
    var screenOffKill = false
    val waitQuiz = WaitQuiz()
    val notificationBuilderHashMap = HashMap<Int,  NotificationCompat.Builder>()
    var intentMonitorServerSocket: ServerSocket? = null
    val screenStatusReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Receive screen off
            BroadcastScreenSwitchHandler.handle(
                this@UbuntuService,
                intent
            )
        }
    }

    var broadcastReceiverForUbuntuServerProcess: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            UbuntuBroadcastHandler.handle(
                this@UbuntuService,
                intent,
            )
        }
    }

    override fun onCreate() {
        cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
        )
        BroadcastManagerForService.registerActionListBroadcastReceiver(
            this,
            broadcastReceiverForUbuntuServerProcess,
            listOf(
                BroadCastIntentScheme.START_UBUNTU_SERVICE.action,
                BroadCastIntentScheme.WIFI_WAIT_NITIFICATION.action,
                BroadCastIntentScheme.ON_UBUNTU_SETUP_NOTIFICATION.action,
                BroadCastIntentScheme.ON_UBUNTU_SETUP_QUIZ_NOTIFICATION.action,
                BroadCastIntentScheme.ON_RUNNING_NOTIFICATION.action,
                BroadCastIntentScheme.IS_ACTIVE_UBUNTU_SERVICE.action,
                BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
                BroadCastIntentScheme.UPDATE_PROCESS_NUM_NOTIFICATION.action,
                BroadCastIntentScheme.ON_SLEEPING_NOTIFICATION.action,
                BroadCastIntentScheme.OPEN_FANNEL.action,
                BroadCastIntentScheme.ADMIN_CMD_START.action,
                BroadCastIntentScheme.BACKGROUND_CMD_START.action,
                BroadCastIntentScheme.FOREGROUND_CMD_START.action,
                BroadCastIntentScheme.CMD_KILL_BY_ADMIN.action,
            )
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
        if(isStartup) {
            isStartup = true
            return START_NOT_STICKY
        }
        if(
            intent?.getStringExtra(
                UbuntuServerIntentExtra.ubuntuStartCommand.schema
            ).isNullOrEmpty()
        ) return START_NOT_STICKY
        if(isTaskKill) {
            isTaskKill = false
            val processNumUpdateIntent = Intent()
            processNumUpdateIntent.action =
                BroadCastIntentScheme.UPDATE_PROCESS_NUM_NOTIFICATION.action
            sendBroadcast(processNumUpdateIntent)
            return START_NOT_STICKY
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
            ProcessManager.monitorProcessAndNum(this)
            IntentRequestMonitor.launch(this)
            return START_NOT_STICKY
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
            ProcessManager.monitorProcessAndNum(this)
            IntentRequestMonitor.launch(this)
            return START_NOT_STICKY
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
        ProcessManager.monitorProcessAndNum(this)
        IntentRequestMonitor.launch(this)
        SetupMonitoring.launch(this)
        InnerPulseServer.launch(this)
        UbuntuInitProcess.launch(this)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        ProcessManager.finishProcess(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        ForegroundContinue.launch(this)
    }
}