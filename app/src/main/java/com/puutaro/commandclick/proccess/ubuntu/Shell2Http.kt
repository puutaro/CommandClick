package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.LogSystems

object Shell2Http {

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
            val shellOutput = CurlManager.get(
                cmdUrl,
                String(),
                String(),
                timeoutMiliSec,
            ).let {
                CurlManager.convertResToStrByConn(it)
            }
            return shellOutput
        } catch (e: Exception) {
            LogSystems.stdErr(e.toString())
            return String()
        }
    }
}