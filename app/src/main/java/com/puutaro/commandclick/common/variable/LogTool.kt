package com.puutaro.commandclick.common.variable

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object LogTool {
    val logPrefix = "### "
    val separator = "----------"
    val errMark = "ERROR"
    val errRedCode = "#ff0000"
    val logGreenPair = "#086312" to "#04b017"
    val leadLogGreenPair = "#04b017" to "#077814"
    val logBlackPair = "#000000" to "#0e6266"
    val leadLogBlackPair = "#545454" to "#4f574d"


    fun saveErrLogCon(
        errCon: String,
        bodyPath: String,
    ){
        val errConWithLabel = "\n[${errMark}]\n\n${errCon}\n"
        val errEvidenceSrc = makeTopPreTagLogTagHolder(
            errRedCode,
            errConWithLabel
        )
        val errLogCon = execMakeErrorLogCon(
            errCon,
            errEvidenceSrc,
            bodyPath,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "err_revUdate.txt").absolutePath,
//            listOf(
//                "errEvidence: ${errEvidence}",
//                "errCon: ${errCon}",
//                "srd: ${ReadText(bodyPath).readText()}",
//                "saveCon: ${errLogCon}",
//            ).joinToString("\n\n\n")
//        )
        FileSystems.writeFile(
            bodyPath,
            errLogCon
        )
    }

    private fun execMakeErrorLogCon(
        errCon: String,
        errEvidenceSrc: String,
        bodyPath: String,
    ): String {
        val srcConWithRed = ErrWord.replace(
            ReadText(bodyPath).readText(),
            errCon,
        )
        val isErrMarkFirst3Line = srcConWithRed.take(3).contains(errMark)
        val errEvidence = when(isErrMarkFirst3Line){
            true -> String()
            else -> errEvidenceSrc
        }
       return errEvidence + srcConWithRed
    }
    fun makeTopSpanLogTagHolder(
        color: String,
        con: String,
    ): String {
        val spanTagHolder = "<span style=\"color:%s;\">%s</span>"
        return spanTagHolder.format(color, con)
    }

    private fun makeTopPreTagLogTagHolder(
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
        }?.joinToString("\n")
        val displayJsAcSrc = makeDisplayJsAcSrc(
            jsAcKeyToSubKeyCon
        )
        val colorStrPair = makeColorCode(times)
        val srcPreTag = makePreTagHolder(
            colorStrPair,
            "src: ${displayJsAcSrc}"
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
                '?',
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

    object ErrWord{

        fun replace(
            con: String,
            errMessage: String,
        ): String {
            val errWord = errExtractHandler(
                errMessage
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "err_replace.txt").absolutePath,
//                listOf(
//                    "con: ${con}",
//                    "errWord: ${errWord}",
////                    "errWordWithRedSpan: ${errWordWithRedSpan}",
////                    "conRep: ${con.replace(
////                        errWord,
////                        errWordWithRedSpan
////                    )}",
//                ).joinToString("\n\n\n")
//            )
            if(
                errWord.isNullOrEmpty()
            ) return con
            val errWordWithRedSpan =
                execMakeSpanTagHolder(errRedCode, errWord)
            return con.replace(
                errWord,
                errWordWithRedSpan
            )
        }

        private fun errExtractHandler(
            errCon: String
        ): String? {
            val errWordExtractRegexList = listOf(
                Regex("([^ \t]+?) is not defined"),
                Regex("(.*) is not a function"),
                Regex("SyntaxError: Missing initializer in (const) declaration"),
                Regex("SyntaxError: Unexpected token '(.*)'"),
                Regex("SyntaxError: Missing (.*) in template expression"),
                Regex("SyntaxError: missing (.*) after argument list"),
            )
            errWordExtractRegexList.forEach {
                try {
                    execExtractErrWord(
                        errCon,
                        it,
                    ).let {
                        if (
                            !it.isNullOrEmpty()
                        ) return it.trim()
                    }
                } catch(e: Exception){
                    return@forEach
                }
            }
            return null
        }

        private fun execExtractErrWord(
            errCon: String,
            errRegexStr: Regex,
        ): String? {
            val errWordResultSrc = errCon.replace(
                errRegexStr,
                "$1"
            )
            val errWordResult = errWordResultSrc.trim().replace(Regex("[\n ]"), "\t")
                .trim().split("\t").lastOrNull()
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//                listOf(
//                    "errCon: ${errCon}",
//                    "errWordResultSrc: ${errWordResultSrc}",
//                    "errRegexStr: ${errRegexStr}",
//                    "errReSrc: ${errCon.replace(
//                        errRegexStr, "$1")}",
//                    "errWordResult: ${errWordResult}",
//                    "errConRep: ${errWordResult?.replace(Regex("[ \n\t]"), "|AAAA|")}"
//                ).joinToString("\n\n\n")
//            )
            if(
                errWordResult.isNullOrEmpty()
                || errWordResultSrc == errCon.trim()
            ) return null
            return errWordResult
        }
    }

}