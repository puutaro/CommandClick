package com.puutaro.commandclick.proccess.ubuntu

import android.content.Context
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class BusyboxExecutor(
    private val context: Context?,
    private val ubuntuFiles: UbuntuFiles,
//    private val prootDebugLogger: ProotDebugLogger,
    private val busyboxWrapper: BusyboxWrapper = BusyboxWrapper(ubuntuFiles)
) {
    private val className = this::class.java.name
    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath

    fun executeScript(
        scriptCall: String,
        monitorFileName: String
    ) {
        val updatedCommand = busyboxWrapper.wrapScript(scriptCall)

        return runCommand(updatedCommand, monitorFileName)
    }

    fun executeCommand(
        command: String,
        monitorFileName: String
    ) {
        val updatedCommand = busyboxWrapper.wrapCommand(command)

        return runCommand(updatedCommand, monitorFileName)
    }
    fun executeCommandByStreaming(
        command: String,
        monitorFileName: String
    ) {
        val updatedCommand = busyboxWrapper.wrapCommand(command)

        return runCommand(updatedCommand, monitorFileName)
    }

    fun getCmdOutput(
        commandSrc: String,
        envMapSrc: Map<String, String>? = null,
    ): String {
        val envMap = makeRepValHashMap(
            envMapSrc,
        )
        val command = CmdClickMap.replace(
            commandSrc,
            envMap,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "shellLog_getOuput.txt").absolutePath,
//            listOf(
//                "commandSrc: ${commandSrc}",
//                "envMapSrc: ${envMapSrc}",
//                "envMap: ${envMap}",
//                "command: ${command}",
//                "out ${execCommandForOutput(
//                    listOf("sh", "-c", command),
//                    null
////            envMap
//                )}",
//            ).joinToString("\n\n\n")
//        )
        return execCommandForOutput(
            listOf("sh", "-c", command),
            null
//            envMap
        )
    }

    private fun makeRepValHashMap(
        extraArgsMap: Map<String, String>?,
    ): Map<String, String> {
        if(
            extraArgsMap.isNullOrEmpty()
        ) return hashMapOf()
        val envMapSrc = extraArgsMap.map {
            val keyName = it.key
            val value = it.value
            val subCmd =
                value.removePrefix("$(").removeSuffix(")")
            val isSubCmdBlock =
                subCmd != value
            when(isSubCmdBlock){
                true -> {
                        keyName to getCmdOutput(
                            subCmd
                        )

                }
                else -> keyName to value
            }
        }.toMap()
        return envMapSrc
//        HashMap(envMapSrc)
    }

    private fun execCommandForOutput(
        command: List<String>,
        env: HashMap<String, String>?
    ): String {
        if (!busyboxWrapper.busyboxIsPresent()) {
            ubuntuFiles.setupLinksForBusyBox()
        }
//        val env = busyboxWrapper.getBusyboxEnv()
        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(ubuntuFiles.filesDir)
        val busyboxHashMap = busyboxWrapper.getBusyboxEnv()
        val envHash = env?.let {
            env + busyboxHashMap
        } ?: busyboxHashMap
        processBuilder.environment().putAll(envHash)
        processBuilder.redirectErrorStream(true)
        var output = String()
        try {
            val process = processBuilder.start()
            output = output(
                process,
                UsePath.cmdClickMonitorFileName_2
            )
            process.waitFor()
        } catch (err: Exception) {
            LogSystems.stdErr(
                context,
                "$err"
            )
        }
        return output.removePrefix("\n")
    }
    private fun runCommand(
        command: List<String>,
        monitorFileName: String
    ) {
        val functionName = object{}.javaClass.enclosingMethod?.name
        if (!busyboxWrapper.busyboxIsPresent()) {
            return FileSystems.updateFile(
                File(
                    cmdclickMonitorDirPath,
                    monitorFileName
                ).absolutePath,
                "${className} ${functionName} no busybox"
            )
        }

        val env = busyboxWrapper.getBusyboxEnv()
        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(ubuntuFiles.filesDir)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)

        return try {
            val process = processBuilder.start()
            streaming(
                process,
                monitorFileName
            )
            val exitCode = process.waitFor()
            outputFailureStatus(
                process,
                exitCode,
                "$functionName",
                monitorFileName
            )
        } catch (err: Exception) {
            FileSystems.updateFile(
                File(
                    cmdclickMonitorDirPath,
                    monitorFileName
                ).absolutePath,
                "$err"
            )
        }
    }

    fun executeKillProcessFromList(
        targetProcessNameList: List<String>,
        monitorFileName: String
    ){
        executeProotCommand(
            listOf("bash", "/support/killProcTree.sh") + targetProcessNameList,
            monitorFileName = monitorFileName
        )
    }

    fun executeProotCommand(
        command: List<String>,
        env: HashMap<String, String> = hashMapOf(),
        monitorFileName: String,

    ) {
        val functionName = object{}.javaClass.enclosingMethod?.name
        FileSystems.removeAndCreateDir(
            ubuntuFiles.filesOneRootfsSupportProcDir.absolutePath
        )
        removeProotTempDir()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                setExtraStartupShellForUbuntu(
                    ubuntuFiles
                )
                launchSetupForUbuntu(
                    ubuntuFiles
                )
            }
            withContext(Dispatchers.IO){
                setupForUbuntu(
                    ubuntuFiles
                )
            }
            withContext(Dispatchers.IO) {
                AssetsFileManager.copyFileOrDirFromAssets(
                    context,
                    AssetsFileManager.ubunutSupportCmdDirPath,
                    AssetsFileManager.ubunutSupportCmdDirPath,
                    ubuntuFiles.filesOneRootfsUsrLocalBin.absolutePath,
                    emptyList()
                )
            }
            withContext(Dispatchers.IO) {
                LinuxCmd.execCommand(
                    context,
                    listOf(
                        "chmod",
                        "-R",
                        "777",
                        ubuntuFiles.filesOneRootfsUsrLocalBin.absolutePath
                    ).joinToString("\t")
                )
            }
        }
        when {
            !busyboxWrapper.busyboxIsPresent() -> {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        monitorFileName
                    ).absolutePath,
                    "${className} ${functionName}, no busybox"
                )
                return
            }
            !busyboxWrapper.prootIsPresent() -> {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        monitorFileName
                    ).absolutePath,
                    "${className} ${functionName}, no proot cmd"
                )
                return
            }
            !busyboxWrapper.executionScriptIsPresent() -> {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        monitorFileName
                    ).absolutePath,
                    "${className} ${functionName}, no execution script"
                )
                return
            }
        }
        val updatedCommand = busyboxWrapper.addBusyboxAndProot(command)
        val filesystemDir = File(
            ubuntuFiles.filesOneRootfs.absolutePath
        )
        env.putAll(
            busyboxWrapper.getProotEnv(
                context,
                filesystemDir,
            )
        )

        val processBuilder = ProcessBuilder(updatedCommand)
        processBuilder.directory(ubuntuFiles.filesDir)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()
            streaming(
                process,
                monitorFileName
            )
            val exitCode = process.waitFor()
            outputFailureStatus(
                process,
                exitCode,
                functionName,
                monitorFileName
            )
        } catch (err: Exception) {
            FileSystems.updateFile(
                File(
                    cmdclickMonitorDirPath,
                    monitorFileName
                ).absolutePath,
                "${className} ${functionName} ${err}"
            )
        }
    }

    private fun removeProotTempDir(){
        val prootTempDirPrefix = "proot-"
        val supportDirPath = ubuntuFiles.filesOneRootfsSupportDir.absolutePath
        FileSystems.showDirList(supportDirPath).forEach {
            val secondDirName = it.removePrefix(prootTempDirPrefix)
            val isDigit = secondDirName.getOrNull(0)?.isDigit() == true
            val isProotTempDir =
                it.startsWith(prootTempDirPrefix) && isDigit
            if(!isProotTempDir) return@forEach
            FileSystems.removeDir("${supportDirPath}/$it")
        }
    }

    private fun outputFailureStatus(
        process: Process,
        exitCode: Int,
        functionName: String?,
        monitorFileName: String,
    ){
        if(exitCode == 0) return
        FileSystems.updateFile(
            File(
                cmdclickMonitorDirPath,
                monitorFileName
            ).absolutePath,
            "${className} ${functionName} failure ${process.exitValue()}"
        )
    }

    private fun streaming(
        process: Process,
        monitorName: String
    ){
        val inputStream = process.inputStream
        val reader = inputStream.bufferedReader(Charsets.UTF_8)
        reader.forEachLine { line ->
            if(
                line.trim().isEmpty()
            ) return@forEachLine
            FileSystems.updateFile(
                File(
                    cmdclickMonitorDirPath,
                    monitorName
                ).absolutePath,
                line
            )
        }
        if(process.inputStream != null){
            process.inputStream.close()
        }
        val errStream = process.errorStream
        val errReader = errStream.bufferedReader(Charsets.UTF_8)
        errReader.forEachLine { line ->
            if(
                line.trim().isEmpty()
            ) return@forEachLine
            FileSystems.updateFile(
                File(
                    cmdclickMonitorDirPath,
                    monitorName
                ).absolutePath,
                line
            )
        }
        if(process.errorStream != null){
            process.errorStream.close()
        }
    }

    private fun output(
        process: Process,
        monitorName: String
    ): String {
        val inputStream = process.inputStream
        val reader = inputStream.bufferedReader(Charsets.UTF_8)
        var output = String()
        reader.forEachLine { line ->
            if(
                line.trim().isEmpty()
            ) return@forEachLine
            output += "\n${line}"
        }
        if(process.inputStream != null){
            process.inputStream.close()
        }
        val errStream = process.errorStream
        val errReader = errStream.bufferedReader(Charsets.UTF_8)
        errReader.forEachLine { line ->
            if(
                line.trim().isEmpty()
            ) return@forEachLine
            FileSystems.updateFile(
                File(
                    cmdclickMonitorDirPath,
                    monitorName
                ).absolutePath,
                line
            )
        }
        if(process.errorStream != null){
            process.errorStream.close()
        }
        return output.removePrefix("\n")
    }

    private fun setupForUbuntu(
        ubuntuFiles: UbuntuFiles
    ){
        if(
            !ubuntuFiles.ubuntuSetupCompFile.isFile
        ) return
//        addBinByUrl()
        addEnvToProfile()
    }

    private fun launchSetupForUbuntu(
        ubuntuFiles: UbuntuFiles
    ){
        val mustCmdGrepList = UbuntuBasicProcess.values().map {
            basicCmd ->
            listOf(
                basicCmd.cmd,
                basicCmd.extra,
            ).filter {
                it.isNotEmpty()
            }.map {
                "| grep \"${it}\""
            }.joinToString(" ")
        } + UbuntuExtraSystemShells.makeGrepCon()
        FileSystems.writeFile(
            File(
                ubuntuFiles.filesOneRootfsSupportDir,
                UbuntuFiles.mustProcessGrepCmdsTxt
            ).absolutePath,
            mustCmdGrepList.joinToString("\n")
        )
    }

    private fun setExtraStartupShellForUbuntu(
        ubuntuFiles: UbuntuFiles
    ){
        if(
            ubuntuFiles.ubuntuLaunchCompFile.isFile
        ) return
        val startupShellCon =
           UbuntuExtraSystemShells.makeStartupShellCon()
        FileSystems.writeFile(
            ubuntuFiles.extraStartupShell.absolutePath,
            startupShellCon
        )
    }

    private fun addEnvToProfile(){
        val etcProfilePath = ubuntuFiles.filesOneRootfsEtcProfile.absolutePath
        val profileConList = ReadText(
            ubuntuFiles.filesOneRootfsEtcProfile.absolutePath
        ).textToList()
        val ubuntuIntentMonitorPort = UsePort.UBUNTU_INTENT_MONITOR_PORT.num.toString()
        val exportList = listOf(
            "export APP_ROOT_PATH=\"${UsePath.cmdclickDirPath}\"",
            "export MONITOR_DIR_PATH=\"${UsePath.cmdclickMonitorDirPath}\"",
            "export APP_DIR_PATH=\"${UsePath.cmdclickAppDirPath}\"",
            "export INTENT_MONITOR_PORT=\"${ubuntuIntentMonitorPort}\"",
            "export INTENT_MONITOR_ADDRESS=\"127.0.0.1:${ubuntuIntentMonitorPort}\"",
            "export REPLACE_VARIABLES_TSV_RELATIVE_PATH=\"${UsePath.replaceVariablesTsvRelativePath}\"",
            "export UBUNTU_ENV_TSV_NAME=\"${UbuntuFiles.ubuntuEnvTsvName}\"",
            "export UBUNTU_SERVICE_TEMP_DIR_PATH=\"${UsePath.cmdclickTempUbuntuServiceDirPath}\"",
        )
        val insertExportList = exportList.filter {
            !profileConList.contains(it)
        }
        val updateProfileCon = profileConList + insertExportList
        FileSystems.writeFile(
            etcProfilePath,
            updateProfileCon.joinToString("\n")
        )
    }

    private fun addBinByUrl(){
        val filesUsrLocalBinPath = ubuntuFiles.filesUsrLocalBin.absolutePath

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                FileSystems.createDirs(filesUsrLocalBinPath)
                val binInstallJobList = listOf(
                    "https://github.com/puutaro/httpshd/releases/download/0.0.1/httpshd-0.0.1-arm64",
                    "https://github.com/puutaro/repbash/releases/download/0.0.1/repbash-0.0.1-arm64",
                ).map { url ->
                    async {
                        val byte = CurlManager.get(
                            context,
                            url,
                            String(),
                            String(),
                            30_000
                        )
                        val binName = File(url).name.split("-").first()
                        val binPath = File(filesUsrLocalBinPath, binName).absolutePath
                        FileSystems.writeFromByteArray(
                            binPath,
                            byte,
                        )
                        LogSystems.stdSys(
                            "binPath: ${binPath}"
                        )
                    }
                }
                binInstallJobList.forEach { it.await() }
            }
        }
    }

}

