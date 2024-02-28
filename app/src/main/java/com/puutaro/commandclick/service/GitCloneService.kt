package com.puutaro.commandclick.service

import android.R
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.syari.kgit.KGit
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeGitClone
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.*
import org.eclipse.jgit.lib.ProgressMonitor
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.ln


class GitCloneService: Service() {

    private val channelNum = ServiceChannelNum.gitClone
    private val notificationIdToImportance =  NotificationIdToImportance.HIGH
    private val cmdclickFannelListSeparator = FannelListVariable.cmdclickFannelListSeparator
    private var notificationManager: NotificationManagerCompat? = null
    private var gitCloneJob: Job? = null
    private var notificationManagefilter: IntentFilter? = null
    private var isProgressCancel = true
    private val cloneDisplayStr = "Cloning..."
    private var broadcastReceiverForGitCloneStop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            gitCloneJob?.cancel()
            isProgressCancel = true
            notificationManager?.cancel(channelNum)
            stopForeground(Service.STOP_FOREGROUND_DETACH)
        }
    }

    override fun onCreate() {
        notificationManagefilter = IntentFilter()
        notificationManagefilter?.addAction(BroadCastIntentSchemeGitClone.STOP_GIT_CLONE.action)
        try {
            registerReceiver(broadcastReceiverForGitCloneStop, notificationManagefilter)
        } catch(e: Exception){
            println("pass")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isProgressCancel = true
        notificationManager?.cancel(channelNum)
        gitCloneJob?.cancel()

        val gitCloneStopIntent = Intent()
        gitCloneStopIntent.action = BroadCastIntentSchemeGitClone.STOP_GIT_CLONE.action

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, gitCloneStopIntent, PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            notificationIdToImportance.id,
            notificationIdToImportance.id,
            notificationIdToImportance.importance
        )
        val context = applicationContext

        notificationManager = NotificationManagerCompat.from(context)

        notificationManager?.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(context,notificationIdToImportance.id)
        notificationBuilder.setSmallIcon(R.drawable.stat_sys_download)
        notificationBuilder.setContentTitle("Cloning...")
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
        val repoFileObj = File(cmdclickFannelAppsDirPath)
        FileSystems.removeDir(cmdclickFannelAppsDirPath)
        FileSystems.createDirs(cmdclickFannelAppsDirPath)
        gitCloneJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                isProgressCancel = false
                kGitClone(
                    context,
                    repoFileObj,
                    notificationBuilder,
                    notificationManager
                )
            }
            withContext(Dispatchers.IO){
                notificationBuilder.setContentTitle("Update fannel list..")
                notificationBuilder.setContentText("Update fannel list..")
                notificationBuilder.setSmallIcon(R.drawable.stat_sys_download)
                notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
                notificationBuilder.setProgress(100, 100, false)
                notificationBuilder.setAutoCancel(true)
                notificationBuilder.clearActions()
                val notification = notificationBuilder.build()
                notificationManager?.notify(channelNum, notification)
            }
            withContext(Dispatchers.IO){
                FileSystems.writeFile(
                    File(
                        UsePath.cmdclickFannelListDirPath,
                        UsePath.fannelListMemoryName
                    ).absolutePath,
                    FannelListVariable.makeFannelListMemoryContents(applicationContext)
                        .joinToString(cmdclickFannelListSeparator)
                )
                isProgressCancel = true
            }
            withContext(Dispatchers.IO){
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
                )
            }
            withContext(Dispatchers.IO){
                notificationManager?.cancel(channelNum)
                stopForeground(Service.STOP_FOREGROUND_DETACH)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiverForGitCloneStop)
        } catch(e: Exception){
            println("pass")
        }
        notificationManager?.cancel(channelNum)
        gitCloneJob?.cancel()
        stopForeground(Service.STOP_FOREGROUND_DETACH)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            unregisterReceiver(broadcastReceiverForGitCloneStop)
        } catch(e: Exception){
            println("pass")
        }
        notificationManager?.cancel(channelNum)
        gitCloneJob?.cancel()
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun kGitClone(
        context: Context?,
        repoFileObj: File,
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat?,
    ){
        if(
            notificationManager == null
        ) return
        var git: KGit? = null
        try {
            git = execKGitClone(
                repoFileObj,
                notificationBuilder,
                notificationManager,
            )
        } catch (e: Exception) {
            LogSystems.stdErr(
                context,
                "close git"
            )
            return
        } finally {
            git?.close()
        }
    }


    private fun execKGitClone(
        repoFileObj: File,
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat,
    ): KGit {
        return KGit.cloneRepository {
            setURI(WebUrlVariables.commandClickRepositoryUrl)
            setDirectory(repoFileObj)
            setProgressMonitor(object : ProgressMonitor {
                var initWorkedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

                var displayTimesForUpdate = 0
                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String?, totalWork: Int) {
                    if(isProgressCancel) return
                    notificationBuilder.setContentTitle(cloneDisplayStr)
                    notificationBuilder.setAutoCancel(true)
                    notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
                    notificationBuilder.setProgress(100, 0, true)
                    val notification = notificationBuilder.build()
                    notificationManager.notify(channelNum, notification)
                }

                override fun update(completed: Int) {
                    if(isProgressCancel) return
                    if(
                        displayTimesForUpdate % 10000 != 0
                    ) return
                    val secondsDiff =
                        TimeUnit.MILLISECONDS.toSeconds(
                            System.currentTimeMillis()
                        ) - initWorkedTime
                    val displayPercentComplete = log105(secondsDiff.toInt()).toInt()
                    notificationBuilder
                        .setContentTitle(
                            "$cloneDisplayStr $displayPercentComplete% / 120s"
                        )
                    notificationBuilder
                        .setContentText(
                            "$cloneDisplayStr $displayPercentComplete% / 120s"
                        )
                    notificationBuilder.setAutoCancel(true)
                    notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
                    notificationBuilder.setProgress(
                        100,
                        displayPercentComplete,
                        false
                    )
                    val notification = notificationBuilder.build()
                    notificationManager.notify(channelNum, notification)
                }

                override fun endTask() {
                    if(isProgressCancel) return
                    isProgressCancel = true
                }
                override fun isCancelled(): Boolean {
                    return false
                }
            })
        }
    }
}

// my intend to log2 120 = 100%?
private fun log105(n: Int): Double {
    return ln(n.toDouble()) / ln(1.049)
}


