package com.puutaro.commandclick.service

import android.app.NotificationChannel
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.broadcast.extra.GitDownloadExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeGitDownload
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.qr.OnGitKey
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.git_download.ByCloneDownloader
import com.puutaro.commandclick.service.lib.git_download.ByFannelListDownloader
import com.puutaro.commandclick.service.lib.git_download.GitDownloadBroadcastHandler
import com.puutaro.commandclick.service.lib.git_download.GitdownLoadServiceFinisher
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadLabels
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadStatus
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import kotlinx.coroutines.Job

class GitDownloadService: Service() {

    var gitDownloadJob: Job? = null
    var getListFileConJob: Job? = null
    var prefix: String = String()
    var fannelRawName: String = String()
    var currentAppDirPath: String = String()
    var fannelListPath: String? = null
    var parentRelativeDirPath: String? = null
    private val notificationIdToImportance =
        NotificationIdToImportance.HIGH
    val chanelId = ServiceChannelNum.gitDownload
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
            BroadCastIntentSchemeGitDownload.STOP_GIT_DOWNLOAD.action,
        )
    }

    var broadcastReceiverForGitDownload: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            GitDownloadBroadcastHandler.handle(
                this@GitDownloadService,
                intent
            )
        }
    }

    override fun onCreate() {
        BroadcastManagerForService.registerActionListBroadcastReceiver(
            this,
            broadcastReceiverForGitDownload,
            listOf(
                BroadCastIntentSchemeGitDownload.STOP_GIT_DOWNLOAD.action,
                BroadCastIntentSchemeGitDownload.STAN_GIT_DOWNLOAD.action
            )
        )
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(GitDownloadStatus.RUNNING.title)
            .setContentText(GitDownloadStatus.RUNNING.message)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .clearActions()
            .addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                GitDownloadLabels.CLOSE.label,
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
        gitDownloadJob?.cancel()
        val stanIntent = Intent()
        stanIntent.action = BroadCastIntentSchemeGitDownload.STAN_GIT_DOWNLOAD.action
        val stanSchema = BroadCastIntentSchemeGitDownload.STAN_GIT_DOWNLOAD.scheme
        prefix = intent?.getStringExtra(
            GitDownloadExtra.PREFIX.schema
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            stanIntent.putExtra(
                stanSchema,
                "${OnGitKey.PREFIX.key} must be specified"
            )
            applicationContext.sendBroadcast(stanIntent)
            return START_NOT_STICKY
        }
        fannelRawName = intent?.getStringExtra(
            GitDownloadExtra.FANNEL_RAW_NAME.schema
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            stanIntent.putExtra(
                stanSchema,
                "${OnGitKey.NAME.key} must be specified"
            )
            applicationContext.sendBroadcast(stanIntent)
            return START_NOT_STICKY
        }
        currentAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        intent?.getStringExtra(
//            GitDownloadExtra.CURRENT_APP_DIR_PATH_FOR_TRANSFER.schema
//        ).let {
//            if(
//                !it.isNullOrEmpty()
//            ) return@let it
//            stanIntent.putExtra(
//                stanSchema,
//                "currentAppDirPath must be specified"
//            )
//            applicationContext.sendBroadcast(stanIntent)
//            return START_NOT_STICKY
//        }
        fannelListPath = intent?.getStringExtra(
            GitDownloadExtra.FANNEL_LIST_PATH.schema
        )
        parentRelativeDirPath = intent?.getStringExtra(
            GitDownloadExtra.PARENT_DIR_PATH_FOR_FILE_UPLOAD.schema
        )
        startComamndNoti()

        val isFannelListPath = !fannelListPath.isNullOrEmpty()
        gitDownloadJob = when(isFannelListPath) {
            true -> ByFannelListDownloader.download(
                this,
            )
            else -> {
                ByCloneDownloader.download(
                    this,
                )
            }
        }
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        GitdownLoadServiceFinisher.finish(this)
    }

    private fun startComamndNoti(){
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(GitDownloadStatus.RUNNING.title)
            .setContentText(GitDownloadStatus.RUNNING.message)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .clearActions()
            .addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                GitDownloadLabels.CANCEL.label,
                cancelPendingIntent
            )
        notificationBuilder?.build()?.let {
            notificationManager.notify(
                chanelId,
                it
            )
        }
    }
}