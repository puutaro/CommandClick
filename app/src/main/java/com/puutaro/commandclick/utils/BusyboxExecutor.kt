package com.puutaro.commandclick.utils

import android.util.Log
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

sealed class ExecutionResult
data class MissingExecutionAsset(val asset: String) : ExecutionResult()
object SuccessfulExecution : ExecutionResult()
data class FailedExecution(val reason: String) : ExecutionResult()
data class OngoingExecution(val process: Process) : ExecutionResult()

class BusyboxExecutor(
    private val ulaFiles: UlaFiles,
//    private val prootDebugLogger: ProotDebugLogger,
    private val busyboxWrapper: BusyboxWrapper = BusyboxWrapper(ulaFiles)
) {

    private val discardOutput: (String) -> Any = { Log.d("busybox", it) }

    fun executeScript(
        scriptCall: String,
        listener: (String) -> Any = discardOutput
    ): ExecutionResult {
        val updatedCommand = busyboxWrapper.wrapScript(scriptCall)

        return runCommand(updatedCommand, listener)
    }

    fun executeCommand(
        command: String,
        listener: (String) -> Any = discardOutput
    ): ExecutionResult {
        val updatedCommand = busyboxWrapper.wrapCommand(command)

        return runCommand(updatedCommand, listener)
    }

    private fun runCommand(command: List<String>, listener: (String) -> Any): ExecutionResult {
        if (!busyboxWrapper.busyboxIsPresent()) {
            return MissingExecutionAsset("busybox")
        }

        val env = busyboxWrapper.getBusyboxEnv()
        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(ulaFiles.filesDir)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)

        return try {
            val process = processBuilder.start()
            collectOutput(process.inputStream, listener)
            getProcessResult(process)
        } catch (err: Exception) {
            FailedExecution("$err")
        }
    }


    fun extractRootFs(
//        commandShouldTerminate: Boolean,
        env: HashMap<String, String> = hashMapOf(),
//        listener: (String) -> Any = discardOutput,
//        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): ExecutionResult {
        when {
            !busyboxWrapper.busyboxIsPresent() ->
                return MissingExecutionAsset("busybox")
            !busyboxWrapper.prootIsPresent() ->
                return MissingExecutionAsset("proot")
            !busyboxWrapper.executionScriptIsPresent() ->
                return MissingExecutionAsset("execution script")
        }

//        val prootDebugEnabled = prootDebugLogger.isEnabled
//        val prootDebugLevel =
//                if (prootDebugEnabled) prootDebugLogger.verbosityLevel else "-1"

        val updatedCommand = busyboxWrapper.addBusyboxAndExtractRootfsShell()
        val filesystemDir = File(
            ulaFiles.filesOneRootfs.absolutePath
        )
//            File("${ulaFiles.filesDir.absolutePath}/$filesystemDirName")


        env.putAll(
            busyboxWrapper.getProotEnv(
                filesystemDir,
//                prootDebugLevel
            )
        )

        val processBuilder = ProcessBuilder(updatedCommand)
        processBuilder.directory(ulaFiles.filesDir)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)

        return try {
            val process = processBuilder.start()
            val inputStream = process.inputStream
            val reader = inputStream.bufferedReader(Charsets.UTF_8)
            reader.forEachLine { line ->
                FileSystems.writeFile(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                    "${ReadText(
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_1,
                    ).readText()}\n$line"
                )
            }
            reader.close()
            val errStream = process.errorStream
            val errReader = errStream.bufferedReader(Charsets.UTF_8)
            errReader.forEachLine { line ->
                FileSystems.writeFile(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                    "${ReadText(
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_1,
                    ).readText()}\n$line"
                )
            }
            errReader.close()
            val output = process.waitFor()
            FailedExecution("ok")
        } catch (err: Exception) {
            FailedExecution("$err")
        }
    }

    fun executeProotCommand(
        command: List<String>,
//        filesystemDirName: String,
//        commandShouldTerminate: Boolean,
        env: HashMap<String, String> = hashMapOf(),
        outputType: TerminalOutputType = TerminalOutputType.Streaming
//        listener: (String) -> Any = discardOutput,
//        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): String {
        when {
            !busyboxWrapper.busyboxIsPresent() -> {
                Log.e(this::class.java.name, "no busybox")
                return String()
            }
            !busyboxWrapper.prootIsPresent() -> {
                Log.e(this::class.java.name, "no proot cmd")
                return String()
            }
            !busyboxWrapper.executionScriptIsPresent() -> {
                Log.e(this::class.java.name, "no execution script")
                return String()
            }
        }

//        val prootDebugEnabled = prootDebugLogger.isEnabled
//        val prootDebugLevel =
//                if (prootDebugEnabled) prootDebugLogger.verbosityLevel else "-1"

        val updatedCommand = busyboxWrapper.addBusyboxAndProot(command)
        val filesystemDir = File(
            ulaFiles.filesOneRootfs.absolutePath
        )
//            File("${ulaFiles.filesDir.absolutePath}/$filesystemDirName")


        env.putAll(
            busyboxWrapper.getProotEnv(
                filesystemDir,
//                prootDebugLevel
            )
        )

        val processBuilder = ProcessBuilder(updatedCommand)
        processBuilder.directory(ulaFiles.filesDir)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()

            when(outputType){
                TerminalOutputType.last -> {
                    val pid = process.waitFor()
                    return lastOutput(process)
//                  TODO after implement (change return type?)
                }
                TerminalOutputType.No -> {
                    streaming(
                        process,
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_2
                    )

                }
                TerminalOutputType.Streaming -> {
                    streaming(
                        process,
                        UsePath.cmdclickMonitorDirPath,
                        UsePath.cmdClickMonitorFileName_1
                    )
                }
            }
            val output = process.waitFor()
            return String()

        } catch (err: Exception) {
            Log.e(this::class.java.name, "$err")
            return String()
        }
    }

    suspend fun recursivelyDelete(absolutePath: String): ExecutionResult = withContext(Dispatchers.IO) {
        val command = "rm -rf $absolutePath"
        return@withContext executeCommand(command)
    }

    private fun collectOutput(inputStream: InputStream, listener: (String) -> Any) {
        val buf = inputStream.bufferedReader(Charsets.UTF_8)

        buf.forEachLine { listener(it) }

        buf.close()
    }

    private fun getProcessResult(process: Process): ExecutionResult {
        return if (process.waitFor() == 0) SuccessfulExecution
        else FailedExecution("Command failed with: ${process.exitValue()}")
    }

    private fun streaming(
        process: Process,
        monitorDir: String,
        monitorName: String
    ){
        val inputStream = process.inputStream
        val reader = inputStream.bufferedReader(Charsets.UTF_8)
        reader.forEachLine { line ->
            FileSystems.writeFile(
                UsePath.cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "${ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                ).readText()}\n$line"
            )
        }
        reader.close()
        val errStream = process.errorStream
        val errReader = errStream.bufferedReader(Charsets.UTF_8)
        errReader.forEachLine { line ->
            FileSystems.writeFile(
                UsePath.cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "${ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                ).readText()}\n$line"
            )
        }
        errReader.close()
    }

    private fun lastOutput(
        process: Process
    ): String {
        var errContents = String()
        BufferedReader(
            InputStreamReader(
                process.errorStream,
                Charset.defaultCharset()
            )
        ).use { r ->
            var line: String?
            while (r.readLine().also { line = it } != null) {
                errContents += "\n" + line
//                println(line)
            }
        }
        var outputContents = String()
        BufferedReader(
            InputStreamReader(
                process.inputStream,
                Charset.defaultCharset()
            )
        ).use { r ->
            var line: String?
            while (r.readLine().also { line = it } != null) {
                outputContents += "\n" + line
//                println(line)
            }
        }
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_2,
            ReadText(
                UsePath.cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_2
            ).readText() + "\n" + errContents
        )
        return outputContents
    }
}

