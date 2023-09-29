package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.variable.ServiceNotificationId
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.ReadText
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
//        #"https://partner-images.canonical.com/core/jammy/" +
//                "current/ubuntu-jammy-core-cloudimg-arm64-root.tar.gz"
    private val startupFilePath = "/support/startup.sh"
    fun set(
        context: Context?,
        monitorFileName: String,
        notificationManager: NotificationManagerCompat?,
        notificationBuilder: NotificationCompat.Builder?
    ): Job? {
       return try {
            CoroutineScope(Dispatchers.IO).launch {
                execSet(
                    context,
                    monitorFileName,
                    notificationManager,
                    notificationBuilder
                )
            }
        } catch (e: Exception){
            FileSystems.updateFile(
                UsePath.cmdclickMonitorDirPath,
                monitorFileName,
                "\n\n${e}"
            )
           null
        }
    }
    private suspend fun execSet(
        contextSrc: Context?,
        monitorFileName: String,
        notificationManager: NotificationManagerCompat?,
        notificationBuilder: NotificationCompat.Builder?
    ) {
        val context  = contextSrc
            ?: return
        val ubuntuFiles = UbuntuFiles(
            context,
        )
        downloadUbuntu(
            ubuntuFiles,
            monitorFileName
        )
        FileSystems.updateFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "\n\nulafiles start"
        )
        val busyboxExecutor = BusyboxExecutor(
            context,
            ubuntuFiles,
        )
        if(
            ubuntuFiles.ubuntuSetupCompFile.isFile
        ) {
            busyboxExecutor.executeProotCommand(
                listOf("bash", startupFilePath),
                monitorFileName = monitorFileName
            )
            return
        }
        val isWifi = NetworkTool.isWifi(context)
        if(!isWifi) {
            val cancelUbuntuServicePendingIntent = PendingIntentCreator.create(
                context,
                BroadCastIntentScheme.STOP_UBUNTU_SERVICE.action,
            )
            notificationBuilder?.setContentTitle(UbuntuStateType.WIFI_WAIT.title)
            notificationBuilder?.setContentText(UbuntuStateType.WIFI_WAIT.message)
            notificationBuilder?.clearActions()
            notificationBuilder?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                ButtonLabel.RESTART.label,
                cancelUbuntuServicePendingIntent,
            )
            notificationBuilder?.build()?.let {
                notificationManager?.notify(
                    ServiceNotificationId.ubuntuServer,
                    it
                )
            }
            return
        }
        FileSystems.removeAndCreateDir(
            ubuntuFiles.filesOneRootfs.absolutePath
        )
        delay(200)
        FileSystems.updateFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "\n\nbysubox instance start"
        )
        initSetup(
            context,
            ubuntuFiles,
            monitorFileName
        )
        FileSystems.updateFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "\n\nextract file"
        )
        val err4 = LinuxCmd.exec(
            listOf(
                "chmod",
                "-R",
                "777",
                ubuntuFiles.supportDir
            ).joinToString("\t")
        )
        busyboxExecutor.executeScript(
            "support/extractRootfs.sh",
            monitorFileName
        )
        FileSystems.removeFiles(
            ubuntuFiles.filesDir.absolutePath,
            UbuntuFiles.rootfsTarGzName
        )
        ubuntuFiles.filesOneRootfsSupportDir.mkdirs()

        ubuntuFiles.filesOneRootfsSupportDir.mkdirs()
        AssetsFileManager.copyFileOrDirFromAssets(
            context,
            AssetsFileManager.ubunutSupportDirPath,
            "ubuntu_setup",
            ubuntuFiles.filesOneRootfs.absolutePath
        )
        ubuntuFiles.filesOneRootfsSupportCommonDir.mkdirs()
        ubuntuFiles.filesOneRootfsStorageDir.mkdir()
