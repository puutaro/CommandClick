package com.puutaro.commandclick.service.lib.git_download

import com.puutaro.commandclick.service.GitDownloadService
import com.puutaro.commandclick.service.lib.file_upload.ReceivePathMacroType
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadLabels
import com.puutaro.commandclick.service.lib.git_download.libs.GitDownloadStatus
import com.puutaro.commandclick.service.lib.git_download.libs.NotiLauncher
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ByFannelListDownloader {

    private val curlTimeoutMiliSec = 5000
    private val retryDelayMiliSec = 300L
    private val invalidResponse = CurlManager.invalidResponse
    var isWaitForFileListCon = true

    fun download(
        gitDownloadService: GitDownloadService,
    ): Job {
        val context = gitDownloadService.applicationContext
        val prefix = gitDownloadService.prefix
        val fannelListPath = gitDownloadService.fannelListPath

        isWaitForFileListCon = true
        var fileListCon = String()
        val fannelListUrl = "$prefix/$fannelListPath"
        return CoroutineScope(Dispatchers.IO).launch {
            gitDownloadService.getListFileConJob = CoroutineScope(Dispatchers.IO).launch {
                var fileListConSrc = String()
                withContext(Dispatchers.IO) {
                    for (i in 1..3) {
                        val fileListConSrcByteArray = CurlManager.get(
                            context,
                            fannelListUrl,
                            String(),
                            String(),
                            curlTimeoutMiliSec
                        )
                        if (
                            CurlManager.isConnOk(fileListConSrcByteArray)
                        ) {
                            fileListConSrc = String(fileListConSrcByteArray)
                            break
                        }
                        delay(retryDelayMiliSec)
                    }
                }
                withContext(Dispatchers.IO) {
                    fileListCon = fileListConSrc
                    isWaitForFileListCon = false
                }
            }

            withContext(Dispatchers.IO){
                getFileListWaitNoti(
                    gitDownloadService,
                )
            }
            if(
                !CurlManager.isConnOk(
                    fileListCon.toByteArray()
                )
                || fileListCon.isEmpty()
            ) {
                withContext(Dispatchers.IO){
                    failureFileListNoti(
                        gitDownloadService,
                        fannelListUrl
                    )
                }
                return@launch
            }
            withContext(Dispatchers.IO) {
                execSaveFile(
                    gitDownloadService,
                    fileListCon,
                )
            }
        }
    }

    private suspend fun execSaveFile(
        gitDownloadService: GitDownloadService,
        fileListCon: String,
    ){
        val context = gitDownloadService.applicationContext
        val cpFileList = makeCpFileListCon(
            gitDownloadService,
            fileListCon,
        )
        val prefix = gitDownloadService.prefix
        val fannelRawName = gitDownloadService.fannelRawName
        if(
            cpFileList.isEmpty()
        ){
            NotiLauncher.noExistFileNoti(
                gitDownloadService,
                fannelRawName
            )
            return
        }
        val currentAppDirPath = gitDownloadService.currentAppDirPath
        val cpFileListIndexSize = cpFileList.size - 1
        (cpFileList.indices).forEach {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    delay(100)
                }
                val fileRelativePath = withContext(Dispatchers.IO) {
                    cpFileList[it]
                }

                val destiFileObj = withContext(Dispatchers.IO) {
                    val cpDestiFilePath = "$currentAppDirPath/${fileRelativePath}"
                    File(cpDestiFilePath)
                }
                val destiFileName = withContext(Dispatchers.IO) {
                    destiFileObj.name
                }
                val getUrl = withContext(Dispatchers.IO) {
                    val parentRelativeDirPath =
                        gitDownloadService.parentRelativeDirPath
                    if (
                        parentRelativeDirPath.isNullOrEmpty()
                    ) "$prefix/$it"
                    else "$prefix/$parentRelativeDirPath/$fileRelativePath"
                }
                val con = withContext(Dispatchers.IO) {
                    var conSrc = byteArrayOf()
                    for (i in 1..3) {
                        conSrc = CurlManager.get(
                            context,
                            getUrl,
                            String(),
                            String(),
                            curlTimeoutMiliSec,
                        )
                        if (
                            conSrc.isNotEmpty()
                        ) break
                        delay(retryDelayMiliSec)
                    }
                    conSrc
                }
                if(
                    String(con).trim() == invalidResponse
                ){
                    gitDownloadService.notificationBuilder
                        ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
                        ?.setContentTitle(
                            GitDownloadStatus.CONNECTION_ERR.title
                        )
                        ?.setContentText(
                            GitDownloadStatus.CONNECTION_ERR.message.format(
                                prefix
                            )
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
                    gitDownloadService.gitDownloadJob?.cancel()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    FileSystems.writeFromByteArray(
                        destiFileObj.absolutePath,
                        con
                    )
                }
                withContext(Dispatchers.Main){
                    if(it % 3 != 0) return@withContext
                    gitDownloadService.notificationBuilder
                        ?.setContentText(
                            "[$it / $cpFileListIndexSize] get ok ${destiFileName}",
                        )?.build()?.let {
                            gitDownloadService.notificationManager.notify(
                                gitDownloadService.chanelId,
                                it
                            )
                        }
                }
                if(it < cpFileListIndexSize) return@launch
                withContext(Dispatchers.IO){
                    CurlManager.post(
                        context,
                        prefix,
                        "Content-type\ttext/plain",
                        ReceivePathMacroType.CLOSE_COPY_SERVER.name,
                        curlTimeoutMiliSec,
                    )
                }
                withContext(Dispatchers.IO){
                    NotiLauncher.compCloseNoti(
                        gitDownloadService
                    )
                }
            }
        }
    }

    private fun makeCpFileListCon(
        gitDownloadService: GitDownloadService,
        fileListCon: String,
    ): List<String> {
        val fannelRawName = gitDownloadService.fannelRawName
        return fileListCon.split("\n").filter {
            it.startsWith(
                fannelRawName
            )
        }
    }

    private suspend fun getFileListWaitNoti(
        gitDownloadService: GitDownloadService,
    ){

        val waitLimitTimes = 600
        for(j in 0..waitLimitTimes){
            delay(100)
            if(!isWaitForFileListCon) break
            if(j % 30 != 0) continue
            gitDownloadService.notificationBuilder
                ?.setContentText(
                    GitDownloadStatus.GET_FILE_LIST.message.format("${j / 10}")
                )?.build()?.let {
                    gitDownloadService.notificationManager.notify(
                        gitDownloadService.chanelId,
                        it
                    )
                }
        }
    }

    private fun failureFileListNoti(
        gitDownloadService: GitDownloadService,
        fannelListUrl: String
    ){
        gitDownloadService.notificationBuilder
            ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
            ?.setContentTitle(
                GitDownloadStatus.FAILURE_FILE_LIST.title,
            )
            ?.setContentText(
                GitDownloadStatus.FAILURE_FILE_LIST.message.format(fannelListUrl),
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
}