// This class is intended to allow stubbing of elements that are unavailable during unit tests.
class BusyboxWrapper(private val ulaFiles: UlaFiles) {
    // For basic commands, CWD should be `applicationFilesDir`
    fun wrapCommand(command: String): List<String> {
        return listOf(ulaFiles.busybox.path, "sh", "-c", command)
    }

    fun wrapScript(command: String): List<String> {
        return listOf(ulaFiles.busybox.path, "sh") + command.split(" ")
    }

    fun getBusyboxEnv(): HashMap<String, String> {
        return hashMapOf(
                "LIB_PATH" to ulaFiles.supportDir.absolutePath,
                "ROOT_PATH" to ulaFiles.filesDir.absolutePath
        )
    }

    fun busyboxIsPresent(): Boolean {
        return ulaFiles.busybox.exists()
    }

    // Proot scripts expect CWD to be `applicationFilesDir/<filesystem`
    fun addBusyboxAndProot(command: List<String>): List<String> {
        return listOf(ulaFiles.busybox.absolutePath, "sh", "support/execInProot.sh") + command
    }

    fun addBusyboxAndExtractRootfsShell(): List<String> {
        return listOf(ulaFiles.busybox.absolutePath, "sh", "support/extractRootfs.sh")
    }

    fun getProotEnv(
        filesystemDir: File,
//        prootDebugLevel: String
    ): HashMap<String, String> {
        // TODO This hack should be removed once there are no users on releases 2.5.14 - 2.6.1
        handleHangingBindingDirectories(filesystemDir)
        val emulatedStorageBinding = "-b ${ulaFiles.emulatedUserDir.absolutePath}:/storage/internal"
        val externalStorageBinding = ulaFiles.sdCardUserDir?.run {
            "-b ${this.absolutePath}:/storage/sdcard"
        } ?: ""
        val bindings = "$emulatedStorageBinding $externalStorageBinding"
        return hashMapOf(
                "LD_LIBRARY_PATH" to ulaFiles.supportDir.absolutePath,
                "LIB_PATH" to ulaFiles.supportDir.absolutePath,
                "ROOT_PATH" to ulaFiles.filesDir.absolutePath,
                "ROOTFS_PATH" to filesystemDir.absolutePath,
//                "PROOT_DEBUG_LEVEL" to prootDebugLevel,
                "EXTRA_BINDINGS" to bindings,
                "OS_VERSION" to System.getProperty("os.version")!!
        )
    }

    fun prootIsPresent(): Boolean {
        return ulaFiles.proot.exists()
    }

    fun executionScriptIsPresent(): Boolean {
        val execInProotFile = File(ulaFiles.supportDir, "execInProot.sh")
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

enum class TerminalOutputType {
    No,
    Streaming,
    last,
}