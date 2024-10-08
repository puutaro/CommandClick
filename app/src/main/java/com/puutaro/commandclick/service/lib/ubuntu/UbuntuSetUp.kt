package com.puutaro.commandclick.service.lib.ubuntu

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.extra.UbuntuEnvTsv
import com.puutaro.commandclick.common.variable.extra.WaitQuizPair
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuInfo
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.UbuntuProcessManager
import com.puutaro.commandclick.service.lib.ubuntu.variable.UbuntuStateType
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.gz.GzTool
import com.puutaro.commandclick.util.shell.LinuxCmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.exists


object UbuntuSetUp {

    private val rootfsTarGzUrl = UbuntuInfo.rootfsTarGzUrl
    private val downloadRootfsTarGzPathObj = File(
        UbuntuFiles.downloadDirPath,
        UbuntuFiles.rootfsTarGzName
    )

    fun exitDownloadMonitorProcess(){
        RootfsDownloader.exit()
    }
    fun set(
        ubuntuService: UbuntuService,
        monitorFileName: String,
    ): Job? {
       return try {
            CoroutineScope(Dispatchers.IO).launch {
                execSet(
                    ubuntuService,
                    monitorFileName,
                )
            }
        } catch (e: Exception){
           LogSystems.stdSSys(
                    "$e"
            )
           null
        }
    }
    private suspend fun execSet(
        ubuntuService: UbuntuService,
        monitorFileName: String,
    ) {
        val context  = ubuntuService.applicationContext
            ?: return
        val ubuntuFiles = ubuntuService.ubuntuFiles
            ?: return
        val startupFilePath = UbuntuFiles.startupFilePath
        val isUbuntuRestore = ubuntuService.isUbuntuRestore
        if(
            ubuntuFiles.ubuntuSetupCompFile.isFile
            && !isUbuntuRestore
        ) {
            withContext(Dispatchers.IO) {
                val busyboxExecutor = BusyboxExecutor(
                    context,
                    ubuntuFiles,
                )
                busyboxExecutor.executeProotCommand(
                    listOf("bash", startupFilePath),
                    monitorFileName = monitorFileName
                )
            }
            return
        }
        if(
            !NetworkTool.isWifi(context)
        ){
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeUbuntu.WIFI_WAIT_NITIFICATION.action
            )
            return
        }

