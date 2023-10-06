package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import java.time.LocalDateTime

object Shell2Http {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private val currentMonitorFileName = UsePath.cmdClickMonitorFileName_2

    fun runCmd(
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
                UsePath.cmdclickTempCmdDirPath,
                UsePath.cmdclickTempCmdShellName,
                shellCon
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()} ${this::class.java.name}\n curl start"
            )
            val shellOutput = CurlManager.get(
                cmdUrl,
                String(),
                String(),
                timeoutMiliSec,
            )
            if (
                shellOutput.isEmpty()
            ) {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    currentMonitorFileName,
                    "### ${LocalDateTime.now()} ${this::class.java.name}\n no output"
                )
                return String()
            }
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()} ${this::class.java.name}\n ${shellOutput}"
            )
            return shellOutput
        } catch (e: Exception) {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()} ${this::class.java.name}\n${e.toString()}"
            )
            return String()
        }
    }
}