// This class is intended to allow stubbing of elements that are unavailable during unit tests.
class BusyboxWrapper(private val ubuntuFiles: UbuntuFiles) {
    // For basic commands, CWD should be `applicationFilesDir`
    fun wrapCommand(command: String): List<String> {
        return listOf(ubuntuFiles.busybox.absolutePath, "sh", "-c", command)
    }

    fun wrapScript(command: String): List<String> {
        return listOf(ubuntuFiles.busybox.absolutePath, "sh") + command.split(" ")
    }

    fun getBusyboxEnv(): HashMap<String, String> {
        return hashMapOf(
            "LIB_PATH" to ubuntuFiles.supportDir.absolutePath,
            "ROOT_PATH" to ubuntuFiles.filesDir.absolutePath,
            "ROOTFS_PATH" to ubuntuFiles.filesOneRootfs.absolutePath,
            "DOWNLOAD_ROOTFS_TARGZ_PATH" to UbuntuFiles.downloadRootfsTarGzPath,
            "UBUNTU_BACKUP_ROOTFS_PATH" to UbuntuFiles.ubuntuBackupRootfsPath,
            "APP_ROOT_PATH" to UsePath.cmdclickDirPath,
            "b" to ubuntuFiles.busybox.absolutePath

        )
    }

    fun busyboxIsPresent(): Boolean {
        return ubuntuFiles.busybox.exists()
    }

