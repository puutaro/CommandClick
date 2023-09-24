package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.system.Os
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UbuntuSetPath
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.utils.BusyboxExecutor
import com.puutaro.commandclick.utils.UlaFiles
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.time.LocalDateTime


object UbuntuSetUp {

    private val genRootDir = UbuntuSetPath.genRootDir
    private val ubuntuRfs = "$genRootDir/ubuntuARM64-fs"
    private val ubuntuGenShellPath = "${genRootDir}/android-ubuntu-gen.sh"
    private val startUbuntuShellPath = "${genRootDir}/start-ubuntu.sh"
    private val rootfsTarGzName = UbuntuSetPath.rootfsTarGzName
    private val downloadDirPath = UbuntuSetPath.downlaodDirPath
    private val downloadRootfsTarGzPath = UbuntuSetPath.downloadRootfsTarGzPath
    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private val arm64UbuntuRootfsUrl =
        "https://github.com/puutaro/CommandClick-Linux/releases/download/v0.0.1/rootfs.tar.gz"
//        #"https://partner-images.canonical.com/core/jammy/" +
//                "current/ubuntu-jammy-core-cloudimg-arm64-root.tar.gz"

    fun set(
        cmdIndexFragment: CommandIndexFragment
    ){
        try {
            CoroutineScope(Dispatchers.IO).launch {
                execSet(cmdIndexFragment)
            }
        } catch (e: Exception){
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "\n\n${e}"
            )
        }
    }
    private suspend fun execSet(
        cmdIndexFragment: CommandIndexFragment
    ) {
        val context  = cmdIndexFragment.context
            ?: return
        FileSystems.writeFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            String()
        )
        downloadUbuntu(UsePath.cmdClickMonitorFileName_1)
//        withContext(Dispatchers.IO) {
//            if(
//                File(
//                    downloadRootfsTarGzPath
//                ).isFile
//            ) return@withContext
//            // put your url.this is sample url.
//            val url = URL(arm64UbuntuRootfsUrl)
//            val conection = url.openConnection()
//            conection.connect()
//            val lenghtOfFile = conection.contentLength
//            // download the file
//            val input = conection.getInputStream()
//            //catalogfile is your destenition folder
//            val output: OutputStream = FileOutputStream(
//                downloadRootfsTarGzPath
//            )
//            val data = ByteArray(1024)
//            var total: Long = 0
//            var count: Int
//            var progress = 0L
//            var previoutDisplayProgress: Long = 0
//            while (input.read(data).also { count = it } != -1) {
//                total += count
//                previoutDisplayProgress = progress
//                progress = total * 100 / lenghtOfFile
//                output.write(data, 0, count)
//                if(progress <= previoutDisplayProgress) continue
//                FileSystems.updateFile(
//                    cmdclickMonitorDirPath,
//                    UsePath.cmdClickMonitorFileName_1,
//                    "\ndownload ${rootfsTarGzName} ${progress}"
//                )
//            }
//            // flushing output
//            output.flush()
//            // closing streams
//            output.close()
//            input.close()
//        }
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nulafiles start"
        )
        val ulaFiles = UlaFiles(
            context,
            context.applicationInfo.nativeLibraryDir
        )
        val busyboxExecutor = BusyboxExecutor(
            context,
            ulaFiles,
        )
        if(
            ulaFiles.ubuntuCompFile.isFile
        ) {
            busyboxExecutor.executeProotCommand(
                listOf("bash", "startup.sh"),
                monitorFileName = UsePath.cmdClickMonitorFileName_1
            )
            return
        }
        FileSystems.removeAndCreateDir(
            ulaFiles.filesOneRootfs.absolutePath
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nbysubox instance start"
        )
        initSetup(
            context,
            ulaFiles,
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nextract file"
        )
        val err4 = LinuxCmd.exec(
            cmdIndexFragment,
            listOf(
                "chmod",
                "-R",
                "777",
                ulaFiles.supportDir
            ).joinToString("\t")
        )
        busyboxExecutor.executeScript(
            "support/extractRootfs.sh",
            UsePath.cmdClickMonitorFileName_1
        )
        FileSystems.removeFiles(
            ulaFiles.filesDir.absolutePath,
            rootfsTarGzName
        )
//        busyboxExecutor.extractRootFs(
//            monitorFileName = UsePath.cmdClickMonitorFileName_1
//        )
        ulaFiles.filesOneRootfsSupportDir.mkdirs()
        AssetsFileManager.copyFileOrDirFromAssets(
            context,
            AssetsFileManager.ubunutSupportDirPath,
            "ubuntu_setup",
            ulaFiles.filesOneRootfs.absolutePath
        )
        ulaFiles.filesOneRootfsSupportCommonDir.mkdirs()
        ulaFiles.filesOneRootfsStorageDir.mkdir()
