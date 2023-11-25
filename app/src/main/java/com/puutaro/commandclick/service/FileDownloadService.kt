package com.puutaro.commandclick.service

import android.app.NotificationChannel
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.intent.extra.FileDownloadExtra
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeFileDownload
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.file_download.FileDownloader
import com.puutaro.commandclick.service.lib.file_download.FileDownloadBroadcastHandler
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownLoadStatus
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadLabels
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import kotlinx.coroutines.Job

class FileDownloadService: Service() {

    var fileDownloadJob: Job? = null
    var getListFileConJob: Job? = null
    private val notificationIdToImportance =
        NotificationIdToImportance.HIGH
    val chanelId = ServiceChannelNum.fileDownload
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
            BroadCastIntentSchemeFileDownload.STOP_FILE_DOWNLOAD.action,
        )
    }

    private var broadcastReceiverForFileDownlaod: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            FileDownloadBroadcastHandler.handle(
                this@FileDownloadService,
                intent
            )
        }
    }

    override fun onCreate() {
        BroadcastManagerForService.registerActionListBroadcastReceiver(
            this,
            broadcastReceiverForFileDownlaod,
            listOf(
                BroadCastIntentSchemeFileDownload.STOP_FILE_DOWNLOAD.action,
                BroadCastIntentSchemeFileDownload.STAN_FILE_DOWNLOAD.action
            )
        )
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(FileDownLoadStatus.RUNNING.title)
            .setContentText(FileDownLoadStatus.RUNNING.message)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .clearActions()
            .addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileDownloadLabels.CLOSE.label,
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
        fileDownloadJob?.cancel()
        val stanIntent = Intent()
        stanIntent.action = BroadCastIntentSchemeFileDownload.STAN_FILE_DOWNLOAD.action
        val mainUrl = intent?.getStringExtra(
            FileDownloadExtra.MAIN_URL.schema
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            applicationContext.sendBroadcast(stanIntent)
            return START_NOT_STICKY
        }
        val fullPathPrFannelRawName = intent?.getStringExtra(
            FileDownloadExtra.FULL_PATH_OR_FANNEL_RAW_NAME.schema
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            applicationContext.sendBroadcast(stanIntent)
            return START_NOT_STICKY
        }
        val currentAppDirPath = intent?.getStringExtra(
            FileDownloadExtra.CURRENT_APP_DIR_PATH_FOR_TRANSFER.schema
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            applicationContext.sendBroadcast(stanIntent)
            return START_NOT_STICKY
        }

        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(FileDownLoadStatus.RUNNING.title)
            .setContentText(FileDownLoadStatus.RUNNING.message)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .clearActions()
            .addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileDownloadLabels.CANCEL.label,
                cancelPendingIntent
            )
        notificationBuilder?.build()?.let {
            notificationManager.notify(
                chanelId,
                it
            )
        }

        fileDownloadJob = FileDownloader.save(
            this,
            currentAppDirPath,
            mainUrl,
            fullPathPrFannelRawName
        )
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}