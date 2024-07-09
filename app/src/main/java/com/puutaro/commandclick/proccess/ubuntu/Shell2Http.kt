package com.puutaro.commandclick.proccess.ubuntu

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.LogSystems
import java.io.File

object Shell2Http {

    fun runScript(
        context: Context?,
        shellPath: String,
        tabSepaArgs: String,
        timeoutMiliSec: Int,
        monitorFileName: String?,
    ): String {
        if (
            context == null
        ) return String()
        val cmd = UbuntuCmdTool.makeRunBashScript(
            shellPath,
            tabSepaArgs,
            monitorFileName,
        )
        return runCmd(
            context,
            cmd,
            timeoutMiliSec,
        )
    }

    fun runScriptAfterKill(
        context: Context?,
        shellPath: String,
        tabSepaArgs: String,
        timeoutMiliSec: Int,
        monitorFileName: String?,
    ): String {
        if (
            context == null
        ) return String()
        val mainCmd = UbuntuCmdTool.makeRunBashScript(
            shellPath,
            tabSepaArgs,
            monitorFileName
        )
        val killCmd = "bash '/support/killProcTree.sh' '${shellPath}' 1>/dev/null 2>&1"
        val cmd = listOf(
            killCmd,
            mainCmd
        ).joinToString(";")
        return runCmd(
            context,
            cmd,
            timeoutMiliSec,
        )
    }

//    suspend fun runScriptAsProc(
//        context: Context?,
//        executeShellPath:String,
//        tabSepaArgs: String,
//        timeMilisec: Int,
//        resFilePathSrc: String?
//    ): String {
//        val resFilePath = when(
//            resFilePathSrc.isNullOrEmpty()
//        ) {
//            false -> resFilePathSrc
//            else -> File(
//                UsePath.cmdclickTempCmdDirPath,
//                CcPathTool.makeRndSuffixFilePath(
//                    "res.txt"
//                )
//            ).absolutePath
//        }
//        BroadcastSender.normalSend(
//            context,
//            BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START.action,
//            listOf(
//                UbuntuServerIntentExtra.foregroundShellPath.schema to
//                        executeShellPath,
//                UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema to
//                        tabSepaArgs,
//                UbuntuServerIntentExtra.foregroundTimeout.schema to
//                        timeMilisec.toString(),
//                UbuntuServerIntentExtra.foregroundResFilePath.schema to
//                        resFilePath,
//                UbuntuServerIntentExtra.foregroundAsProc.schema to
//                        "on"
//            )
//        )
//        ResAndProcess.wait(
//            context,
//            resFilePath,
//            executeShellPath,
//        )
//        return ReadText(resFilePath).readText()
//    }

    private fun runCmd(
        context: Context?,
        cmd: String,
        timeoutMiliSec: Int,
    ): String {
        if(
            context == null
        ) return String()
        val cmdUrl = "http://127.0.0.1:${UsePort.HTTP2_SHELL_PORT.num}"
        try {
            if (
                !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
            ) {
                ToastUtils.showShort("Launch ubuntu")
                return String()
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "run.txt").absolutePath,
//                listOf(
//                    "cmdUrl: ${cmdUrl}",
//                    "cmd: ${cmd}",
//                ).joinToString("\n")
//            )
            val shellOutput = CurlManager.post(
                context,
                cmdUrl,
                String(),
                cmd,
                timeoutMiliSec,
            ).let {
                CurlManager.convertResToStrByConn(it)
            }
            return shellOutput
        } catch (e: Exception) {
            LogSystems.stdWarn(
                e.toString()
            )
            return String()
        }
    }

    fun runCmd2(
        context: Context?,
        executeShellPath:String,
        tabSepaArgs: String = String(),
        timeoutMiliSec: Int,
    ): String {
        val cmdUrl = "http://127.0.0.1:${UsePort.HTTP2_SHELL_PORT.num}/bash"
        try {
            val shellCon = """
                #!/bin/bash
                
                exec bash "${executeShellPath}" ${tabSepaArgs}
            """.trimIndent()
            FileSystems.writeFile(
                File(
                    UsePath.cmdclickTempCmdDirPath,
                    UsePath.cmdclickTempCmdShellName
                ).absolutePath,
                shellCon
            )
            val shellOutput = CurlManager.get(
                context,
                cmdUrl,
                String(),
                String(),
                timeoutMiliSec,
            ).let {
                CurlManager.convertResToStrByConn(it)
            }
            return shellOutput
        } catch (e: Exception) {
            LogSystems.stdWarn(
                e.toString()
            )
            return String()
        }
    }
}