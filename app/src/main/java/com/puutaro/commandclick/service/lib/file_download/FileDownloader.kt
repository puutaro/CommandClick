package com.puutaro.commandclick.service.lib.file_download

import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.qr.CpFileKey
import com.puutaro.commandclick.service.lib.file_upload.ReceivePathMacroType
import com.puutaro.commandclick.service.FileDownloadService
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadStatus
import com.puutaro.commandclick.service.lib.file_download.libs.FileDownloadLabels
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
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
    private var commonDirPath: String? = null

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
        val context = fileDownloadService.applicationContext
        val mainUrl = fileDownloadService.mainUrl
        val getPathOrFannelRawName = fileDownloadService.fullPathPrFannelRawName
//        val parendDirPathForUploader =
//            fileDownloadService.currentAppDirPathForUploader ?: String()
        val parentDirPathSrc = File(getPathOrFannelRawName).parent
            ?.removeSuffix("/")
            ?: String()
        val parentDirPath = if(
            getPathOrFannelRawName.startsWith("/")
        ) parentDirPathSrc
        else String()
        val cpFileMapStr = sequenceOf(
            "${CpFileKey.CP_FILE_MACRO_FOR_SERVICE.key}=${ReceivePathMacroType.GET_FILE_LIST.name}",
            "${CpFileKey.PATH.key}=$parentDirPath",
//            "${CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key}=${parendDirPathForUploader}"
        ).joinToString("\t")