//        ulaFiles.filesOneRootfsStorageEmurated0Dir.mkdirs()
        ulaFiles.filesOneRootfsUsrLocalBinSudo.mkdirs()
        ulaFiles.filesOneRootfsEtcDProfileDir.mkdir()
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nsupport copy start"
        )
        val rootfsSupportDir =  File("${ulaFiles.filesOneRootfs}/support")
        if(!rootfsSupportDir.isDirectory) rootfsSupportDir.mkdir()
        FileSystems.writeFile(
        "${ulaFiles.filesOneRootfs}/support",
            "default.pa",
            AssetsFileManager.readFromAssets(
                cmdIndexFragment.context,
                AssetsFileManager.etcPulseDefaultPa
            )
        )
//        "${ulaFiles.documentDirPath}/rootfs/support"
//        File("${ulaFiles.documentDirPath}/rootfs/support").copyRecursively(
//            rootfsSupportDir,
//            overwrite = true,
//            onError = { file, exception ->
//                OnErrorAction.SKIP
//                // do something with file or exception
//                // the last expression must be of type OnErrorAction
//            }
//        )
        val lsResult = LinuxCmd.exec(
            cmdIndexFragment,
            listOf(
                "ls",
                ulaFiles.filesOneRootfs.absolutePath
            ).joinToString("\t")
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nlsResult: ${lsResult}"
        )
        FileSystems.writeFile(
            ulaFiles.filesOneRootfs.absolutePath,
            "startup.sh",
            AssetsFileManager.readFromAssets(
                cmdIndexFragment.context,
                AssetsFileManager.ubunutStartupScriptPath
            )
        )
        busyboxExecutor.executeProotCommand(
            listOf("bash", "startup.sh"),
            monitorFileName = UsePath.cmdClickMonitorFileName_1
        )
    }

    private suspend fun downloadUbuntu(
        monitorFileName: String,
    ){
        withContext(Dispatchers.IO) {
            if(
                File(
                    downloadRootfsTarGzPath
                ).isFile
            ) return@withContext
            // put your url.this is sample url.
            val url = URL(arm64UbuntuRootfsUrl)
            val conection = url.openConnection()
            conection.connect()
            val lenghtOfFile = conection.contentLength
            // download the file
            val input = conection.getInputStream()
            //catalogfile is your destenition folder
            val output: OutputStream = FileOutputStream(
                downloadRootfsTarGzPath
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
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    "download $rootfsTarGzName $progress"
                )
            }
            // flushing output
            output.flush()
            // closing streams
            output.close()
            input.close()
        }
    }

    private fun initSetup(
        context: Context?,
        ulaFiles: UlaFiles,
    ){
        FileSystems.createDirs(
            ulaFiles.supportDir.absolutePath
        )
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "${
                ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                ).readText()
            }\n\nsupport copy start"
        )
        AssetsFileManager.copyFileOrDirFromAssets(
            context,
            AssetsFileManager.ubunutSupportDirPath,
            "ubuntu_setup",
            ulaFiles.supportDir.absolutePath
        )
//        docSupportDir.copyRecursively(
//            supportDir,
//            overwrite = true,
//            onError = { file, exception ->
//                OnErrorAction.SKIP
//                // do something with file or exception
//                // the last expression must be of type OnErrorAction
//            }
//        )
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "${
                ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                ).readText()
            }\n\nchmod start"
        )
        ulaFiles.supportDir.listFiles()?.forEach {
            ulaFiles.makePermissionsUsable(
                ulaFiles.supportDir.absolutePath,
                it.name
            )
        }
        FileSystems.createDirs(
            ulaFiles.filesOneRootfs.absolutePath
        )
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "${
                ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                ).readText()
            }\n\nrootfs copy start"
        )
        FileSystems.copyFile(
            downloadRootfsTarGzPath,
            "${ulaFiles.filesDir}/${rootfsTarGzName}"
        )

        ulaFiles.makePermissionsUsable(
            ulaFiles.filesOneRootfs.absolutePath,
            "rootfs.tar.gz"
        )
        File(
            "${ulaFiles.filesOneRootfs.absolutePath}/.success_filesystem_extraction"
        ).createNewFile()
//        File(
//            "${filesOneRootfs.absolutePath}/support/.proot_version"
//        ).writeText(String())
        File(
            "${ulaFiles.documentDirPath}/1RootfsSupport.txt"
        ).writeText(
            "${ulaFiles.filesOneRootfs.absolutePath}/support\n${
                File(
                    "${ulaFiles.filesOneRootfs.absolutePath}/support"
                ).list()?.joinToString("\n")
                    ?: String()
            }"
        )
        ulaFiles.emulatedUserDir.mkdirs()
        ulaFiles.sdCardUserDir?.mkdirs()
        ulaFiles.setupLinks()
    }
}
