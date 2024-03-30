package com.puutaro.commandclick.common.variable

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems

object LogTool {
    val logPrefix = "### "
    val separator = "----------"
    val errMark = "ERROR"
    val errRedCode = "#ff0000"
    val logGreenPair = "#086312" to "#04b017"
    val leadLogGreenPair = "#04b017" to "#077814"
    val logBlackPair = "#000000" to "#0e6266"
//    val logBluePair = "#0e6266" to "#2e888c"
    val leadLogBlackPair = "#545454" to "#4f574d"

    fun makeTopSpanLogTagHolder(
        color: String,
        con: String,
    ): String {
        val spanTagHolder = "<span style=\"color:%s;\">%s</span>"
        return spanTagHolder.format(color, con)
    }

    fun makeTopPreTagLogTagHolder(
        color: String,
        con: String,
    ): String {
        val preTagHolder = "<pre style=\"color:%s;\">%s</pre>"
        return preTagHolder.format(color, con)
    }
    private fun makePreTagHolder(
        colorPair: Pair<String, String>,
        conSrc: String,
    ): String {
//        val preTagHolder = "<pre style=\"color:%s;\">%s</pre>"
        val preTagHolder = "<pre>%s</pre>"
        val con = conSrc.split("\n").mapIndexed {
            index, line ->
            val colorStr = when(
                index % 2 == 0
            ){
                true -> colorPair.first
                else -> colorPair.second
            }
            execMakeSpanTagHolder(colorStr, line)
        }.joinToString("\n")
        return preTagHolder.format(con)
    }

    fun makeSpanTagHolder(
        colorPair: Pair<String, String>,
        conSrc: String,
    ): String {
//        val preTagHolder = "<pre style=\"color:%s;\">%s</pre>"
        val preTagHolder = "<span>%s</span>"
        return  conSrc.split("\n").mapIndexed {
                index, line ->
            val colorStr = when(
                index % 2 == 0
            ){
                true -> colorPair.first
                else -> colorPair.second
            }
            execMakeSpanTagHolder(colorStr, line)
        }.joinToString("\n")
//        return preTagHolder.format(con)
    }

    private fun execMakeSpanTagHolder(
        color: String,
        con: String,
    ): String {
        val spanTagHolder = "<span style=\"color:%s;\">%s</span>"
        return spanTagHolder.format(color, con)
    }


    private val escapeErrMessageList = listOf(
        "Java exception was raised during method invocation"
    )
    fun howEscapeErrMessage (errMessage: String): Boolean {
        return escapeErrMessageList.any {
            errMessage.contains(it)
        }
    }

    fun makeColorCode(
        times: Int,
    ): Pair<String, String> {
        val logGreenCodePair = logGreenPair
        val logBlackCodePair = logBlackPair
        val isEven = times % 2 == 0
        return when(isEven){
            false -> logGreenCodePair
            else -> logBlackCodePair
        }
    }

    fun makeLeadColorCode(
        times: Int,
    ): Pair<String, String> {
        val leadLogGreenCodePair = leadLogGreenPair
        val leadLogBlackCodePair = leadLogBlackPair
        val isEven = times % 2 == 0
        return when(isEven){
            false -> leadLogGreenCodePair
            else -> leadLogBlackCodePair
        }
    }

    fun jsActionLog(
        jsAcKeyToSubKeyCon: String?,
        jsActionMap: Map<String, String>?
    ){
        var times = 0
//        LogTool.preTagHolder
        val jsActionMapLogCon = jsActionMap?.filterKeys {
            it.isNotEmpty()
        }?.map {
            val colorStrPair =
                makeColorCode(times)
            times++
            makePreTagHolder(
                colorStrPair,
                "${it.key}: ${it.value}",
            )
//            preTagHolder.format(
//                colorStrPair,
//                "${it.key}: ${it.value}",
//            )
        }?.joinToString("\n")
        val displayJsAcSrc = makeDisplayJsAcSrc(
            jsAcKeyToSubKeyCon
        )
        val colorStrPair = makeColorCode(times)
        val srcPreTag = makePreTagHolder(
            colorStrPair,
            "src: ${displayJsAcSrc}"
        )
//            preTagHolder.format(
//            makeColorCode(times),
//            "src: ${displayJsAcSrc}"
//        )
        FileSystems.writeFile(
            UsePath.jsDebugReportPath,
            listOf(
                "[JsAction]\n",
                jsActionMapLogCon,
                srcPreTag,
            ).joinToString("\n")
        )
    }

    private fun makeDisplayJsAcSrc(
        jsActionMapLogCon: String?
    ): String {
        return QuoteTool.replaceBySurroundedIgnore(
            jsActionMapLogCon ?: String(),
            '|',
            "\n\n>|"
        ).let {
            QuoteTool.replaceBySurroundedIgnore(
                it,
                '!',
                "\n !"
            )
        }.let {
            QuoteTool.replaceBySurroundedIgnore(
                it,
                '&',
                "\n  &"
            )
        }
    }
}