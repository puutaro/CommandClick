package com.puutaro.commandclick.common.variable

import android.content.Context
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.LogSystems
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
        val displayJsAcSrc = DisplayJsAcSrc.make(
            jsAcKeyToSubKeyCon
        )
        val colorStrPair = makeColorCode(times)
        val srcPreTag = makePreTagHolder(
            colorStrPair,
            "src: ${displayJsAcSrc}"
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcLog.txt").absolutePath,
//            listOf(
//                "jsActionMap: ${jsActionMap}",
//                "srcLog: ${listOf(
//                    "[JsAction]\n",
//                    jsActionMapLogCon,
//                    srcPreTag,
//                ).joinToString("\n")}"
//            ).joinToString("\n\n") + "\n----------\n"
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

    object DisplayJsAcSrc {
        fun make(
            jsAcKeyToSubKeyCon: String?
        ): String {
            if (
                jsAcKeyToSubKeyCon.isNullOrEmpty()
            ) return String()
            val jsAcKeyToSubKeyConWithLogSepa =
                toSepa(jsAcKeyToSubKeyCon)
            return jsAcKeyToSubKeyConWithLogSepa
        }

        private fun toSepa(
            jsAcKeyToSubKeyCon: String
        ): String {
            val separatorPairForLog = listOf(
                '|' to "\n\n>|",
                '?' to "\n ?",
                '&' to "\n  &",
            )
            var jsAcKeyToSubKeyConForJs = jsAcKeyToSubKeyCon
            separatorPairForLog.forEach {
                jsAcKeyToSubKeyConForJs = jsAcKeyToSubKeyConForJs.replace(
                    it.first.toString(),
                    it.second,
                )
            }
            return jsAcKeyToSubKeyConForJs
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
            ) return putColorByErrMsg(
                con,
                errMessage,
            )
            val errWordWithRedSpan =
                execMakeSpanTagHolder(errRedCode, errWord)
            val tagRegex = Regex("<[^>]*>[^>]*?<[^>]*>")
            val tagRegexResultList = tagRegex.findAll(con).map {
                it.value
            }
            val repConAndTagStrToMarkList = replaceTag(
                con,
                tagRegexResultList
            )
            val repConWithTagStr = repConAndTagStrToMarkList.first
            val repConWithErrWord = repConWithTagStr.replace(
                errWord,
                errWordWithRedSpan
            )
            val tagStrToMarkList = repConAndTagStrToMarkList.second
            return replaceWithTagMark(
                repConWithErrWord,
                tagStrToMarkList
            )
        }

        private fun replaceTag(
            srcCon: String,
            tagRegexResultList: Sequence<String>
        ): Pair<String, List<Pair<String, String>>> {
            val tagStrToMarkList = mutableListOf<Pair<String, String>>()
            val tagTempMarkBase = "CMCCLICK_HTML_TAG"
            var markNo = 0
            var repCon = srcCon
            tagRegexResultList.forEach {
                    tagStr ->
                val tagTempMark = "${tagTempMarkBase}${markNo}"
                val isAlreadyRepTagStr =
                    tagStrToMarkList.map{
                        it.first
                    }.contains(tagStr)
                if(
                    isAlreadyRepTagStr
                ) return@forEach
                repCon = repCon.replace(
                    tagStr,
                    tagTempMark
                )
                tagStrToMarkList.add(tagStr to tagTempMark)
                markNo++
            }
            return repCon to tagStrToMarkList
        }

        private fun replaceWithTagMark(
            repConWithErrWord: String,
            tagStrToMarkList: List<Pair<String, String>>
        ): String {
            var repCon = repConWithErrWord
            tagStrToMarkList.forEach {
                val tagMark = it.second
                val tagStr = it.first
                repCon = repCon.replace(
                    tagMark,
                    tagStr
                )
            }
            return repCon
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
                Regex("Uncaught SyntaxError: Unexpected identifier '(.*)'"),
                Regex("Cannot read properties of undefined \\(reading '(.*)'\\)")
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

        private enum class NoKeyWordJsErrCheck(
            val msg: String,
            val findRegex: Regex,
        ){
            METHOD_NOT_FOUND(
                "Method not found",
                Regex("\\.[a-zA-Z0-9_]+?\\([^)]*?\\)")
//                Regex("[a-zA-Z0-9_]+?\\.[a-zA-Z0-9_]+?\\([^)]*?\\)")
            )
        }

        private fun putColorByErrMsg(
            srcJsCon: String,
            errMessage: String,
        ): String {
            val putColorConBySyntaxCheckEnum =
                makePutColorConBySyntaxCheckEnum(
                    srcJsCon,
                    srcJsCon,
                    errMessage,
                )
            val putColorConByNoKeyWordJsErrCheck =
                makePutColorConByNoKeyWordJsErrCheck(
                    srcJsCon,
                    putColorConBySyntaxCheckEnum,
                    errMessage,
                )
            return putColorConByNoKeyWordJsErrCheck
        }

        private fun makePutColorConBySyntaxCheckEnum(
            srcJsCon: String,
            curPutColorCon: String,
            errMessage: String,
        ): String {
            var putColorCon = curPutColorCon
            SyntaxCheck.CheckEnums.values().forEach {
                val msg = it.msg
                val escapeStrRegex = it.escapeStrRegex
                val findRegex = it.findRegex
                val isNotErr = !errMessage.contains(msg)
                if (
                    isNotErr
                ) return@forEach
                val srcJsConForFind = escapeStrRegex?.let {
                    srcJsCon.replace(
                        escapeStrRegex,
                        String()
                    )
                } ?: srcJsCon
                val findRegexResult = findRegex.findAll(srcJsConForFind)
                findRegexResult.forEach {
                    val hitStr = it.value
                    putColorCon = putColorCon.replace(
                        hitStr,
                        "<span style=\"color:${errRedCode};\">${hitStr}</span>"
                    )
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "regext.txt").absolutePath,
//                        listOf(
//                            "srcJsCon: ${srcJsCon}",
//                            "putColorConForFind: ${putColorConForFind}",
//                            "hitStr: ${hitStr}"
//                        ).joinToString("\n\n")
//                    )
                }
            }
            return putColorCon
        }

        private fun makePutColorConByNoKeyWordJsErrCheck(
            srcJsCon: String,
            curPutColorCon: String,
            errMessage: String,
        ): String {
            var putColorCon = curPutColorCon
            NoKeyWordJsErrCheck.values().forEach {
                val msg = it.msg
                val findRegex = it.findRegex
                val isNotErr = !errMessage.contains(msg)
                if (
                    isNotErr
                ) return@forEach
                val findRegexResult = findRegex.findAll(srcJsCon)
                findRegexResult.forEach {
                    val hitStr = it.value
                    putColorCon = putColorCon.replace(
                        hitStr,
                        "<span style=\"color:${errRedCode};\">${hitStr}</span>"
                    )
                }
            }
            return putColorCon
        }
    }

    object SyntaxCheck {

        enum class CheckEnums(
            val msg: String,
            val escapeStrRegex: Regex?,
            val findRegex: Regex,
        ){
            BRACKET_CHECK(
                "Func must not use in bracket for readable code",
                null,
                Regex("\\$\\{[^{}]?[a-zA-Z0-9_.]+\\([^()]*\\)[^}]*?\\}"),
            ),
            IRREGULAR_METHOD_CHECK(
                "Method name must be half-width alphanumeric characters",
                Regex("`[^\n]*[a-zA-Z0-9_]+?\\.[a-zA-Z0-9_]+?[^a-zA-Z0-9_;()]+?[^\n]*?\\([^)]*?\\)[^\n]*`"),
                Regex("[a-zA-Z0-9_]+?\\.[a-zA-Z0-9_]+?[^a-zA-Z0-9_;()]+?[^\n]*?\\([^)\n]*?\\)"),
            )
        }

        fun checkJsAcSyntax(
            context: Context?,
            jsCon: String?
        ): Boolean {
            if (
                jsCon.isNullOrEmpty()
            ) return false
            if (
                jsCon.replace("\n", String()).trim().isEmpty()
            ) return false
            val noLogJsCon = jsCon.split("\n").filter {
                val isNotCommentOut = !it.trim().startsWith("//")
                isNotCommentOut
            }.joinToString("\n")
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcCheck.txt").absolutePath,
//                listOf(
//                    "jsCon: ${jsCon}",
//                    "noLogJsCon: ${noLogJsCon}",
//                    "jsCon: ${noLogJsCon.replace(
//                        CheckEnums.IRREGULAR_METHOD_CHECK.extractRegex,
//                        "#### $1 ####"
//                    )}"
//                ).joinToString("\n\n")
//            )
            CheckEnums.values().forEach {
                val errName = it.msg
                val escapeStrRegex = it.escapeStrRegex
                val errRegex = it.findRegex
                val escapedNoLogJsCon = escapeStrRegex?.let {
                    noLogJsCon.replace(
                        it,
                        String()
                    )
                } ?: noLogJsCon
                val isNotSyntaxErr = !errRegex.containsMatchIn(escapedNoLogJsCon)
                if (
                    isNotSyntaxErr
                ) return@forEach
                saveErrLogCon(
                    errName,
                    UsePath.jsDebugReportPath,
                )
                LogSystems.stdErr(
                    context,
                    errName,
                    debugNotiJanre = BroadCastIntentExtraForJsDebug.DebugGenre.JS_ERR.type
                )
                return true
            }
            return false

        }
    }

}