package com.puutaro.commandclick.util.shell

import android.content.Context
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.util.LogSystems
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset


object LinuxCmd {

    fun isBasicProcess(context: Context?): Boolean {
        val psResult = execCommand(
            context,
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

        return isProotProcess
                && isWsshProcess
                && isHttp2ShellProcess
                && isDropbearProcess
                && isPulseAudioProcess
    }

    fun isProcessCheck(
        context: Context?,
        processName: String
    ): Boolean {
        if(
            processName.isEmpty()
        ) return false
        val psResult = execCommand(
            context,
            listOf(
                "sh",
                "-c",
                "ps -ef | grep -v grep"
            ).joinToString("\t")
        )
        return psResult.contains(processName)

    }

    fun chmod(
        context: Context?,
        dirPath: String
    ) {
        val result = execCommand(
            context,
            listOf(
                "chmod",
                "-R",
                "777",
                dirPath
            ).joinToString("\t")
        )
        LogSystems.stdSys(
            "chmod ${result}"
        )
    }

    fun killFrontProcess(
        context: Context?,
    ){
        val packageName = context?.packageName
            ?: return
        execKillProcess(
            context,
            frontSystemPList(packageName)
        )
    }

    fun killSubFrontProcess(
        context: Context?,
    ){
        val packageName = context?.packageName
            ?: return
        execKillProcess(
            context,
            subFrontSystemPList(
                packageName
            )
        )
    }
    fun killAllProcess(){
        LogSystems.stdSys(
            "allkill"
        )
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun killProcess(
        context: Context?,
    ){
        val packageName =  context?.packageName
            ?: return
        execKillProcess(
            context,
            pListOutputExcludeApp(packageName)
        )
    }

    private fun execKillProcess(
        context: Context?,
        pListOutputCmd: String,
    ){
        val psOutput = execCommand(
            context,
            listOf("sh" , "-c", pListOutputCmd).joinToString("\t")
        )
        LogSystems.stdSys(
            "psOutput ${psOutput}"
        )
        val pListOutput = psOutput.split("\n").map {
            it.split("\t").getOrNull(1) ?: String()
        }.joinToString("  ")
        LogSystems.stdSys(
            "pListOutput ${pListOutput}"
        )
        val killCmd = "kill -9 ${pListOutput}"
        LogSystems.stdSys(
            "killCmd ${killCmd}"
        )
        val killOutput = execCommand(
            context,
            listOf(
                "sh",
                "-c",
                killCmd
            ).joinToString("\t")
        )
        LogSystems.stdSys(
            "kill output ${killOutput}"
        )
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

    fun execCommandTrimNewLIne(
        context: Context?,
        cmdList: String
    ): String {
        return execCommand(
            context,
            cmdList
        ).removePrefix("\n")
            .removeSuffix("\n")
    }

    fun execCommand(
        context: Context?,
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
            LogSystems.stdErr(
                context,
                "### ${this::class.java.name} ${e}"
            )
            return e.toString()
        }
    }
}