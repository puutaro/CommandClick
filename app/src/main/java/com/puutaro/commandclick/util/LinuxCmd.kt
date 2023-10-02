package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.time.LocalDateTime


object LinuxCmd {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private val cmdClickMonitorFileName = UsePath.cmdClickMonitorFileName_2


    fun isBasicProcess(): Boolean {
        val psResult = execCommand(
            listOf(
                "sh",
                "-c",
                "ps -ef | grep -v grep"
            ).joinToString("\t")
        )
        val isProotProcess =
            psResult.contains("proot")
        val isHttp2ShellProcess =
            psResult.contains("shell2http")
                && psResult.contains("${UsePort.HTTP2_SHELL_PORT.num}")
        val isDropbearProcess =
            psResult.contains("dropbear")
                    && psResult.contains("${UsePort.DROPBEAR_SSH_PORT.num}")
        val isWsshProcess =
            psResult.contains("wssh")
                    && psResult.contains("${UsePort.WEB_SSH_TERM_PORT.num}")
        val isPulseAudioProcess =
            psResult.contains("pulseaudio --start")

        FileSystems.writeFile(
            UsePath.cmdclickDefaultAppDirPath,
            "debut.txt",
            "\n${LocalDateTime.now()}\n$psResult"
        )
        return isProotProcess
                && isWsshProcess
                && isHttp2ShellProcess
                && isDropbearProcess
                && isPulseAudioProcess
    }

    fun execCommand(
        cmdList: String
    ): String {
        try {
            val pb = ProcessBuilder().command(
                cmdList.split("\t")
            ).redirectErrorStream(true)
            val process = pb.start()
            val exitCode = process.waitFor()

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
                }
            }
            return outputContents + "\n" + errContents
        } catch (e: Exception){
            FileSystems.writeFile(
                cmdclickMonitorDirPath,
                cmdClickMonitorFileName,
                "### ${this::class.java.name} ${e.toString()}"
            )
            return e.toString()
        }
    }
}