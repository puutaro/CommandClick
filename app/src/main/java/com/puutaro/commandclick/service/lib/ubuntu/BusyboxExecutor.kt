package com.puutaro.commandclick.service.lib.ubuntu

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.NetworkTool
import kotlinx.coroutines.Dispatchers
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

    private fun runCommand(
        command: List<String>,
        monitorFileName: String
    ) {
        val functionName = object{}.javaClass.enclosingMethod?.name
        if (!busyboxWrapper.busyboxIsPresent()) {
            return FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
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
                cmdclickMonitorDirPath,
                monitorFileName,
                "$err"
            )
        }
    }


    fun extractRootFs(
//        commandShouldTerminate: Boolean,
        env: HashMap<String, String> = hashMapOf(),
        monitorFileName: String
    ) {
        val functionName = object{}.javaClass.enclosingMethod?.name
        when {
            !busyboxWrapper.busyboxIsPresent() -> {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    "${className} ${functionName}, no busybox"
                )
                return
            }
        }
        val updatedCommand = busyboxWrapper.addBusyboxAndExtractRootfsShell()
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

        return try {
            val process = processBuilder.start()
            val inputStream = process.inputStream
            val reader = inputStream.bufferedReader(Charsets.UTF_8)
            reader.forEachLine { line ->
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    line
                )
            }
            reader.close()
            val errStream = process.errorStream
            val errReader = errStream.bufferedReader(Charsets.UTF_8)
            errReader.forEachLine { line ->
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    line
                )
            }
            errReader.close()
            val exitCode = process.waitFor()
            outputFailureStatus(
                process,
                exitCode,
                functionName,
                monitorFileName
            )
        } catch (err: Exception) {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
                "${className} ${functionName} ${err}"
            )
        }
    }

    fun executeKillAllProcess(
        monitorFileName: String
    ){
        val packageName = context?.packageName ?: String()
        executeProotCommand(
            listOf("bash", "-c", "echo kill;kill -9 $(ps aux | grep -v \"$packageName\" | awk '{print $2}' );kill end"),
            monitorFileName = monitorFileName
        )
    }

    fun executeKillProcess(
        targetProcessName: String,
        monitorFileName: String
    ){
        val packageName = context?.packageName ?: String()
        executeProotCommand(
            listOf("bash", "-c", "echo kill;kill -9 $(ps aux | grep '${targetProcessName}' | grep -v \"$packageName\" | awk '{print $2}' );kill end"),
            monitorFileName = monitorFileName
        )
    }

    fun executeProotCommand(
        command: List<String>,
//        filesystemDirName: String,
//        commandShouldTerminate: Boolean,
        env: HashMap<String, String> = hashMapOf(),
        monitorFileName: String,
        updateubuntuCoroutineJobsHashMapUpdateData: UbuntuCoroutineJobsHashMapUpdateData? = null

    ) {
        val functionName = object{}.javaClass.enclosingMethod?.name
        when {
            !busyboxWrapper.busyboxIsPresent() -> {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    "${className} ${functionName}, no busybox"
                )
                return
            }
            !busyboxWrapper.prootIsPresent() -> {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
                    "${className} ${functionName}, no proot cmd"
                )
                return
            }
            !busyboxWrapper.executionScriptIsPresent() -> {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    monitorFileName,
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
            if(
                updateubuntuCoroutineJobsHashMapUpdateData != null
            ) {
                updateubuntuCoroutineJobsHashMapUpdateData.ubuntuCoroutineJobsHashMap[
                        updateubuntuCoroutineJobsHashMapUpdateData.backgroundJobType
                ]?.cancel()
                val processNumUpdateIntent = Intent()
                processNumUpdateIntent.action =
                    BroadCastIntentScheme.UPDATE_PROCESS_NUM_UBUNTU_SERVICE.action
                context?.sendBroadcast(processNumUpdateIntent)
                return
            }
        } catch (err: Exception) {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorFileName,
                "${className} ${functionName} ${err}"
            )
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
            cmdclickMonitorDirPath,
            monitorFileName,
            "${className} ${functionName} failure ${process.exitValue()}"
        )
    }

    suspend fun recursivelyDelete(
        monitorFileName: String,
        absolutePath: String
    ) = withContext(Dispatchers.IO) {
        val command = "rm -rf $absolutePath"
        return@withContext executeCommand(
            command,
            monitorFileName
        )
    }

    private fun streaming(
        process: Process,
        monitorName: String
    ){
        val inputStream = process.inputStream
        val reader = inputStream.bufferedReader(Charsets.UTF_8)
        reader.forEachLine { line ->
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorName,
                line
            )
        }
        reader.close()
        val errStream = process.errorStream
        val errReader = errStream.bufferedReader(Charsets.UTF_8)
        errReader.forEachLine { line ->
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                monitorName,
                line
            )
        }
        errReader.close()
    }
}

// This class is intended to allow stubbing of elements that are unavailable during unit tests.
class BusyboxWrapper(private val ubuntuFiles: UbuntuFiles) {
    // For basic commands, CWD should be `applicationFilesDir`
    fun wrapCommand(command: String): List<String> {
        return listOf(ubuntuFiles.busybox.path, "sh", "-c", command)
    }

    fun wrapScript(command: String): List<String> {
        return listOf(ubuntuFiles.busybox.path, "sh") + command.split(" ")
    }

    fun getBusyboxEnv(): HashMap<String, String> {
        return hashMapOf(
                "LIB_PATH" to ubuntuFiles.supportDir.absolutePath,
                "ROOT_PATH" to ubuntuFiles.filesDir.absolutePath,
                "ROOTFS_PATH" to ubuntuFiles.filesOneRootfs.absolutePath,
        )
    }

    fun busyboxIsPresent(): Boolean {
        return ubuntuFiles.busybox.exists()
    }

    // Proot scripts expect CWD to be `applicationFilesDir/<filesystem`
    fun addBusyboxAndProot(command: List<String>): List<String> {
        return listOf(ubuntuFiles.busybox.absolutePath, "sh", "support/execInProot.sh") + command
    }

    fun addBusyboxAndExtractRootfsShell(): List<String> {
        return listOf(ubuntuFiles.busybox.absolutePath, "sh", "support/extractRootfs.sh")
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
        return hashMapOf(
                "LD_LIBRARY_PATH" to ubuntuFiles.supportDir.absolutePath,
                "LIB_PATH" to ubuntuFiles.supportDir.absolutePath,
                "ROOT_PATH" to ubuntuFiles.filesDir.absolutePath,
                "ROOTFS_PATH" to rootfsDir.absolutePath,
//                "PROOT_DEBUG_LEVEL" to prootDebugLevel,
                "EXTRA_BINDINGS" to bindings,
                "OS_VERSION" to System.getProperty("os.version")!!,
                "IP_V4_ADDRESS" to NetworkTool.getIpv4Address(context)
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