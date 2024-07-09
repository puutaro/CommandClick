package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object UbuntuCmdTool {
    fun makeRunBashScript(
        shellPath: String,
        tabSepaArgs: String,
        monitorFileName: String?,
    ): String {
        val args =
            tabSepaArgs.replace(Regex("[\t]+"), "\t")
                .split("\t")
                .map {
                    QuoteTool.compBothQuote(
                        it,
                        "\""
                    )
                }.joinToString("\t")
        val toMonitor = when(monitorFileName.isNullOrEmpty()){
            true -> String()
            false -> " >> ${File(UsePath.cmdclickMonitorDirPath, monitorFileName).absolutePath} 2>&1 "
        }
        return listOf(
            "bash",
            "\"${shellPath}\"",
            args,
            toMonitor
        ).joinToString(" ")
    }
}