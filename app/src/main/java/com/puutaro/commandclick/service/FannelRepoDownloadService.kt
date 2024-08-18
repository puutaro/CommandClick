package com.puutaro.commandclick.service

import android.R
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeFannelRepoDownload
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.ubuntu.WaitQuiz
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.UrlFileSystems
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File


class FannelRepoDownloadService: Service() {

    private val channelNum = ServiceChannelNum.fannelRepoDownload
    private val notificationIdToImportance =  NotificationIdToImportance.HIGH
    private var notificationManager: NotificationManagerCompat? = null
    private var fannelRepoDownloadJob: Job? = null
    private var execDownloadJob: Job? = null
    private val waitQuiz = WaitQuiz()
    private var broadcastReceiverForRepoDownloadStop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            exit(true)
        }
    }

    override fun onCreate() {
        BroadcastManagerForService.registerActionListBroadcastReceiver(
            this,
            broadcastReceiverForRepoDownloadStop,
            BroadCastIntentSchemeFannelRepoDownload.values().map {
                it.action
            }

        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        exit(false)

        val fanenlRepoDownloadStopIntent = Intent()
        fanenlRepoDownloadStopIntent.action = BroadCastIntentSchemeFannelRepoDownload.STOP_FANNEL_REPO_DOWNLOAD.action
        val context = applicationContext
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            fanenlRepoDownloadStopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            notificationIdToImportance.id,
            notificationIdToImportance.id,
            notificationIdToImportance.importance
        )

        notificationManager = NotificationManagerCompat.from(context)

        notificationManager?.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(context,notificationIdToImportance.id)
        notificationBuilder.setSmallIcon(R.drawable.stat_sys_download)
        notificationBuilder.setContentTitle(setNotiTitleMessage(1))
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setOnlyAlertOnce(true)
        notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
        notificationBuilder.setProgress(0, 0, true)
        notificationBuilder.addAction(
            R.drawable.ic_menu_close_clear_cancel,
            "cancel",
            pendingIntent
        )
        startForeground(channelNum, notificationBuilder.build())

        val cmdclickFannelAppsDirPath = UsePath.cmdclickFannelAppsDirPath
        val cmdclickFannelListDirPath = UsePath.cmdclickFannelListDirPath
        var cloneComp = false
        fannelRepoDownloadJob = CoroutineScope(Dispatchers.IO).launch {
            execDownloadJob = launch {
                withContext(Dispatchers.IO){
                    FileSystems.removeAndCreateDir(cmdclickFannelAppsDirPath)
                    FileSystems.removeAndCreateDir(cmdclickFannelListDirPath)
                }
                withContext(Dispatchers.IO){
                    updateNotiTitle(
                        notificationBuilder,
                        notificationManager,
                        pendingIntent,
                        setNotiTitleMessage(2)
                    )
                }
                withContext(Dispatchers.IO) {
                    fannelRepoDownload(
                        context
                    )
                }
                withContext(Dispatchers.IO){
                    updateNotiTitle(
                        notificationBuilder,
                        notificationManager,
                        pendingIntent,
                        setNotiTitleMessage(3)
                    )
                }
                withContext(Dispatchers.IO){
                    FileSystems.writeFile(
                        File(
                            cmdclickFannelListDirPath,
                            UsePath.fannelListMemoryName
                        ).absolutePath,
                        FannelListVariable.makeFannelListMemoryContents(
                            context
                        ).joinToString(
                            FannelListVariable.cmdclickFannelListSeparator
                        )
                    )
                }
                withContext(Dispatchers.IO) {
                    cloneComp = true
                }
            }
            withContext(Dispatchers.IO){
                var curSec = 0
                while(true){
                    if(
                        execDownloadJob?.isCancelled == true
                        || cloneComp
                    ) break
                    updateCloneProgress(
                        notificationBuilder,
                        notificationManager,
                        curSec
                    )
                    delay(3000)
                    curSec += 3
                }
            }
            withContext(Dispatchers.IO){
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
                )
            }
            withContext(Dispatchers.IO){
                exit(true)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        exit(true)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            unregisterReceiver(broadcastReceiverForRepoDownloadStop)
        } catch(e: Exception){
            println("pass")
        }
        exit(true)
    }

    private suspend fun fannelRepoDownload(
        context: Context
    ){
        val urlFileSystems = UrlFileSystems()
        val concurrentLimit = 10
        val semaphore = Semaphore(concurrentLimit)
        val fannelList = withContext(Dispatchers.IO) {
            urlFileSystems.execGetFannelList(context).split("\n")
        }
        withContext(Dispatchers.IO) {
            val jobList = fannelList.map {
                if(
                    execDownloadJob?.isCancelled == true
                ) return@map null
                async {
                    semaphore.withPermit {
                        val downloadUrl = "${urlFileSystems.gitUserContentFannelPrefix}/${it}"
                        val conByteArray = CurlManager.get(
                            context,
                            downloadUrl,
                            String(),
                            String(),
                            10000,
                        )
                        if(
                            !CurlManager.isConnOk(conByteArray)
                        ) return@async
                        val destiFileObj = File(UsePath.cmdclickFannelDirPath, it)
                        FileSystems.writeFromByteArray(
                            destiFileObj.absolutePath,
                            conByteArray
                        )
                    }
                }
            }
            jobList.forEach { it?.await() }
        }

    }

    private fun updateCloneProgress(
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat?,
        seconds: Int,
    ){
        val limitSeconds = 120
        val displayPercentage = (seconds * 100) / limitSeconds
        val notification = notificationBuilder.apply {
            setAutoCancel(true)
            setContentText(waitQuiz.echoQorA())
            setProgress(
                100,
                displayPercentage,
                false
            )
        }.build()
        notificationManager?.notify(channelNum, notification)
    }

    private fun updateNotiTitle(
        notificationBuilder: NotificationCompat.Builder?,
        notificationManager: NotificationManagerCompat?,
        pendingIntent: PendingIntent,
        title: String
    ){
        val notification = notificationBuilder?.apply {
            setSmallIcon(R.drawable.stat_sys_download)
            setAutoCancel(true)
            setContentTitle(title)
            clearActions()
            addAction(
                R.drawable.ic_menu_close_clear_cancel,
                "cancel",
                pendingIntent
            )
        }?.build()
        notification?.let {
            notificationManager?.notify(channelNum, it)
        }
    }

    private fun exit(isLast: Boolean){
        fannelRepoDownloadJob?.cancel()
        execDownloadJob?.cancel()
        notificationManager?.cancel(channelNum)
        if(isLast) {
            BroadcastManagerForService.unregisterBroadcastReceiver(
                this,
                broadcastReceiverForRepoDownloadStop
            )
            stopForeground(Service.STOP_FOREGROUND_DETACH)
            notificationManager?.cancel(channelNum)
            stopSelf()
        }
    }

    private fun setNotiTitleMessage(
        order: Int
    ): String{
        val titleMsgList = listOf(
            "Ready...",
            "Cloning...",
            "Update fannel list..."
        )
        val lenMsgList = titleMsgList.size
        val indexSrc = order - 1
        val index = try {
            titleMsgList[indexSrc]
            indexSrc
        } catch (e: Exception){
            titleMsgList.lastIndex
        }
        val title = titleMsgList[index]
        return "[${index + 1}/${lenMsgList}] ${title}"
    }
}