        withContext(Dispatchers.IO) {
            LogSystems.stdSSys(
                "Ready.."
            )
            FileSystems.removeAndCreateDir(
                ubuntuFiles.filesOneRootfs.absolutePath
            )
        }
        if(
            !isUbuntuRestore
        ){
            RootfsDownloader.handle(
                ubuntuService,
            )
        }
        withContext(Dispatchers.IO) {
            initSetup(
                context,
                ubuntuFiles,
            )
        }
        withContext(Dispatchers.IO) {
            LogSystems.stdSSys(
                "Extract file..."
            )
        }
        withContext(Dispatchers.IO) {
            val err4 = LinuxCmd.chmod(
                context,
                ubuntuFiles.supportDir.absolutePath
            )
        }
        val busyboxExecutor = withContext(Dispatchers.IO) {
            BusyboxExecutor(
                context,
                ubuntuFiles,
            )
        }
        withContext(Dispatchers.IO) {
//            val extractShellRelativePath = "support/extractRootfs.sh"
            when(isUbuntuRestore){
                true -> {
                    extractTarForRestore(
                        context,
                        ubuntuFiles.filesOneRootfs.absolutePath
                    )
//                    busyboxExecutor.executeScript(
//                        "${extractShellRelativePath} on",
//                        monitorFileName
//                    )
//                    removeRootfsTarGz()
                }
                else -> {
                    GzTool.extractTarWithoutOwnership(
                        UbuntuFiles.downloadRootfsTarGzPath,
                        ubuntuFiles.filesOneRootfs.absolutePath,
                        true
                    )
                    removeRootfsTarGz()
//                    busyboxExecutor.executeScript(
//                        extractShellRelativePath,
//                        monitorFileName
//                    )
                }
            }
        }
        withContext(Dispatchers.IO) {
            ubuntuFiles.filesOneRootfsSupportDir.mkdirs()
            ubuntuFiles.filesOneRootfsSupportCommonDir.mkdirs()
            ubuntuFiles.filesOneRootfsStorageDir.mkdir()
            ubuntuFiles.filesOneRootfsEtcDProfileDir.mkdir()
        }
        withContext(Dispatchers.IO){
            AssetsFileManager.copyFileOrDirFromAssets(
                context,
                AssetsFileManager.ubunutSupportDirPath,
                AssetsFileManager.ubuntuSetupDirPath,
                ubuntuFiles.filesOneRootfs.absolutePath,
                emptyList()
            )
            AssetsFileManager.copyFileOrDirFromAssets(
                context,
                AssetsFileManager.ubunutSupportCmdDirPath,
                AssetsFileManager.ubunutSupportCmdDirPath,
                ubuntuFiles.filesOneRootfsUsrLocalBin.absolutePath,
                emptyList()
            )
            val filesOneRootfsSupportDirPath = ubuntuFiles.filesOneRootfsSupportDir.absolutePath
            FileSystems.writeFile(
                File(
                    filesOneRootfsSupportDirPath,
                    UbuntuFiles.waitQuizTsvName
                ).absolutePath,
                WaitQuizPair.makeQuizPairCon()
            )
            FileSystems.writeFile(
                File(
                    filesOneRootfsSupportDirPath,
                    UbuntuFiles.ubuntuEnvTsvName
                ).absolutePath,
                UbuntuEnvTsv.makeTsv(context)
            )
        }
        withContext(Dispatchers.IO) {
            LogSystems.stdSSys(
                "chmod.."
            )
            LinuxCmd.chmod(
                context,
                ubuntuFiles.filesOneRootfs.absolutePath
            )
//            val output = LinuxCmd.execCommand(
//                context,
//                listOf(
//                    "sh",
//                    "-c",
//                    "ls -al ${ubuntuFiles.filesOneRootfs.absolutePath}"
//                ).joinToString("\t")
//            )
//            LogSystems.stdSSys("ls ${ubuntuFiles.filesOneRootfs.absolutePath}: ${output}")
//            val output2 = LinuxCmd.execCommand(
//                context,
//                listOf(
//                    "sh",
//                    "-c",
//                    "ls -al ${ubuntuFiles.filesOneRootfsSupportDir.absolutePath}"
//                ).joinToString("\t")
//            )
//            LogSystems.stdSSys("ls ${ubuntuFiles.filesOneRootfsSupportDir.absolutePath}: ${output2}")
//
        }
        withContext(Dispatchers.IO) {
            LogSystems.stdSSys(
                "Setup start"
            )
            FileSystems.createDirs(
                ubuntuFiles.filesOneRootfsSupportDir.absolutePath
            )
            busyboxExecutor.executeProotCommand(
                listOf("bash", startupFilePath),
                monitorFileName = monitorFileName
            )
        }
    }

    private suspend fun extractTarForRestore(
        context: Context,
        filesOneRootfsPath: String,
    ){
//        val replacePair = Pair("___", "/")
        val ubuntuBackupRootfsDirPath = UbuntuFiles.getUbuntuBackupRootfsDirPath()
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        withContext(Dispatchers.IO) {
            val jobList = FileSystems.showDirList(
                ubuntuBackupRootfsDirPath
            ).map {
                async {
                    semaphore.withPermit {
                        val currentRootfsDirPath = File(ubuntuBackupRootfsDirPath, it).absolutePath
                        val rootfsFileName =  FileSystems.sortedFiles(
                            currentRootfsDirPath
                        ).firstOrNull() ?: return@withPermit
                        val rootfsFilePath = File(currentRootfsDirPath, rootfsFileName).absolutePath
                        GzTool.extractTarWithoutOwnership(
                            rootfsFilePath,
                            filesOneRootfsPath,
                            false
                        )
                    }
                }
            }
            jobList.forEach { it.await() }
        }
    }


    private object RootfsDownloader {

        private var monitorDownloadJob: Job? = null
        private var monitorDownloadErrJob: Job? = null

        fun exit(){
            monitorDownloadJob?.cancel()
            monitorDownloadErrJob?.cancel()
        }
        suspend fun handle(
            ubuntuService: UbuntuService,
        ) {
            try {
                execDownload(ubuntuService)
            } catch (e: Exception) {
                DownloadErr.cleanup(
                    ubuntuService,
                    null,
                    null,
                )
                return
            }
        }

        private suspend fun execDownload(
            ubuntuService: UbuntuService,
        ) {
            val downloadProgressChannel = Channel<Long>(UNLIMITED)
            val errChannel = Channel<String>(1)
            val ubuntuFiles =
                ubuntuService.ubuntuFiles
                    ?: return
            val supportDirPath = ubuntuFiles.supportDir.absolutePath
            FileSystems.createDirs(
                supportDirPath
            )
            removeRootfsTarGz()
            val rootfsTarGzOriginLength = withContext(Dispatchers.IO) {
                getRootfsTarGzOriginLength(rootfsTarGzUrl)
            }
            if(
                rootfsTarGzOriginLength == 0
            ){
                DownloadErr.cleanup(
                    ubuntuService,
                    downloadProgressChannel,
                    errChannel,
                )
                return
            }
            withContext(Dispatchers.IO) {
                monitorDownloadJob?.cancel()
                monitorDownloadJob = CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        LogSystems.stdSSys(
                            "Download start"
                        )
                        var currentLength = 0L
                        var previousProgressRate = 0L
                        for (progress in downloadProgressChannel) {
                            currentLength += progress
                            val currentProgressRate = (currentLength * 100) / rootfsTarGzOriginLength
                            if (
                                currentProgressRate <= previousProgressRate
                            ) continue
                            previousProgressRate = currentProgressRate
                            LogSystems.stdSSys(
                                "Download ${currentProgressRate}%"
                            )
                        }
                    }
                }
                monitorDownloadErrJob?.cancel()
                monitorDownloadErrJob = CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        for (msg in errChannel) {
                            DownloadErr.cleanup(
                                ubuntuService,
                                downloadProgressChannel,
                                errChannel,
                            )
                            errChannel.close()
                            break
                        }
                    }
                }
                try {
                    execRootfsTarGzDownload(
                        ubuntuService,
                        downloadProgressChannel,
                        errChannel,
                    )
                } catch (e: Exception) {
                    errChannel.send(e.toString())
                }
                downloadProgressChannel.close()
                errChannel.close()
                exitDownloadMonitorProcess()
            }
            if(
                isLessLength(
                    rootfsTarGzOriginLength
                )
            ) {
                DownloadErr.cleanup(
                    ubuntuService,
                    downloadProgressChannel,
                    errChannel,
                )
                return
            }
            withContext(Dispatchers.IO) {
                LogSystems.stdSSys("Download comp")
            }
        }

        private suspend fun getRootfsTarGzOriginLength(
            rootfsGzUrl: String
        ): Int {
            val totalLength = withContext(Dispatchers.IO) {
                try {
                    val url = URL(rootfsGzUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    val lengthOfFile = connection.contentLength
                    connection.disconnect()
                    lengthOfFile
                } catch (e: Exception) {
                    0
                }
            }
            return totalLength
        }

        private suspend fun execRootfsTarGzDownload(
            ubuntuService: UbuntuService,
            downloadProgressChannel: Channel<Long>,
            errChannel: Channel<String>,
        ) {
            withContext(Dispatchers.IO) {
                // put your url.this is sample url.
                val url = URL(rootfsTarGzUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                // download the file
                val totalLength = connection.contentLength
                if(
                    totalLength == 0
                ) {
                    DownloadErr.cleanup(
                        ubuntuService,
                        downloadProgressChannel,
                        errChannel,
                    )
                    return@withContext
                }
                val input = connection.getInputStream()
                //catalogfile is your destenition folder
                val output: OutputStream = FileOutputStream(
                    downloadRootfsTarGzPathObj
                )
                val data = ByteArray(1024)
                var total: Long = 0
                var previousTotal = 0L
                var count: Int
                var previousPercentage = 0L
                while (input.read(data).also { count = it } != -1) {
                    total += count
                    val curPercentage = (total * 100) / totalLength
                    val isOverCertainPercentage =
                        count > 0 && curPercentage - previousPercentage  > 5
                    val isComp = total >= totalLength
                    if (
                        isOverCertainPercentage
                        || isComp
                    ) {
                        val sendTotal = total - previousTotal
                        downloadProgressChannel.send(sendTotal)
                        previousTotal = total
                        previousPercentage = curPercentage
                    }
                    output.write(data, 0, count)
                }
                // flushing output
                output.flush()
                // closing streams
                output.close()
                input.close()
                connection.disconnect()
            }
        }
        private object DownloadErr {
            fun cleanup(
                ubuntuService: UbuntuService,
                downloadProgressChannel: Channel<Long>?,
                errChannel: Channel<String>?,
            ) {
                LogSystems.stdSErr(
                    "Download err",
                )
                downloadProgressChannel?.close()
                errChannel?.close()
                exitDownloadMonitorProcess()
                removeRootfsTarGz()
                UbuntuInitProcess.cancel(ubuntuService)
                UbuntuProcessManager.killAllCoroutineJob(ubuntuService)
                compDownloadErrNoti(ubuntuService)
            }

            private fun compDownloadErrNoti(
                ubuntuService: UbuntuService,
            ){
                val context = ubuntuService.applicationContext
                val notificationManager =
                    ubuntuService.getSystemService(
                        Service.NOTIFICATION_SERVICE
                    ) as NotificationManager
                val shouldMsg = UbuntuStateType.DOWNLOAD_ERR.message
                val channelId = ubuntuService.chanelId
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        for (i in 1..3) {
                            BroadcastSender.normalSend(
                                context,
                                BroadCastIntentSchemeUbuntu.DOWN_LOAD_ERR_NOTI.action
                            )
                            delay(300)
                            val currentDisplayMessage =
                                notificationManager.activeNotifications.filter {
                                    it.id == channelId
                                }.firstOrNull()?.notification?.extras?.getString("android.text")
                            if (
                                currentDisplayMessage == shouldMsg
                            ) break
                        }
                    }
                }
            }
        }

        private fun isLessLength(
            rootfsTarGzOriginLength: Int
        ): Boolean {
            if(
                rootfsTarGzOriginLength == 0
            ) return true
            val rootfsTarGzLength = downloadRootfsTarGzPathObj.length()
            if(
                rootfsTarGzLength == 0L
            ) return true
            return rootfsTarGzLength < rootfsTarGzOriginLength
        }
    }


    private fun copyAndRemoveDownloadRootfs(
        ubuntuFiles: UbuntuFiles
    ){
        FileSystems.copyFile (
            downloadRootfsTarGzPathObj.absolutePath,
            "${ubuntuFiles.filesDir}/${UbuntuFiles.rootfsTarGzName}"
        )
        removeRootfsTarGz()
    }

    private fun initSetup(
        context: Context?,
        ubuntuFiles: UbuntuFiles,
    ){
        FileSystems.createDirs(
            ubuntuFiles.supportDir.absolutePath
        )
        LogSystems.stdSSys(
            "Support copy start"
        )
        AssetsFileManager.copyFileOrDirFromAssets(
            context,
            AssetsFileManager.ubunutSupportDirPath,
            "ubuntu_setup",
            ubuntuFiles.supportDir.absolutePath,
            emptyList()
        )
        LogSystems.stdSSys(
            "Chmod start"
        )
        ubuntuFiles.supportDir.listFiles()?.forEach {
            ubuntuFiles.makePermissionsUsable(
                ubuntuFiles.supportDir.absolutePath,
                it.name
            )
        }
        FileSystems.createDirs(
            ubuntuFiles.filesOneRootfs.absolutePath
        )
        LogSystems.stdSSys(
            "Rootfs copy start"
        )
        ubuntuFiles.makePermissionsUsable(
            ubuntuFiles.filesOneRootfs.absolutePath,
            UbuntuFiles.rootfsTarGzName
        )
        File(
            "${ubuntuFiles.filesOneRootfs.absolutePath}/.success_filesystem_extraction"
        ).createNewFile()
        FileSystems.createDirs(
            ubuntuFiles.emulatedUserDir.absolutePath
        )
        ubuntuFiles.sdCardUserDir?.absolutePath?.let {
            FileSystems.createDirs(
                it
            )
        }
        ubuntuFiles.setupLinks()
    }

    private fun removeRootfsTarGz(
    ){
        FileSystems.removeFiles(
            downloadRootfsTarGzPathObj.absolutePath
        )
    }
}
