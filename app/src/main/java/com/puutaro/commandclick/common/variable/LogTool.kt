package com.puutaro.commandclick.common.variable

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems

object LogTool {
    val logPrefix = "### "
    val preTagHolder = "<pre style=\"color:%s;\">%s</pre>"
    val spanTagHolder = "<span style=\"color:%s;\">%s</span>"
    val separator = "----------"
    val errMark = "ERROR"
    val errRedCode = "#ff0000"
    val logGreen = "#086312"
    val leadLogGreen = "#04b017"
    val logBlack = "#000000"
    val leadLogBlack = "#545454"

    fun makeColorCode(
        times: Int,
    ): String {
        val logGreenCode = logGreen
        val logBlackCode = logBlack
        val isEven = times % 2 == 0
        return when(isEven){
            false -> logGreenCode
            else -> logBlackCode
        }
    }

    fun makeLeadColorCode(
        times: Int,
    ): String {
        val leadLogGreenCode = leadLogGreen
        val leadLogBlackCode = leadLogBlack
        val isEven = times % 2 == 0
        return when(isEven){
            false -> leadLogGreenCode
            else -> leadLogBlackCode
        }
    }

    fun jsActionLog(
        jsAcKeyToSubKeyCon: String?,
        jsActionMap: Map<String, String>?
    ){
        var times = 0
        val preTagHolder = LogTool.preTagHolder
        val jsActionMapLogCon = jsActionMap?.filterKeys {
            it.isNotEmpty()
        }?.map {
            val colorStr =
                LogTool.makeColorCode(times)
            times++
            preTagHolder.format(
                colorStr,
                "${it.key}: ${it.value}",
            )
        }?.joinToString("\n")
        val srcPreTag = preTagHolder.format(
            LogTool.makeColorCode(times),
            "src: ${jsAcKeyToSubKeyCon}"
        )
        FileSystems.writeFile(
            UsePath.jsDebugReportPath,
            listOf(
                "[JsAction]\n",
                jsActionMapLogCon,
                srcPreTag,
            ).joinToString("\n")
        )
    }
}