package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.utils.BusyboxExecutor
import com.puutaro.commandclick.utils.UbuntuFiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL


object UbuntuSetUp {

    private val rootfsTarGzName = UbuntuFiles.rootfsTarGzName
    private val downloadDirPath = UbuntuFiles.downloadDirPath
    private val downloadRootfsTarGzPath = UbuntuFiles.downloadRootfsTarGzPath
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
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nulafiles start"
        )
        val ubuntuFiles = UbuntuFiles(
            context,
        )
        val busyboxExecutor = BusyboxExecutor(
            context,
            ubuntuFiles,
        )
        if(
            ubuntuFiles.ubuntuCompFile.isFile
        ) {
            busyboxExecutor.executeProotCommand(
                listOf("bash", "startup.sh"),
                monitorFileName = UsePath.cmdClickMonitorFileName_1
            )
            return
        }
        FileSystems.removeAndCreateDir(
            ubuntuFiles.filesOneRootfs.absolutePath
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nbysubox instance start"
        )
        initSetup(
            context,
            ubuntuFiles,
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
                ubuntuFiles.supportDir
            ).joinToString("\t")
        )
        busyboxExecutor.executeScript(
            "support/extractRootfs.sh",
            UsePath.cmdClickMonitorFileName_1
        )
        FileSystems.removeFiles(
            ubuntuFiles.filesDir.absolutePath,
            rootfsTarGzName
        )
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
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nsupport copy start"
        )
        val rootfsSupportDir =  File("${ubuntuFiles.filesOneRootfs}/support")
        if(!rootfsSupportDir.isDirectory) rootfsSupportDir.mkdir()
        FileSystems.writeFile(
        "${ubuntuFiles.filesOneRootfs}/support",
            "default.pa",
            AssetsFileManager.readFromAssets(
                cmdIndexFragment.context,
                AssetsFileManager.etcPulseDefaultPa
            )
        )
        val lsResult = LinuxCmd.exec(
            cmdIndexFragment,
            listOf(
                "ls",
                ubuntuFiles.filesOneRootfs.absolutePath
            ).joinToString("\t")
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_1,
            "\n\nlsResult: ${lsResult}"
        )
        FileSystems.writeFile(
            ubuntuFiles.filesOneRootfs.absolutePath,
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
                    "download $rootfsTarGzName ${progress}%"
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
        ubuntuFiles: UbuntuFiles,
    ){
        FileSystems.createDirs(
            ubuntuFiles.supportDir.absolutePath
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
            ubuntuFiles.supportDir.absolutePath
        )
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
            "${ubuntuFiles.filesDir}/${rootfsTarGzName}"
        )

        ubuntuFiles.makePermissionsUsable(
            ubuntuFiles.filesOneRootfs.absolutePath,
            "rootfs.tar.gz"
        )
        File(
            "${ubuntuFiles.filesOneRootfs.absolutePath}/.success_filesystem_extraction"
        ).createNewFile()
        ubuntuFiles.emulatedUserDir.mkdirs()
        ubuntuFiles.sdCardUserDir?.mkdirs()
        ubuntuFiles.setupLinks()
    }
}
