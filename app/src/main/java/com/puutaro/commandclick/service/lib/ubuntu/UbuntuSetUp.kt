package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.extra.UbuntuEnvTsv
import com.puutaro.commandclick.common.variable.extra.WaitQuizPair
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuInfo
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.libs.ProcessManager
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL


object UbuntuSetUp {

    private val startupFilePath = "/support/startup.sh"
    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
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
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
                    "\n\n${e}"
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
        withContext(Dispatchers.IO) {
            FileSystems.removeAndCreateDir(
                ubuntuFiles.filesOneRootfs.absolutePath
            )
        }
        when(isUbuntuRestore){
            true
            -> {
                withContext(Dispatchers.IO) {
                    FileSystems.updateFile(
                        cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_2,
                        "copy start"
                    )
                    FileSystems.copyFile(
                        ubuntuFiles.ubuntuBackupRootfsFile.absolutePath,
                        "${ubuntuFiles.filesDir}/${UbuntuFiles.rootfsTarGzName}"
                    )
                }
            }
            else
            -> {
                withContext(Dispatchers.IO) {
                    downloadUbuntu(
                        context,
                        ubuntuService,
                        monitorFileName
                    )
                    delay(200)
                    FileSystems.copyFile(
                        UbuntuFiles.downloadRootfsTarGzPath,
                        "${ubuntuFiles.filesDir}/${UbuntuFiles.rootfsTarGzName}"
                    )
                }
            }

        }
        withContext(Dispatchers.IO) {
            initSetup(
                context,
                ubuntuFiles,
                monitorFileName
            )
        }
        withContext(Dispatchers.IO) {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
                "\nextract file"
            )
        }
        withContext(Dispatchers.IO) {
            val err4 = LinuxCmd.execCommand(
                listOf(
                    "chmod",
                    "-R",
                    "777",
                    ubuntuFiles.supportDir
                ).joinToString("\t")
            )
        }

        val busyboxExecutor = withContext(Dispatchers.IO) {
            BusyboxExecutor(
                context,
                ubuntuFiles,
            )
        }
        withContext(Dispatchers.IO) {
            busyboxExecutor.executeScript(
                "support/extractRootfs.sh",
                monitorFileName
            )
        }
        withContext(Dispatchers.IO) {
            FileSystems.removeFiles(
                ubuntuFiles.filesDir.absolutePath,
                UbuntuFiles.rootfsTarGzName
            )
            ubuntuFiles.filesOneRootfsSupportDir.mkdirs()

            ubuntuFiles.filesOneRootfsSupportDir.mkdirs()
            ubuntuFiles.filesOneRootfsSupportCommonDir.mkdirs()
            ubuntuFiles.filesOneRootfsStorageDir.mkdir()
            ubuntuFiles.filesOneRootfsEtcDProfileDir.mkdir()
        }
        withContext(Dispatchers.IO){
            AssetsFileManager.copyFileOrDirFromAssets(
                context,
                AssetsFileManager.ubunutSupportDirPath,
                "ubuntu_setup",
                ubuntuFiles.filesOneRootfs.absolutePath
            )
            FileSystems.copyDirectory(
                ubuntuFiles.filesOneRootfsSupportCmdDir.absolutePath,
                ubuntuFiles.filesOneRootfsUsrLocalBin.absolutePath
            )
            val filesOneRootfsSupportDirPath = ubuntuFiles.filesOneRootfsSupportDir.absolutePath
            FileSystems.writeFile(
                filesOneRootfsSupportDirPath,
                UbuntuFiles.waitQuizTsvName,
                WaitQuizPair.makeQuizPairCon()
            )
            FileSystems.writeFile(
                filesOneRootfsSupportDirPath,
                UbuntuFiles.ubuntuEnvTsvName,
                UbuntuEnvTsv.makeTsv()
            )
        }
        withContext(Dispatchers.IO) {
            LinuxCmd.execCommand(
                listOf(
                    "chmod",
                    "-R",
                    "777",
                    ubuntuFiles.filesOneRootfsUsrLocalBin.absolutePath
                ).joinToString("\t")
            )
        }
        withContext(Dispatchers.IO) {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
                "\nexec setup"
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

    private suspend fun downloadUbuntu(
        context: Context,
        ubuntuService: UbuntuService,
        monitorFileName: String,
    ){
        try {
            execDownloadUbuntu(
                ubuntuService,
                monitorFileName
            )
        } catch (e: Exception){
            val setupIntent = Intent()
            setupIntent.action = BroadCastIntentScheme.ON_UBUNTU_SETUP_NOTIFICATION.action
            context.sendBroadcast(setupIntent)
            return
        }
    }

    private suspend fun execDownloadUbuntu(
        ubuntuService: UbuntuService,
        monitorFileName: String,
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles ?: return
        val supportDirPath = ubuntuFiles.supportDir.absolutePath
        val downloadCompTxt = ubuntuFiles.ubuntuRootfsDownloadCompFile.name
        FileSystems.createDirs(
            supportDirPath
        )
        withContext(Dispatchers.IO) {
            if(
                UbuntuInfo.onForDev
            ) return@withContext
            if(
                File(
                    "${supportDirPath}/${downloadCompTxt}",
                ).isFile
            ) return@withContext
            FileSystems.removeFiles(
                UbuntuFiles.downloadDirPath,
                UbuntuFiles.rootfsTarGzName
            )
            // put your url.this is sample url.
            val url = URL(UbuntuInfo.arm64UbuntuRootfsUrl)
            val conection = url.openConnection()
            conection.connect()
            val lenghtOfFile = conection.contentLength
            // download the file
            val input = conection.getInputStream()
            //catalogfile is your destenition folder
            val output: OutputStream = FileOutputStream(
                UbuntuFiles.downloadRootfsTarGzPath
            )
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            var progress = 0L
            var previousDisplayProgress: Long = 0
            while (input.read(data).also { count = it } != -1) {
                val processNum = ProcessManager.processNumCalculator(ubuntuService)
                if(processNum == 0) {
                    FileSystems.removeFiles(
                        UbuntuFiles.downloadDirPath,
                        UbuntuFiles.rootfsTarGzName
                    )
                    break
                }
                total += count
                previousDisplayProgress = progress
                progress = total * 100 / lenghtOfFile
                output.write(data, 0, count)
                if (progress <= previousDisplayProgress) continue
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    "download ${progress}%"
                )
            }
            // flushing output
            output.flush()
            // closing streams
            output.close()
            input.close()
        }
        withContext(Dispatchers.IO){
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
                "\ndownlaod comp"
            )
            FileSystems.writeFile(
                supportDirPath,
                downloadCompTxt,
                String()
            )
        }
    }

    private fun initSetup(
        context: Context?,
        ubuntuFiles: UbuntuFiles,
        monitorFileName: String,
    ){
        FileSystems.createDirs(
            ubuntuFiles.supportDir.absolutePath
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            monitorFileName,
            "support copy start"
        )
        AssetsFileManager.copyFileOrDirFromAssets(
            context,
            AssetsFileManager.ubunutSupportDirPath,
            "ubuntu_setup",
            ubuntuFiles.supportDir.absolutePath
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            monitorFileName,
            "chmod start"
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
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            monitorFileName,
            "rootfs copy start"
        )
        if(
            !UbuntuInfo.onForDev
        ) {
            FileSystems.removeFiles(
                UbuntuFiles.downloadDirPath,
                UbuntuFiles.rootfsTarGzName
            )
        }

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
}
