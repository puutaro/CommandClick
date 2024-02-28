package com.puutaro.commandclick.service.lib.git_download

import android.R
import com.github.syari.kgit.KGit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.GitDownloadService
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadLabels
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadStatus
import com.puutaro.commandclick.service.lib.git_download.libs.NotiLauncher
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.jgit.lib.ProgressMonitor
import java.io.File

object ByCloneDownloader {

    var gitCloneWait = true
    private val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath

    fun download(
        gitDownloadService: GitDownloadService
    ): Job {
        gitCloneWait = true
        return CoroutineScope(Dispatchers.IO).launch {
            gitDownloadService.getListFileConJob = CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    execGitClone(
                        gitDownloadService
                    )
                }
                withContext(Dispatchers.IO){
                    gitCloneWait = false
                }
            }

            withContext(Dispatchers.IO) {
                gitCloneWaitNoti(gitDownloadService)
            }
            if (
                FileSystems.sortedFiles(cmdclickTempDownloadDirPath).size <= 0
            ) {
                withContext(Dispatchers.IO) {
                    gitCloneFailureNoti(gitDownloadService)
                }
                return@launch
            }
            val isSuccess = withContext(Dispatchers.IO) {
                execSaveFile(
                    gitDownloadService,
                )
            }
            if(!isSuccess) return@launch
            withContext(Dispatchers.IO) {
                NotiLauncher.compCloseNoti(gitDownloadService)
            }
        }
    }

    private fun execGitClone(
        gitDownloadService: GitDownloadService
    ){
        val context = gitDownloadService.applicationContext
        FileSystems.removeAndCreateDir(cmdclickTempDownloadDirPath)
        val prefix = gitDownloadService.prefix
        val cmdclickTempDownloadDirPathObj = File(cmdclickTempDownloadDirPath)
        try {
            KGit.cloneRepository {
                setURI(prefix)
                setDirectory(cmdclickTempDownloadDirPathObj)
                setProgressMonitor(object : ProgressMonitor {
                    override fun start(totalTasks: Int) {}
                    override fun beginTask(title: String?, totalWork: Int) {}
                    override fun update(completed: Int) {}
                    override fun endTask() {}

                    override fun isCancelled(): Boolean {
                        return false
                    }
                })
            }
        } catch(e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
        }
    }

    private fun execSaveFile(
        gitDownloadService: GitDownloadService
    ): Boolean {
        val currentAppDirPath = gitDownloadService.currentAppDirPath
        val fannelRawName = gitDownloadService.fannelRawName
        val parentRelativeDirPath = gitDownloadService.parentRelativeDirPath
        val fannelParentDirPath =
            when(parentRelativeDirPath.isNullOrEmpty()) {
                true -> cmdclickTempDownloadDirPath
                else -> "$cmdclickTempDownloadDirPath/$parentRelativeDirPath"
            }
        val fannelDirName = CcPathTool.makeFannelDirName(fannelRawName)
        val downloadFannelDirPath = "$fannelParentDirPath/$fannelDirName"
        val jsFannelPath = "$fannelParentDirPath/${fannelRawName}.js"
        val shellFannelPath = "$fannelParentDirPath/${fannelRawName}.sh"
        val downloadFannelPath = if(
            File(jsFannelPath).isFile
        ) jsFannelPath
        else shellFannelPath
        val downloadFannelName = File(downloadFannelPath).name
        val destiFannelDirPath = "$currentAppDirPath/$fannelDirName"
        val destiFannelPath = "$currentAppDirPath/${downloadFannelName}"
        if(!File(downloadFannelPath).isFile){
            NotiLauncher.noExistFileNoti(
                gitDownloadService,
                downloadFannelName,
            )
            return false
        }
        copyStartNoti(
            gitDownloadService,
            fannelDirName,
        )
        FileSystems.copyFile(
            downloadFannelPath,
            destiFannelPath
        )
        copyStartNoti(
            gitDownloadService,
            downloadFannelName,
        )
        FileSystems.copyDirectory(
            downloadFannelDirPath,
            destiFannelDirPath
        )
        FileSystems.removeDir(
            cmdclickTempDownloadDirPath
        )
        return true
    }


    private suspend fun gitCloneWaitNoti(
        gitDownloadService: GitDownloadService,
    ){
        val waitLimitTimes = 600
        for (j in 0..waitLimitTimes) {
            delay(100)
            if (!gitCloneWait) break
            if (j % 30 != 0) continue
            gitDownloadService.notificationBuilder
                ?.setContentTitle(
                    GitDownloadStatus.GIT_CLONE.title
                )
                ?.setContentText(
                    GitDownloadStatus.GIT_CLONE.message.format("${j / 10}")
                )?.build()?.let {
                    gitDownloadService.notificationManager.notify(
                        gitDownloadService.chanelId,
                        it
                    )
                }
        }
    }

    private fun gitCloneFailureNoti(
        gitDownloadService: GitDownloadService
    ){
        val prefix = gitDownloadService.prefix
        gitDownloadService.notificationBuilder
            ?.setSmallIcon(R.drawable.stat_sys_download_done)
            ?.setContentTitle(
                GitDownloadStatus.FAILURE_GIT_CLONE.title,
            )
            ?.setContentText(
                GitDownloadStatus.FAILURE_GIT_CLONE.message.format(prefix),
            )
        gitDownloadService.notificationBuilder?.clearActions()
        gitDownloadService.notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.icons8_cancel,
            GitDownloadLabels.CLOSE.label,
            gitDownloadService.cancelPendingIntent
        )?.build()?.let {
            gitDownloadService.notificationManager.notify(
                gitDownloadService.chanelId,
                it
            )
        }
    }

    private fun copyStartNoti(
        gitDownloadService: GitDownloadService,
        copyTargetName: String,
    ){
        gitDownloadService.notificationBuilder
            ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
            ?.setContentTitle(
                GitDownloadStatus.COPY.title
            )
            ?.setContentText(
                GitDownloadStatus.COPY.message.format(
                    copyTargetName
                )
            )?.build()?.let {
            gitDownloadService.notificationManager.notify(
                gitDownloadService.chanelId,
                it
            )
        }
    }
}