    // Proot scripts expect CWD to be `applicationFilesDir/<filesystem`
    fun addBusyboxAndProot(command: List<String>): List<String> {
        return listOf(ubuntuFiles.busybox.absolutePath, "sh", "support/execInProot.sh") + command
    }

    fun getProotEnv(
        context: Context?,
        rootfsDir: File,
//        prootDebugLevel: String
    ): HashMap<String, String> {
        // TODO This hack should be removed once there are no users on releases 2.5.14 - 2.6.1
        handleHangingBindingDirectories(rootfsDir)
        val emulatedStorageBinding = "-b ${ubuntuFiles.emulatedUserDir.absolutePath}:/storage/internal"
        val externalStorageBinding = ubuntuFiles.sdCardUserDir?.run {
            "-b ${this.absolutePath}:/storage/sdcard"
        } ?: ""
        val bindings = "$emulatedStorageBinding $externalStorageBinding"
        val ubuntuIntentMonitorPort = UsePort.UBUNTU_INTENT_MONITOR_PORT.num.toString()
        return hashMapOf(
            "LD_LIBRARY_PATH" to ubuntuFiles.supportDir.absolutePath,
            "LIB_PATH" to ubuntuFiles.supportDir.absolutePath,
            "ROOT_PATH" to ubuntuFiles.filesDir.absolutePath,
            "ROOTFS_PATH" to rootfsDir.absolutePath,
//                "PROOT_DEBUG_LEVEL" to prootDebugLevel,
            "EXTRA_BINDINGS" to bindings,
            "OS_VERSION" to System.getProperty("os.version")!!,
            "PACKAGE_NAME" to context?.packageName.toString(),
            "UBUNTU_PC_PULSE_SET_SERVER_PORT" to UsePort.UBUNTU_PC_PULSE_SET_SERVER_PORT.num.toString(),
            "UBUNTU_PULSE_RECEIVER_PORT" to UsePort.UBUNTU_PULSE_RECEIVER_PORT.num.toString(),
            "HTTP2_SHELL_PORT" to UsePort.HTTP2_SHELL_PORT.num.toString(),
            "WEB_SSH_TERM_PORT" to UsePort.WEB_SSH_TERM_PORT.num.toString(),
            "DROPBEAR_SSH_PORT" to UsePort.DROPBEAR_SSH_PORT.num.toString(),
            "INTENT_MONITOR_PORT" to ubuntuIntentMonitorPort,
            "INTENT_MONITOR_ADDRESS" to "127.0.0.1:${ubuntuIntentMonitorPort}",
            "CMDCLICK_USER" to UbuntuInfo.user,
            "CREATE_IMAGE_SWITCH" to UbuntuInfo.createImageSwitch,
            "APP_ROOT_PATH" to UsePath.cmdclickDirPath,
            "HTTP2_SHELL_PATH" to "${UsePath.cmdclickTempCmdDirPath}/${UsePath.cmdclickTempCmdShellName}",
            "MONITOR_DIR_PATH" to UsePath.cmdclickMonitorDirPath,
            "APP_DIR_PATH" to UsePath.cmdclickAppDirPath,
            "REPLACE_VARIABLES_TSV_RELATIVE_PATH" to UsePath.replaceVariablesTsvRelativePath,
            "UBUNTU_ENV_TSV_NAME" to UbuntuFiles.ubuntuEnvTsvName,
            "UBUNTU_SERVICE_TEMP_DIR_PATH" to UsePath.cmdclickTempUbuntuServiceDirPath,
        )
    }

