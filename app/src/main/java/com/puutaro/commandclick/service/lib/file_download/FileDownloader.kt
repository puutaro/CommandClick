package com.puutaro.commandclick.service.lib.file_download

import com.puutaro.commandclick.proccess.qr.CpFileKey
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
    private var isWaitForFileListCon = true
    private var fileListConSrc = String()

    fun save(
        fileDownloadService: FileDownloadService,
    ): Job {
        isWaitForFileListCon = true
        var fileListCon = String()
        return CoroutineScope(Dispatchers.IO).launch {

            fileDownloadService.getListFileConJob = CoroutineScope(Dispatchers.IO).launch {
                fileListConSrc = String()
                withContext(Dispatchers.IO) {
                    getFileListCon(
                        fileDownloadService
                    )
                }
                withContext(Dispatchers.IO) {
                    fileListCon = fileListConSrc
                    isWaitForFileListCon = false
                }
            }
            withContext(Dispatchers.IO){
                waitGetFileListCon(
                    fileDownloadService
                )
            }
            if(
                !CurlManager.isConnOk(
                    fileListCon.toByteArray()
                )
                || fileListCon.isEmpty()
            ) {
                withContext(Dispatchers.IO){
                    failureGetFileListConNoti(
                        fileDownloadService
                    )
                }
                return@launch
            }
            withContext(Dispatchers.IO) {
                execSaveFile(
                    fileDownloadService,
                    fileListCon,
                )
            }
        }
    }

    private suspend fun getFileListCon(
        fileDownloadService: FileDownloadService
    ){
        val mainUrl = fileDownloadService.mainUrl
        val getPathOrFannelRawName = fileDownloadService.fullPathPrFannelRawName
        val parendDirPathForUploader =
            fileDownloadService.currentAppDirPathForUploader ?: String()
        val parentDirPathSrc = File(getPathOrFannelRawName).parent
            ?.removeSuffix("/")
            ?: String()
        val parentDirPath = if(
            getPathOrFannelRawName.startsWith("/")
        ) parentDirPathSrc
        else String()
        val cpFileMapStr = listOf(
            "${CpFileKey.CP_FILE_MACRO_FOR_SERVICE.key}=${ReceivePathMacroType.GET_FILE_LIST.name}",
            "${CpFileKey.PATH.key}=$parentDirPath",
            "${CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key}=${parendDirPathForUploader}"
        ).joinToString("\t")
        for (i in 1..3) {
            val fileListConSrcByteArray = CurlManager.post(
                mainUrl,
                String(),
                cpFileMapStr,
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


    private suspend fun waitGetFileListCon(
        fileDownloadService: FileDownloadService
    ){
        val waitLimitTimes = 600
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

    private fun failureGetFileListConNoti(
        fileDownloadService: FileDownloadService
    ){
        val getPathOrFannelRawName = fileDownloadService.fullPathPrFannelRawName
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

    private suspend fun execSaveFile(
        fileDownloadService: FileDownloadService,
        fileListCon: String,
    ){
        val currentAppDirPath = fileDownloadService.currentAppDirPath
        val mainUrl = fileDownloadService.mainUrl
        val getPathOrFannelRawName = fileDownloadService.fullPathPrFannelRawName
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
        val parendDirPathForUploader =
            fileDownloadService.currentAppDirPathForUploader ?: String()
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
                    val cpFileMapStr = listOf(
                        "${CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key}=${parendDirPathForUploader}",
                        "${CpFileKey.PATH.key}=${cpFilePath}",
                    ).joinToString("\t")
                    for (i in 1..3) {
                        conSrc = CurlManager.post(
                            mainUrl,
                            "Content-type\ttext/plain",
                            cpFileMapStr,
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
            val cpFileMapStr = listOf(
                "${CpFileKey.CP_FILE_MACRO_FOR_SERVICE.key}=${ReceivePathMacroType.CLOSE_COPY_SERVER.name}"
            ).joinToString("\t")
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
                        cpFileMapStr,
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