//        FileSystems.writeFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "qrReq.txt"
//            ).absolutePath,
//            cpFileMapStr
//        )
        for (i in 1..3) {
            val fileListConSrcByteArray = CurlManager.post(
                context,
                mainUrl,
                String(),
                cpFileMapStr,
                curlTimeoutMiliSec
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "down_get.txt").absolutePath,
//                listOf(
//                    "fileListConSrcByteArray: ${String(fileListConSrcByteArray)}",
//                    "cpFileMapStr: ${cpFileMapStr}",
//                ).joinToString("\n")
//            )
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
        val context = fileDownloadService.applicationContext
//        val currentAppDirPath = fileDownloadService.currentAppDirPath
        val mainUrl = fileDownloadService.mainUrl
        val getPathOrFannelRawName = fileDownloadService.fullPathPrFannelRawName
        val cpFileList = makeCpFileListCon(
            getPathOrFannelRawName,
            fileListCon,
        )
//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qrFileListCon.txt",
//            fileListCon
//        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "qrCpFileList.txt",).absolutePath,
//            cpFileList.joinToString("\n")
//        )
        if(
            cpFileList.isEmpty()
        ){
            cpFileListConNotFoundNoti(
                fileDownloadService,
                getPathOrFannelRawName
            )
            return
        }

        if(
            !fileDownloadService.isMoveToCurrentDir.isNullOrEmpty()
        ) commonDirPath = extractCommonDirPath(cpFileList)
        else commonDirPath = null

        val cpFileListIndexSize = cpFileList.size - 1
//        val parendDirPathForUploader =
//            fileDownloadService.currentAppDirPathForUploader ?: String()

//        FileSystems.writeFile(
//            UsePath.cmdclickDefaultAppDirPath,
//            "qrProcessLog.txt",
//            String()
//        )
        (cpFileList.indices).forEach {
            delay(100)
            val cpUploaderFilePath = cpFileList[it]
            val cpDownloaderFilePath = CcPathTool.convertAppDirPathToLocal(
                cpUploaderFilePath,
//                currentAppDirPath,
                commonDirPath
            )
//            FileSystems.updateFile(
//                UsePath.cmdclickDefaultAppDirPath,
//                "qrProcessLog.txt",
//                "cpUploaderFilePath: $cpUploaderFilePath\ncpDownloaderFilePath: $cpDownloaderFilePath\n" +
//                        "currentAppDirPath: $currentAppDirPath\ncommonDirPath: $commonDirPath"
//            )
            val con = withContext(Dispatchers.IO) {
                getFileCon(
                    context,
                    mainUrl,
//                    parendDirPathForUploader,
                    cpUploaderFilePath,
                )
            }
            if(
                String(con).trim() == invalidResponse
            ){
                cannotGetFileListConNoti(
                    fileDownloadService,
                    mainUrl,
                )
                return
            }
            val destiFileObj = withContext(Dispatchers.IO) {
                File(cpDownloaderFilePath)
            }
            val destiFileName = withContext(Dispatchers.IO) {
                destiFileObj.name
            }

            val cpFileMapStr = listOf(
                "${CpFileKey.CP_FILE_MACRO_FOR_SERVICE.key}=${ReceivePathMacroType.CLOSE_COPY_SERVER.name}"
            ).joinToString("\t")
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    FileSystems.writeFromByteArray(
                        cpDownloaderFilePath,
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
                        context,
                        mainUrl,
                        "Content-type\ttext/plain",
                        cpFileMapStr,
                        curlTimeoutMiliSec,
                    )
                }
                withContext(Dispatchers.IO){
                    BroadcastSender.normalSend(
                        fileDownloadService,
                        BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
                    )
                    BroadcastSender.normalSend(
                        fileDownloadService,
                        BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
                    )
                }
                withContext(Dispatchers.IO){
                    compNoti(
                        fileDownloadService,
                        getPathOrFannelRawName
                    )
                }
            }
        }
    }

    private suspend fun getFileCon(
        context: Context?,
        mainUrl: String,
//        parendDirPathForUploader: String,
        cpUploaderFilePath: String,
    ): ByteArray {

        var conSrc = byteArrayOf()
        val cpFileMapStr = listOf(
//            "${CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key}=${parendDirPathForUploader}",
            "${CpFileKey.PATH.key}=${cpUploaderFilePath}",
        ).joinToString("\t")
        for (i in 1..3) {
            conSrc = CurlManager.post(
                context,
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
        return conSrc
    }

    private fun cannotGetFileListConNoti(
        fileDownloadService: FileDownloadService,
        mainUrl: String,
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
    }
    private fun cpFileListConNotFoundNoti(
        fileDownloadService: FileDownloadService,
        getPathOrFannelRawName: String
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
    }

    private fun compNoti(
        fileDownloadService: FileDownloadService,
        getPathOrFannelRawName: String
    ){
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

    private fun makeCpFileListCon(
        getPathOrFannelRawName: String,
        fileListCon: String,
    ): List<String> {
        val isMakeServerCurrentDirPath =
            !getPathOrFannelRawName.startsWith("/")
        val grepPath =
            when(isMakeServerCurrentDirPath) {
                true
                -> concatCommonDirAsPrefix(
                    getPathOrFannelRawName,
                    fileListCon,
                )

                else
                -> makeGrepPathByIsDir(
                    getPathOrFannelRawName,
                    fileListCon,
                )
            }
        return fileListCon.split("\n").filter {
            it.startsWith(grepPath)
        }
    }

    private fun concatCommonDirAsPrefix(
        getPathOrFannelRawName: String,
        fileListCon: String,
    ): String {
        return fileListCon.let {
            CcPathTool.extractCurrentDirPathFromPath(it)
        }.split("\n").distinct().firstOrNull()?.let {
            "$it/${getPathOrFannelRawName}"
        } ?: String()
    }

    private fun makeGrepPathByIsDir(
        getPathOrFannelRawName: String,
        fileListCon: String,
    ): String {
        val isDir =
            fileListCon.contains("$getPathOrFannelRawName/")
        if(isDir) return "$getPathOrFannelRawName/"
        return getPathOrFannelRawName
    }

    private fun extractCommonDirPath(
        cpFileList: List<String>
    ): String {
        val cpFileListSize = cpFileList.size
        val rootDirPathEntry =
            cpFileList.minOf { it }
        val isAllCommon = cpFileList.filter {
            it.startsWith(rootDirPathEntry)
        }.size == cpFileListSize
        if(
            isAllCommon
            && File(rootDirPathEntry).isFile
        ) return rootDirPathEntry
        return File(rootDirPathEntry).parent ?: String()
    }
}