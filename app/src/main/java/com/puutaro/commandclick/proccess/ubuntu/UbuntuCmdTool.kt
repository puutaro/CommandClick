package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.util.str.QuoteTool

object UbuntuCmdTool {
    fun makeRunBashScript(
        shellPath: String,
        tabSepaArgs: String
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
        return listOf(
            "bash",
            "\"${shellPath}\"",
            args
        ).joinToString(" ")
    }
}