//        ulaFiles.filesOneRootfsStorageEmurated0Dir.mkdirs()
        ubuntuFiles.filesOneRootfsUsrLocalBinSudo.mkdirs()
        ubuntuFiles.filesOneRootfsEtcDProfileDir.mkdir()
        FileSystems.updateFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "\n\nsupport copy start"
        )
        val rootfsSupportDir =  File("${ubuntuFiles.filesOneRootfs}/support")
        if(!rootfsSupportDir.isDirectory) rootfsSupportDir.mkdir()
//        FileSystems.writeFile(
//        "${ubuntuFiles.filesOneRootfs}/support",
//            "default.pa",
//            AssetsFileManager.readFromAssets(
//                context,
//                AssetsFileManager.etcPulseDefaultPa
//            )
//        )
        val lsResult = LinuxCmd.exec(
            listOf(
                "ls",
                ubuntuFiles.filesOneRootfs.absolutePath
            ).joinToString("\t")
        )
        FileSystems.updateFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "\n\nlsResult: ${lsResult}"
        )
        busyboxExecutor.executeProotCommand(
            listOf("bash", startupFilePath),
            monitorFileName = monitorFileName
        )
    }

    private suspend fun downloadUbuntu(
        ubuntuFiles: UbuntuFiles,
        monitorFileName: String,
    ){
        val supportDirPath = ubuntuFiles.supportDir.absolutePath
        val downloadCompTxt = "downloadComp.txt"
        FileSystems.createDirs(
            supportDirPath
        )
        withContext(Dispatchers.IO) {
            return@withContext
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
            val url = URL(UbuntuFiles.arm64UbuntuRootfsUrl)
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
            var previoutDisplayProgress: Long = 0
            while (input.read(data).also { count = it } != -1) {
                total += count
                previoutDisplayProgress = progress
                progress = total * 100 / lenghtOfFile
                output.write(data, 0, count)
                if(progress <= previoutDisplayProgress) continue
                FileSystems.updateFile(
                    UsePath.cmdclickMonitorDirPath,
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
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "${
                ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    monitorFileName,
                ).readText()
            }\n\nsupport copy start"
        )
        AssetsFileManager.copyFileOrDirFromAssets(
            context,
            AssetsFileManager.ubunutSupportDirPath,
            "ubuntu_setup",
            ubuntuFiles.supportDir.absolutePath
        )
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "${
                ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    monitorFileName,
                ).readText()
            }\n\nchmod start"
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
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            monitorFileName,
            "${
                ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    monitorFileName,
                ).readText()
            }\n\nrootfs copy start"
        )
        FileSystems.copyFile(
            UbuntuFiles.downloadRootfsTarGzPath,
            "${ubuntuFiles.filesDir}/${UbuntuFiles.rootfsTarGzName}"
        )
//        FileSystems.removeFiles(
//            UbuntuFiles.downloadDirPath,
//            UbuntuFiles.rootfsTarGzName
//        )

        ubuntuFiles.makePermissionsUsable(
            ubuntuFiles.filesOneRootfs.absolutePath,
            "rootfs.tar.gz"
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

    fun killAllCoroutineJob(
        ubuntuCoroutineJobsHashMap: HashMap<String, Job?>
    ){
        ubuntuCoroutineJobsHashMap.forEach { t, u ->
            u?.cancel()
        }
    }
}

enum class UbuntuProcessType {
    SetUp,
    SetUpMonitoring,
    PulseaudioSetUp,
    monitoringProcessNum,
}

enum class UbuntuStateType(
    val title: String,
    val message: String,
) {
    WAIT("Wait..", "Wait.."),
    UBUNTU_SETUP_WAIT(
        "Ubuntu Setup, Ok?",
        "Take 5 minutes for install"
    ),
    WIFI_WAIT("Connect wifi!", "Connect wifi! and restart"),
    ON_SETUP("Ubuntu Setup..", "Ubuntu Setup..(take 5 minutes)"),
    RUNNING("Ubuntu running..", "%d process.."),
}

enum class ButtonLabel(
    val label: String,
){
    CANCEL("CANCEL"),
    RESTART("RESTART"),
    SETUP("SETUP"),
    TERMINAL("TERMINAL"),
}