    fun prootIsPresent(): Boolean {
        return ubuntuFiles.proot.exists()
    }

    fun executionScriptIsPresent(): Boolean {
        val execInProotFile = File(ubuntuFiles.supportDir, "execInProot.sh")
        return execInProotFile.exists()
    }

    // TODO this hack should be removed when no users are left using version 2.5.14 - 2.6.1
    private fun handleHangingBindingDirectories(filesystemDir: File) {
        // If users upgraded from a version 2.5.14 - 2.6.1, the storage directory will exist but
        // with unusable permissions. It needs to be recreated.
        val storageBindingDir = File(filesystemDir, "storage")
        val storageBindingDirEmpty = storageBindingDir.listFiles()?.isEmpty() ?: true
        if (storageBindingDir.exists() && storageBindingDir.isDirectory && storageBindingDirEmpty) {
            storageBindingDir.delete()
        }
        storageBindingDir.mkdirs()

        // If users upgraded from a version before 2.5.14, the old sdcard binding should be removed
        // to increase clarity.
        val sdCardBindingDir = File(filesystemDir, "sdcard")
        val sdCardBindingDirEmpty = sdCardBindingDir.listFiles()?.isEmpty() ?: true
        if (sdCardBindingDir.exists() && sdCardBindingDir.isDirectory && sdCardBindingDirEmpty) {
            sdCardBindingDir.delete()
        }
    }
}