package com.puutaro.commandclick.util.shell

import android.content.Context
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.proccess.ubuntu.UbuntuExtraSystemShells
import com.puutaro.commandclick.proccess.ubuntu.UbuntuBasicProcess
import com.puutaro.commandclick.util.LogSystems
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset


object LinuxCmd {

    private val objectName = this::class.java.name

    fun isBasicProcess(context: Context?): Boolean {
        val psResult = execCommand(
            context,
            listOf(
                "sh",
                "-c",
                "ps -ef | grep -v grep"
            ).joinToString("\t")
        )
        val ubuntuBasicProcessValues = UbuntuBasicProcess.values()
        val isMustProcess = ubuntuBasicProcessValues.filter {
            mustCmd ->
            val isCmd = when(mustCmd.extra.isEmpty()) {
                true -> psResult.contains(mustCmd.cmd)
                else -> psResult.contains(mustCmd.cmd)
                        && psResult.contains(mustCmd.extra)
            }
            isCmd
        }.size == ubuntuBasicProcessValues.size
        val autoRestoreShellPathList =
            UbuntuExtraSystemShells.OnAutoRestore.makeRestoreProcessPathList()
                .map{
                    val isPath = it.startsWith("/")
                            || it.startsWith("\${")
                    when(isPath){
                        true ->  File(it).name
                        else -> it
                    }
                }
        val isExtraProcess =
            autoRestoreShellPathList.all {
                psResult.contains(it)
            } || autoRestoreShellPathList.isEmpty()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "restore_is_basic.txt").absolutePath,
//            listOf(
//                "isExtraProcess: ${isExtraProcess}",
//                "isEmpty: ${ autoRestoreShellPathList.isEmpty()}",
//                "resotreShellPathList.all: ${autoRestoreShellPathList.all {
//                    psResult.contains(it)
//                }}",
//                "psResult_pulseaudio: ${psResult.split("\n").filter{
//                    it.contains("pulseaudio --start")
//                }}",
//                "psResult_loop: ${psResult.split("\n").filter{
//                    it.contains("ppDir/default/loop.sh")
//                }}",
//                "resotreShellPathList: ${autoRestoreShellPathList}",
//                "resotreShellPathList.size: ${autoRestoreShellPathList.size}",
//            ).joinToString("\n")
//        )


        return isMustProcess
                && isExtraProcess
//                && isPulseAudioProcess
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
//        LogSystems.stdSys(
//            "chmod ${result}"
//        )
    }


    fun killCertainProcess(
        context: Context?,
        processNane: String,
    ){
        val funcName = object{}.javaClass.enclosingMethod?.name
        LogSystems.stdSys(
            sequenceOf(
                "${objectName}.${funcName}",
                processNane
            ).joinToString("\t")
        )
        val packageName = context?.packageName
            ?: return
        execKillProcess(
            context,
            findPList(
                packageName,
                processNane,
            )
        )
    }
    fun killAllProcess(){
        val funcName = object{}.javaClass.enclosingMethod?.name
        LogSystems.stdSys(
            sequenceOf(
                "${objectName}.${funcName}",
            ).joinToString("\t")
        )
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun killProcess(
        context: Context?,
    ){
        val funcName = object{}.javaClass.enclosingMethod?.name
        LogSystems.stdSys(
            sequenceOf(
                "${objectName}.${funcName}",
            ).joinToString("\t")
        )
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
            sequenceOf("sh" , "-c", pListOutputCmd).joinToString("\t")
        )
//        LogSystems.stdSys(
//            "psOutput ${psOutput}"
//        )
        val pListOutput = psOutput.split("\n").map {
            it.split("\t").getOrNull(1) ?: String()
        }.joinToString("  ")
//        LogSystems.stdSys(
//            "pListOutput ${pListOutput}"
//        )
        val killCmd = "kill -9 ${pListOutput}"
//        LogSystems.stdSys(
//            "killCmd ${killCmd}"
//        )
        val killOutput = execCommand(
            context,
            sequenceOf(
                "sh",
                "-c",
                killCmd
            ).joinToString("\t")
        )
//        LogSystems.stdSys(
//            "kill output ${killOutput}"
//        )
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


    private fun findPList(
        packageName: String,
        processName: String
    ): String {
        return if(BuildConfig.DEBUG) {
            "app_pname=\$(ps -ef | grep -i '${packageName}$' | sed 's/ .*//g' ); " +
                    "ps -ef | grep -v '${packageName}$' " +
                    "| grep -e '${processName}' " +
                    "| sed 's/  */\\t/g'"
        } else {
            "ps -ef " +
                    "| grep '${processName}' " +
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

            val errContents = StringBuilder()
            BufferedReader(
                InputStreamReader(
                    process.errorStream,
                    Charset.defaultCharset()
                )
            ).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    errContents.append("\n" + line)
                }
            }
            val outputContents = StringBuilder()
            BufferedReader(
                InputStreamReader(
                    process.inputStream,
                    Charset.defaultCharset()
                )
            ).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    outputContents.append("\n" + line)
                }
            }
            return outputContents.append("\n" + errContents.toString()).toString()
        } catch (e: Exception){
            LogSystems.stdErr(
                context,
                "### ${objectName} ${e}"
            )
            return e.toString()
        }
    }
}