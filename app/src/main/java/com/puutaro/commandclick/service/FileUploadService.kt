package com.puutaro.commandclick.service

import android.app.NotificationChannel
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.intent.extra.FileUploadExtra
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeFileUpload
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.file_upload.CopyFannelServer
import com.puutaro.commandclick.service.lib.file_upload.FileUploadBroadcastHandler
import com.puutaro.commandclick.service.lib.file_upload.FileUploadFinisher
import com.puutaro.commandclick.service.lib.file_upload.UploadAcceptTime
import com.puutaro.commandclick.service.lib.file_upload.libs.FileUploadLabels
import com.puutaro.commandclick.service.lib.file_upload.libs.FileUploadStatus
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.ServerSocket
import java.time.LocalDateTime

class FileUploadService: Service() {

    var fileUploadJob: Job? = null
    var monitorAcceptTimeJob: Job? = null
    var currentAppDirPath = String()
    var copyFannelSocket: ServerSocket? = null
    private val notificationIdToImportance =
        NotificationIdToImportance.LOW
    val chanelId = ServiceChannelNum.fileUpload
    val cmdclickTempFileUploadServiceDirPath = UsePath.cmdclickTempFileUploadServiceDirPath
    val uploadServiceAcceptTimeTxtName = UsePath.uploadServiceAcceptTimeTxtName
    val notificationManager by lazy {
        val channel = NotificationChannel(
            notificationIdToImportance.id,
            notificationIdToImportance.id,
            notificationIdToImportance.importance
        )
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(channel)
        notificationManager
    }
    var notificationBuilder:  NotificationCompat.Builder? = null

    val cancelPendingIntent by lazy {
        PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentSchemeFileUpload.STOP_FILE_UPLOAD.action
        )
    }

    val broadcastReceiverForFileUplaod: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            FileUploadBroadcastHandler.handle(
                this@FileUploadService,
                intent
            )
        }
    }

    override fun onCreate() {
        BroadcastManagerForService.registerActionListBroadcastReceiver(
            this,
            broadcastReceiverForFileUplaod,
            listOf(
                BroadCastIntentSchemeFileUpload.STOP_FILE_UPLOAD.action,
                BroadCastIntentSchemeFileUpload.STAN_FILE_UPLOAD.action
            )
        )
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(FileUploadStatus.RUNNING.title)
            .setContentText(FileUploadStatus.RUNNING.message)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .clearActions()
            .addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileUploadLabels.CANCEL.label,
                cancelPendingIntent
            )
        notificationBuilder?.build()?.let {
            notificationManager.notify(
                chanelId,
                it
            )
            startForeground(
                chanelId,
                it
            )
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        fileUploadJob?.cancel()


        currentAppDirPath = intent?.getStringExtra(
            FileUploadExtra.CURRENT_APP_DIR_PATH_FOR_FILE_UPLOAD.schema
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            val stanIntent = Intent()
            stanIntent.action = BroadCastIntentSchemeFileUpload.STAN_FILE_UPLOAD.action
            applicationContext.sendBroadcast(stanIntent)
            return START_NOT_STICKY
        }
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(FileUploadStatus.RUNNING.title)
            .setContentText(FileUploadStatus.RUNNING.message)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .clearActions()
            .addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileUploadLabels.CANCEL.label,
                cancelPendingIntent
            )
        notificationBuilder?.build()?.let {
            notificationManager.notify(
                chanelId,
                it
            )
            startForeground(
                chanelId,
                it
            )
        }

        fileUploadJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                FileSystems.writeFile(
                    File(
                        cmdclickTempFileUploadServiceDirPath,
                        uploadServiceAcceptTimeTxtName
                    ).absolutePath,
                    LocalDateTime.now().toString()
                )
            }
            withContext(Dispatchers.IO) {
                CopyFannelServer.launch(
                    this@FileUploadService
                )
            }
        }

        monitorAcceptTimeJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(1000)
            }
            withContext(Dispatchers.IO) {
                UploadAcceptTime.monitor(this@FileUploadService)
            }
        }
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUploadFinisher.exit(this)
    }
}