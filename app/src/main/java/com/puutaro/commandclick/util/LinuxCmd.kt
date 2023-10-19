package com.puutaro.commandclick.util

import com.puutaro.commandclick.BuildConfig
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

    fun chmod(
        dirPath: String
    ) {
        val result = execCommand(
            listOf(
                "chmod",
                "-R",
                "777",
                dirPath
            ).joinToString("\t")
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "chmod ${result}"
        )
    }

    fun killFrontProcess(
        packageName: String,
    ){
        execKillProcess(
            frontSystemPList(packageName)
        )
    }

    fun killSubFrontProcess(
        packageName: String,
    ){
        execKillProcess(
            subFrontSystemPList(packageName)
        )
    }
    fun killAllProcess(){
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "allkill"
        )
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    fun killProcess(
        packageName: String,
    ){
        execKillProcess(
            pListOutputExcludeApp(packageName)
        )
    }

    private fun execKillProcess(
        pListOutputCmd: String,
    ){
        val plistcmd00 = "ps -ef"
        val psOutput0 = execCommand(
            listOf("sh" , "-c", plistcmd00).joinToString("\t")
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "psOutput0 ${psOutput0}"
        )
        val psOutput = execCommand(
            listOf("sh" , "-c", pListOutputCmd).joinToString("\t")
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "psOutput ${psOutput}"
        )
        val pListOutput = psOutput.split("\n").map {
            it.split("\t").getOrNull(1) ?: String()
        }.joinToString("  ")
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "pListOutput ${pListOutput}"
        )
        val killCmd = "kill -9 ${pListOutput}"
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "killCmd ${killCmd}"
        )
        val killOutput = execCommand(
            listOf(
                "sh",
                "-c",
                killCmd
            ).joinToString("\t")
        )
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            cmdClickMonitorFileName,
            "kill output ${killOutput}"
        )
    }

    private fun makePListOutput0(
        packageName: String
    ): String {
        return "app_pname=\$(ps -ef | grep '${packageName}' | tail -1 | sed 's/ .*//g' ); " +
                "ps -ef " +
                "| grep '\${app_pname}' "
    }

    private fun pListOutputExcludeApp(
        packageName: String
    ): String {
        return if(BuildConfig.DEBUG){
            "app_pname=\$(ps -ef | grep -E '${packageName}$' | sed 's/ .*//g' );" +
                "ps -ef " +
                "| grep -v '${packageName}$' " +
                "| grep \${app_pname} " +
                "| sed 's/  */\\t/g'"
        } else {
            "ps -ef | grep -v ' ${packageName}' | sed 's/  */\\t/g'"
        }
    }

    private fun frontSystemPList(
        packageName: String
    ): String {
        return if(BuildConfig.DEBUG){
            "app_pname=\$(ps -ef | grep -E '${packageName}$' | sed 's/ .*//g' ); " +
                    "ps -ef | grep -v '${packageName}$' " +
                    "| grep -e 'wssh --address=' -e 'shell2http' " +
                    "| sed 's/  */\\t/g'"
        } else {
            "ps -ef " +
                    "| grep -e 'wssh --address=' -e 'shell2http' " +
                    "| sed 's/  */\\t/g'"
        }
    }

    private fun subFrontSystemPList(
        packageName: String
    ): String {
        return if(BuildConfig.DEBUG) {
            "app_pname=\$(ps -ef | grep -i '${packageName}$' | sed 's/ .*//g' ); " +
                    "ps -ef | grep -v '${packageName}$' " +
                    "| grep -e 'pulseaudio' " +
                    "| sed 's/  */\\t/g'"
        } else {
            "ps -ef " +
                    "| grep 'pulseaudio' " +
                    "| sed 's/  */\\t/g'"
        }
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