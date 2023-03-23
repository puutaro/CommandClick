package com.puutaro.commandclick.service

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.syari.kgit.KGit
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.NotificationChanel
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.*
import org.eclipse.jgit.lib.ProgressMonitor
import java.io.File


class GitCloneService: Service() {

    private val cmdclickFannelListSeparator = FannelListVariable.cmdclickFannelListSeparator
    private val descriptionFirstLineLimit = FannelListVariable.descriptionFirstLineLimit
    private var notificationManager: NotificationManagerCompat? = null
    private var gitCloneJob: Job? = null
    private var notificationManagefilter: IntentFilter? = null
    private var isProgressCancel = true
    private val cloneDisplayStr = "Cloning..."
    private var broadcastReceiverForGitCloneStop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            gitCloneJob?.cancel()
            isProgressCancel = true
            notificationManager?.cancelAll()
        }
    }

    override fun onCreate() {
        notificationManagefilter = IntentFilter()
        notificationManagefilter?.addAction(BroadCastIntentScheme.STOP_GIT_CLONE.action)
        try {
            registerReceiver(broadcastReceiverForGitCloneStop, notificationManagefilter)
        } catch(e: Exception){
            println("pass")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isProgressCancel = true
        notificationManager?.cancelAll()
        gitCloneJob?.cancel()

        val gitCloneStopIntent = Intent()
        gitCloneStopIntent.action = BroadCastIntentScheme.STOP_GIT_CLONE.action

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, gitCloneStopIntent, PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            NotificationChanel.GIT_CLONE_NOTIFICATION.id,
            NotificationChanel.GIT_CLONE_NOTIFICATION.name,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setSound(null, null);
        val context = applicationContext

        notificationManager = NotificationManagerCompat.from(context)

        notificationManager?.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(context, NotificationChanel.GIT_CLONE_NOTIFICATION.id)
        notificationBuilder.setSmallIcon(R.drawable.stat_sys_download)
        notificationBuilder.setContentTitle("Cloning...")
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
        notificationBuilder.setProgress(0, 0, true)
        notificationBuilder.addAction(
            R.drawable.ic_menu_close_clear_cancel,
            "cancel",
            pendingIntent
        )

        val cmdclickFannelAppsDirPath = UsePath.cmdclickFannelAppsDirPath
        val repoFileObj = File(cmdclickFannelAppsDirPath)
        FileSystems.removeDir(cmdclickFannelAppsDirPath)
        FileSystems.createDirs(cmdclickFannelAppsDirPath)
        gitCloneJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                isProgressCancel = false
                kGitClone(
                    repoFileObj,
                    notificationBuilder,
                    notificationManager
                )
            }
            withContext(Dispatchers.IO){
                FileSystems.writeFile(
                    UsePath.cmdclickFannelListDirPath,
                    UsePath.fannelListMemoryName,
                    makeFannelListMemoryContents().joinToString(cmdclickFannelListSeparator)
                )
                isProgressCancel = true
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
        notificationManager?.cancelAll()
        gitCloneJob?.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            unregisterReceiver(broadcastReceiverForGitCloneStop)
        } catch(e: Exception){
            println("pass")
        }
        notificationManager?.cancelAll()
        gitCloneJob?.cancel()
        stopSelf()
    }

    private fun makeFannelListMemoryContents(): List<String> {
        val cmdclickFannelItselfDirPath = UsePath.cmdclickFannelItselfDirPath
        if(
            !File(cmdclickFannelItselfDirPath).isDirectory
        ) return emptyList()
        val fannelsListSource = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            cmdclickFannelItselfDirPath,
        )
        return fannelsListSource.map {
            val descFirstLineSource = ScriptFileDescription.makeDescriptionContents(
                ReadText(
                    cmdclickFannelItselfDirPath,
                    it
                ).textToList(),
                it
            ).split('\n').firstOrNull()
            val descFirstLine = if(
                !descFirstLineSource.isNullOrEmpty()
                && descFirstLineSource.length > descriptionFirstLineLimit
            ) descFirstLineSource.substring(0, descriptionFirstLineLimit)
            else descFirstLineSource
            return@map if(descFirstLine.isNullOrEmpty()) it
            else {
                "$it\n\t\t- $descFirstLine"
            }
        }
    }


    private fun kGitClone(
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
            Log.e(LoggerTag.fannnelListUpdateErr, "close git")
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
                var lastWorked = 0
                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String?, totalWork: Int) {
                    if(isProgressCancel) return
                    notificationBuilder.setContentTitle(cloneDisplayStr)
                    notificationBuilder.setAutoCancel(true)
                    notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
                    notificationBuilder.setProgress(100, 0, true)
                    notificationManager.notify(0, notificationBuilder.build())
                    lastWorked = 0
                }

                override fun update(completed: Int) {
                    if(isProgressCancel) return
                    val percentComplete = (completed.toFloat() / lastWorked * 100).toInt()
                    notificationBuilder.setContentTitle("$cloneDisplayStr $percentComplete%")
                    notificationBuilder.setContentText("$cloneDisplayStr $percentComplete%")
                    notificationBuilder.setAutoCancel(true)
                    notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
                    notificationBuilder.setProgress(100, percentComplete, false)
                    notificationManager.notify(0, notificationBuilder.build())
                    lastWorked = completed
                }

                override fun endTask() {
                    if(isProgressCancel) return
                    notificationBuilder.setContentTitle("Cloned 100%")
                    notificationBuilder.setContentText("cloned 100%")
                    notificationBuilder.setSmallIcon(R.drawable.stat_sys_download_done)
                    notificationBuilder.setContentText(WebUrlVariables.commandClickRepositoryUrl)
                    notificationBuilder.setProgress(100, 100, false)
                    notificationBuilder.setAutoCancel(true)
                    notificationBuilder.clearActions()
                    notificationManager.notify(0, notificationBuilder.build())
                    isProgressCancel = true
                }
                override fun isCancelled(): Boolean {
                    return false
                }
            })
        }
    }
}
