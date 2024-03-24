package com.puutaro.commandclick.proccess.ubuntu

import android.content.Context
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.LogSystems
import java.io.File

object Shell2Http {

    fun runCmd(
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