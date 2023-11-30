package com.puutaro.commandclick.service.lib.file_download

import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.service.lib.file_upload.ReceivePathMacroType
import com.puutaro.commandclick.service.FileDownloadService
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadStatus
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadLabels
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object FileDownloader {

    private val curlTimeoutMiliSec = 5000
    private val retryDelayMiliSec = 300L
    private val invalidResponse = CurlManager.invalidResponse

    fun save(
        fileDownloadService: FileDownloadService,
        currentAppDirPath: String,
        mainUrl: String,
        getPathOrFannelRawName: String,
    ): Job {
        val macroSeparator = QrSeparator.sepalator.str
        val parentDirPath = File(getPathOrFannelRawName).parent
            ?.removeSuffix("/")
            ?: String()
        val getFileListMacro = ReceivePathMacroType.GET_FILE_LIST.name
        val getFilePathAndArg = if(
            getPathOrFannelRawName.startsWith("/")
        ) "$getFileListMacro $macroSeparator $parentDirPath"
        else getFileListMacro

        var isWaitForFileListCon = true
        var fileListCon = String()
        return CoroutineScope(Dispatchers.IO).launch {

            fileDownloadService.getListFileConJob = CoroutineScope(Dispatchers.IO).launch {
                var fileListConSrc = String()
                withContext(Dispatchers.IO) {
                    for (i in 1..3) {
                        val fileListConSrcByteArray = CurlManager.post(
                            mainUrl,
                            String(),
                            getFilePathAndArg,
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

            val waitLimitTimes = 600
            withContext(Dispatchers.IO){
                for(j in 0..waitLimitTimes){
                    delay(100)
                    if(!isWaitForFileListCon) break
                    if(j % 30 != 0) continue
                    fileDownloadService.notificationBuilder
                        ?.setContentText(
                            FileDownloadStatus.GET_FILE_LIST.message.format("${j / 10}")
                        )?.build()?.let {
                            fileDownloadService.notificationManager.notify(
                                fileDownloadService.chanelId,
                                it
                            )
                        }
                }
            }
            if(
                !CurlManager.isConnOk(
                    fileListCon.toByteArray()
                )
                || fileListCon.isEmpty()
            ) {
                withContext(Dispatchers.IO){
                    fileDownloadService.notificationBuilder
                        ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
                        ?.setContentTitle(
                            FileDownloadStatus.FAILURE_FILE_LIST.title,
                        )
                        ?.setContentText(
                            FileDownloadStatus.FAILURE_FILE_LIST.message.format(getPathOrFannelRawName),
                        )
                    fileDownloadService.notificationBuilder?.clearActions()
                    fileDownloadService.notificationBuilder?.addAction(
                        com.puutaro.commandclick.R.drawable.icons8_cancel,
                        FileDownloadLabels.CLOSE.label,
                        fileDownloadService.cancelPendingIntent
                    )?.build()?.let {
                        fileDownloadService.notificationManager.notify(
                            fileDownloadService.chanelId,
                            it
                        )
                    }
                }
                return@launch
            }
            withContext(Dispatchers.IO) {
                execSaveFile(
                    fileDownloadService,
                    mainUrl,
                    fileListCon,
                    currentAppDirPath,
                    getPathOrFannelRawName
                )
            }
        }
    }

    private suspend fun execSaveFile(
        fileDownloadService: FileDownloadService,
        mainUrl: String,
        fileListCon: String,
        currentAppDirPath: String,
        getPathOrFannelRawName: String = String(),
    ){
        val cpFileList = makeCpFileListCon(
            getPathOrFannelRawName,
            fileListCon,
            currentAppDirPath,
        )
        if(
            cpFileList.isEmpty()
        ){
            fileDownloadService.notificationBuilder
                ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
                ?.setContentTitle(
                    FileDownloadStatus.FAILURE_GREP_FILE_LIST.title,
                )
                ?.setContentText(
                    FileDownloadStatus.FAILURE_GREP_FILE_LIST.message.format(getPathOrFannelRawName),
                )
            fileDownloadService.notificationBuilder?.clearActions()
            fileDownloadService.notificationBuilder?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                FileDownloadLabels.CLOSE.label,
                fileDownloadService.cancelPendingIntent
            )?.build()?.let {
                fileDownloadService.notificationManager.notify(
                    fileDownloadService.chanelId,
                    it
                )
            }
            return
        }
        val cpFileListIndexSize = cpFileList.size - 1
        (cpFileList.indices).forEach {
            delay(100)
            val cpFilePath = cpFileList[it]

            val destiFileObj = withContext(Dispatchers.IO) {
                File(cpFilePath)
            }
                val destiFileParentDirPath =  withContext(Dispatchers.IO) {
                    destiFileObj.parent
                        ?: String()
                }
                val destiFileName = withContext(Dispatchers.IO) {
                    destiFileObj.name
                }
                val con = withContext(Dispatchers.IO) {
                    var conSrc = byteArrayOf()
                    for (i in 1..3) {
                        conSrc = CurlManager.post(
                            mainUrl,
                            "Content-type\ttext/plain",
                            cpFilePath,
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
                fileDownloadService.notificationBuilder
                    ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
                    ?.setContentTitle(
                        FileDownloadStatus.CONNECTION_ERR.title
                    )
                    ?.setContentText(
                        FileDownloadStatus.CONNECTION_ERR.message.format(
                            mainUrl
                        )
                    )
                fileDownloadService.notificationBuilder?.clearActions()
                fileDownloadService.notificationBuilder?.addAction(
                    com.puutaro.commandclick.R.drawable.icons8_cancel,
                    FileDownloadLabels.CLOSE.label,
                    fileDownloadService.cancelPendingIntent
                )?.build()?.let {
                    fileDownloadService.notificationManager.notify(
                        fileDownloadService.chanelId,
                        it
                    )
                }
                fileDownloadService.fileDownloadJob?.cancel()
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    FileSystems.writeFromByteArray(
                        destiFileParentDirPath,
                        destiFileName,
                        con
                    )
                }
                withContext(Dispatchers.Main){
                    if(it % 3 != 0) return@withContext
                    fileDownloadService.notificationBuilder
                        ?.setContentText(
                            "[$it / $cpFileListIndexSize] get ok ${destiFileName}",
                        )?.build()?.let {
                             fileDownloadService.notificationManager.notify(
                                 fileDownloadService.chanelId,
                                it
                            )
                    }
                }
                if(it < cpFileListIndexSize) return@launch
                withContext(Dispatchers.IO){
                    CurlManager.post(
                        mainUrl,
                        "Content-type\ttext/plain",
                        ReceivePathMacroType.CLOSE_COPY_SERVER.name,
                        curlTimeoutMiliSec,
                    )
                }
                withContext(Dispatchers.IO){
                    fileDownloadService.notificationBuilder
                        ?.setSmallIcon(android.R.drawable.stat_sys_download_done)
                        ?.setContentTitle(
                            FileDownloadStatus.COMP.title
                        )
                        ?.setContentText(
                            FileDownloadStatus.COMP.message.format(
                                getPathOrFannelRawName
                            )
                        )
                    fileDownloadService.notificationBuilder?.clearActions()
                    fileDownloadService.notificationBuilder?.addAction(
                        com.puutaro.commandclick.R.drawable.icons8_cancel,
                        FileDownloadLabels.CLOSE.label,
                        fileDownloadService.cancelPendingIntent
                    )?.build()?.let {
                        fileDownloadService.notificationManager.notify(
                            fileDownloadService.chanelId,
                            it
                        )
                    }
                }
            }
        }
    }

    private fun makeCpFileListCon(
        getPathOrFannelRawName: String,
        fileListCon: String,
        currentAppDirPath: String,
    ): List<String> {
        return fileListCon.let {
            CcPathTool.convertIfFunnelRawNamePathToFullPath(
                currentAppDirPath,
                it
            )
        }.let {
            CcPathTool.convertAppDirPathToLocal(
                it,
                currentAppDirPath
            )
        }.split("\n").filter {
            it.startsWith(
                CcPathTool.convertIfFunnelRawNamePathToFullPath(
                    currentAppDirPath,
                    getPathOrFannelRawName
                )
            )
        }
    }

}