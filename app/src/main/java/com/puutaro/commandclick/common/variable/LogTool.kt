package com.puutaro.commandclick.common.variable

import android.content.Context
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.import.JsImportManager
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.util.LogSystems
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
        errMsg: String,
        bodyPath: String,
    ){
        val errConWithLabel = "\n[${errMark}]\n\n${errMsg}\n"
        val errEvidenceSrc = makeTopPreTagLogTagHolder(
            errRedCode,
            errConWithLabel
        )
        val errLogCon = execMakeErrorLogCon(
            errMsg,
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
        errMsg: String,
        errEvidenceSrc: String,
        bodyPath: String,
    ): String {
        val srcConWithRed = ErrWord.replace(
            ReadText(bodyPath).readText(),
            errMsg,
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
        conSrc: String,
    ): String {
        val preTagHolder = "<pre>%s</pre>"
        return preTagHolder.format(conSrc)
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

    private enum class DisplayGanre(
        val ganre: String,
        val order: Int,
    ){
        TYPE(JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key, 0),
        JS_CON(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key, 1),
        GENERATED("Generated", 2),
        SRC("Src", 3),
    }

    fun jsActionLog(
        jsAcKeyToSubKeyCon: String?,
        keyToSubKeyMapListWithoutAfterSubKey:  List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey:  List<Pair<String, Map<String, String>>>?,
        jsActionMapToJsConOnlyReplace: Pair<Map<String, String>, String?>?
    ){
        val logJsActionMap =
            LogJsActionMapMaker.makeLogJsActionMap(
                jsActionMapToJsConOnlyReplace
            )
        val typeLogConWithTag = makeTypeWithTag(logJsActionMap)
        val jsConLogConWithTag = makeJsConWithTag(logJsActionMap)

        val generatedDetailTagCon = generatedWithTagCon(
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        )
        val srcPreTagCon =
            makeSrcConWithTag(jsAcKeyToSubKeyCon)
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
                typeLogConWithTag,
                srcPreTagCon,
                generatedDetailTagCon,
                jsConLogConWithTag,
            ).joinToString("\n")
        )
    }

    private fun makeTypeWithTag (
        logJsActionMap: Map<String, String>?
    ): String? {
        val typeKeyName = JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
        return logJsActionMap?.get(
            typeKeyName
        )?.let {
            val spanTagCon = makeWithTagConForType(
                JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key,
                DisplayGanre.TYPE.order,
                it,
            )
            makePreTagHolder(spanTagCon)
        }
    }

    private fun makeJsConWithTag(
        logJsActionMap: Map<String, String>?
    ): String? {
        val jsConKeyName = JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
        return logJsActionMap?.get(
            jsConKeyName
        )?.let {
            val spanTagCon = makeWithTagCon(
                jsConKeyName,
                DisplayGanre.JS_CON.order,
                it,
            )
            generateDetailTagHolder(spanTagCon)
        }
    }

    private fun makeWithTagCon(
        keyName: String,
        order: Int,
        body: String,
    ): String {
        val colorStrPair = makeColorCode(order)
        val displayGenre =
            keyName.replaceFirstChar { it.uppercase() }
                .trim()
        val displayGenreCon = body.replace(
            Regex("([ \t]*\n)+"),
            "\n"
        )
        return makeSpanTagHolder(
            colorStrPair,
            "${displayGenre}: \n ${displayGenreCon}",
        )
    }

    private fun makeWithTagConForType(
        keyName: String,
        order: Int,
        body: String,
    ): String {
        val colorStrPair = makeColorCode(order)
        val displayGenre =
            keyName.replaceFirstChar { it.uppercase() }
                .trim()
        return makeSpanTagHolder(
            colorStrPair,
            "${displayGenre}: ${body}",
        )
    }

    private fun generatedWithTagCon(
        keyToSubKeyMapListWithoutAfterSubKey:  List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey:  List<Pair<String, Map<String, String>>>?,
    ): String {
        val displayJsAcGeneratedCon = DisplayJsAcGenerate.make(
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        )
        val colorStrPairForGenerated = makeColorCode(DisplayGanre.GENERATED.order)
        val generatedSpanTagCon =
            makeSpanTagHolder(
                colorStrPairForGenerated,
                "${DisplayGanre.GENERATED.ganre}:\n ${displayJsAcGeneratedCon}"
            )
        return generateDetailTagHolder(
            generatedSpanTagCon
        )
    }
    private fun generateDetailTagHolder(
        spanTagCon: String
    ): String {
        val spanTagConList = spanTagCon.split("\n")
        val displayGenrePreTagCon =
            spanTagConList.firstOrNull()
                ?: String()
        val bodyPreTagCon =
            when(spanTagConList.size > 0) {
                true -> spanTagConList.filterIndexed { index, c ->
                    index > 0
                }.joinToString("\n")
                else -> String()
            }
        return DetailTagManager.putHolder(
            displayGenrePreTagCon,
            bodyPreTagCon
        )
    }

    private fun generateDetailOpenTagHolder(
        spanTagCon: String
    ): String {
        val spanTagConList = spanTagCon.split("\n")
        val displayGenrePreTagCon =
            spanTagConList.firstOrNull()
                ?: String()
        val bodyPreTagCon =
            when(spanTagConList.size > 0) {
                true -> spanTagConList.filterIndexed { index, c ->
                    index > 0
                }.joinToString("\n")
                else -> String()
            }
        return DetailTagManager.putOpenHolder(
            displayGenrePreTagCon,
            bodyPreTagCon
        )
    }

    private fun makeSrcConWithTag(
        jsAcKeyToSubKeyCon: String?,
    ): String {
        val displayJsAcSrc = DisplayJsAcSrc.make(
            jsAcKeyToSubKeyCon
        )
        val colorStrPair = makeColorCode(DisplayGanre.SRC.order)
        val srcSpanTagCon = makeSpanTagHolder(
            colorStrPair,
            "${DisplayGanre.SRC.ganre}:\n ${displayJsAcSrc}"
        )
        return generateDetailOpenTagHolder(srcSpanTagCon)
    }


    object DisplayJsAcGenerate {

        fun make(
            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
            keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
        ): String {
            val deepLikeBlue = "#1f68de"
            val replaceActionImportVirtualSubKeyToColor =
                "?${JsActionKeyManager.actionImportVirtualSubKey}=" to deepLikeBlue
            val normalCon = KeyToSubKeyConTool.makeCon(keyToSubKeyMapListWithoutAfterSubKey)
            val afterCon = KeyToSubKeyConTool.makeCon(keyToSubKeyMapListWithAfterSubKey).replace(
                Regex("^"),
                " "
            ).replace(
                Regex("\n"),
                " "
            )
            val jsAcConGenerated = listOf(
                "normal -> ",
                normalCon,
                " after -> ",
                afterCon
            ).joinToString("\n").replace(
                replaceActionImportVirtualSubKeyToColor.first,
                execMakeSpanTagHolder(
                    replaceActionImportVirtualSubKeyToColor.second,
                    replaceActionImportVirtualSubKeyToColor.first,
                )

            )
            return jsAcConGenerated
        }
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
            return QuoteTool.splitBySurroundedIgnore(
                jsAcKeyToSubKeyConForJs,
                '|'
            ).map {
                ">|${it}"
            }.joinToString("\n")
        }
    }

    object KeyToSubKeyConTool {

//        fun makeMap(
//            keyToSubKeyMapList: List<Pair<String, Map<String, String>>>?,
//        ): List<Map<String, String>> {
//            if (
//                keyToSubKeyMapList.isNullOrEmpty()
//            ) return emptyList()
//            return keyToSubKeyMapList.map {
//                val subKeyMap = it.second
//                subKeyMap
//            }
//        }
        fun makeCon(
            keyToSubKeyMapList: List<Pair<String, Map<String, String>>>?,
        ): String {
            if (
                keyToSubKeyMapList.isNullOrEmpty()
            ) return String()
            return keyToSubKeyMapList.map {
                val mainKeyName = "\n\n>|${it.first}"
                val subKeyMap = it.second
                val formatSubKeyCon = toSepa(subKeyMap)
                listOf(
                    mainKeyName,
                    formatSubKeyCon
                ).joinToString("\n")
            }.joinToString("\n").replace(
                Regex("[\n]+"),
                "\n"
            )
        }

        private fun toSepa(
            subKeyMap: Map<String, String>
        ): String {
            return subKeyMap.map {
                val subKey = " ?${it.key}"
                val subKeyValue = it.value
                listOf(
                    subKey,
                    subKeyValue
                ).joinToString("=")
            }.joinToString("\n")
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath,"debug.txt").absolutePath,
//                listOf(
//                    "con: ${con}",
//                    "repConWithTagStr: ${repConWithTagStr}",
//                    "repConWithErrWord: ${repConWithErrWord}",
//                    "replaceWithTagMark: ${replaceWithTagMark(
//                        repConWithErrWord,
//                        tagStrToMarkList
//                    )}",
//                ).joinToString("\n\n")
//            )
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
                val tagTempMark = "\${${tagTempMarkBase}${markNo}}"
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
            ),
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
            val putColorConByOneTimesVarWord = VarNotUse.makePutColorConByOneTimesVarWord(
                putColorConByNoKeyWordJsErrCheck,
                errMessage,
            )
            return putColorConByOneTimesVarWord
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

    object VarNotUse {

//        private val varName = JsActionKeyManager.JsActionsKey.JS_VAR.key
//        private val matchRegex = Regex("\b${varName}\b[ \t]*=\b[a-zA-Z0-9_]+\b")
        private const val varNotUseErrMessagePrefix = "Not use var "
        private const val varNotUseErrMessage = "${varNotUseErrMessagePrefix}'%s'"
        private val messageExtractRegex = Regex("${varNotUseErrMessagePrefix}'(.*)'")


        fun makePutColorConByOneTimesVarWord(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(varNotUseErrMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val onTimesVarWord = errMessage.replace(
                messageExtractRegex,
                "$1"
            )
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "jsLogColorCon.txt").absolutePath,
                listOf(
                    "onTimesVarWord: ${onTimesVarWord}",
                    "curPutColorCon: ${curPutColorCon}",
                ).joinToString("\n")
            )
            return curPutColorCon.replace(
                Regex("([^a-zA-Z0-9])${onTimesVarWord}([^a-zA-Z0-9])"),
                "$1<span style=\"color:${errRedCode};\">${onTimesVarWord}</span>$2"
            )
        }
        fun checkJsAsSyntaxForVarNotUse(
            context: Context?,
            jsCon: String?,
//            keyToSubKeyMapListWithReplace: List<Pair<String, Map<String, String>>>?,
//            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
//            keyToSubKeyMapListWithAfterSubKey:  List<Pair<String, Map<String, String>>>?,
        ): Boolean {
            val isOneWord = !checkOneTimesVarName(
                context,
                jsCon,
//                keyToSubKeyMapListWithReplace,
//                keyToSubKeyMapListWithoutAfterSubKey,
//                keyToSubKeyMapListWithAfterSubKey,
            ).isNullOrEmpty()
            return isOneWord
        }
        private fun checkOneTimesVarName(
            context: Context?,
            jsCon: String?,
//            keyToSubKeyMapListWithReplace: List<Pair<String, Map<String, String>>>?,
//            keyToSubKeyMapListWithoutAfterSubKey:  List<Pair<String, Map<String, String>>>?,
//            keyToSubKeyMapListWithAfterSubKey:  List<Pair<String, Map<String, String>>>?,
        ): String? {
            if(
                jsCon.isNullOrEmpty()
            ) return null
            val oneVarName = findOneTimesVarName(jsCon)
            val errMessage = varNotUseErrMessage.format(oneVarName)
            if(
                oneVarName.isNullOrEmpty()
            ) return null
            saveErrLogCon(
                errMessage,
                UsePath.jsDebugReportPath,
            )
            LogSystems.stdErr(
                context,
                errMessage,
                debugNotiJanre = BroadCastIntentExtraForJsDebug.DebugGenre.JS_ERR.type
            )

            return varNotUseErrMessage.format(oneVarName)
        }

        private fun findOneTimesVarName(
            jsCon: String,
        ): String? {
            val jsConNoCommentOut = jsCon.split("\n").filter {
                val trimLine = it.trim()
                val isNotCommentOut = !trimLine.startsWith("//")
                val jsFileSystemClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
                    JsFileSystem::class.java.simpleName
                )
                val isNotLog = !trimLine.startsWith("${jsFileSystemClassName}.stdLog")
                isNotCommentOut&& isNotLog
            }.joinToString("\n").let { "|${it}|" }
            val varKeyName = JsActionKeyManager.JsActionsKey.JS_VAR.key
            val varDefinitionRegex = Regex("${varKeyName}[ \t]+[a-zA-Z0-9]+[ \t]*=[^\n]*")
            val oneTimesDefinition = varDefinitionRegex.findAll(jsConNoCommentOut).firstOrNull {
                val varDefinition = it.value.trim()
                val varName = extractVarName(varDefinition)
                val jsConRemoveDefinition = jsConNoCommentOut.replace(
                    Regex("${varKeyName}[ \t]+${varName}[ \t]*=[^\n]*"),
                    String()
                )
                val varNameRegex = Regex("[^a-zA-Z0-9_]${varName}[^a-zA-Z0-9_]")
                val isZero = varNameRegex.findAll(
                    jsConRemoveDefinition
                ).count() == 0
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsAcOnTimes.txt").absolutePath,
//                    listOf(
//                        "varDefinition: ${varDefinition}",
//                        "jsConNoCommentOut: ${jsConNoCommentOut}",
//                        "jsConRemoveDefinition: ${jsConRemoveDefinition}",
//                        "varName: AA${varName}AA",
//                        "cout: ${varNameRegex.findAll(
//                            jsConRemoveDefinition
//                        ).count()}"
//                    ).joinToString("\n\n")
//                )
                isZero
            }?.value ?: return null
            return extractVarName(oneTimesDefinition)
        }

        private fun extractVarName(
            varDefinition: String,
        ): String {
            val varKeyName = JsActionKeyManager.JsActionsKey.JS_VAR.key
            return varDefinition
                .removePrefix(varKeyName)
                .split("=")
                .firstOrNull()
                ?.trim()
                ?: String()

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
            ),
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


    private object LogJsActionMapMaker {

        fun makeLogJsActionMap(
            jsActionMapToJsConOnlyReplace: Pair<Map<String, String>, String?>?
        ): Map<String, String>? {
            val srcJsActionMap = jsActionMapToJsConOnlyReplace?.first
            val jsConWithReplace = jsActionMapToJsConOnlyReplace?.second
            val jsConKey =
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
            val importCon =
                makeImportCon(jsConWithReplace)
            return srcJsActionMap?.map {
                val keyName = it.key
                if(
                    keyName == jsConKey
                ) {
                    val jsCon = it.value
                    return@map keyName to listOf(
                        importCon,
                        jsCon
                    ).joinToString("\n")
                }
                keyName to it.value
            }?.toMap()
        }

        private fun makeImportCon(
            jsConWithReplace: String?,
        ): String {
            val tsvImportRegex = TsvImportManager.tsvImportRegex
            val tsvImportSentences = extractImportCon(
                jsConWithReplace,
                tsvImportRegex,
            )
            val jsImportRegex = Regex(
                "\n[ \t]*${JsImportManager.jsImportPreWord}[^\n]+"
            )
            val jsImportSentences = extractImportCon(
                jsConWithReplace,
                jsImportRegex,
            )
            return listOf(
                tsvImportSentences,
                jsImportSentences
            ).joinToString("\n")
        }
        private fun extractImportCon(
            jsConWithReplace: String?,
            importRegex: Regex,
        ): String {
            if (
                jsConWithReplace.isNullOrEmpty()
            ) return String()

            return jsConWithReplace.let {
                importRegex.findAll(
                    "\n${it}"
                ).map {
                    val tsvImportSentence = it.value.trim()
                    tsvImportSentence
                }.joinToString("\n")
            }
        }
    }

    object DetailTagManager {

        enum class DetailTagToHolder(
            val tag: String,
            val holderMark: String
        ){
            DETAIL_TAG_SATRT(
                "<details>",
                "\${CMDCLICK_DETAIL_TAG_SATRT}"
            ),
            DETAIL_TAG_SATRT_OPEN(
                "<details open>",
                "\${CMDCLICK_DETAIL_TAG_SATRT_OPEN}"
            ),
            DETAIL_TAG_END(
                "</details>",
                "\${CMDCLICK_DETAIL_TAG_END}"
            ),
            DETAIL_SUMMARY_TAG_START(
                "<summary>",
                "\${CMDCLICK_SUMMARY_TAG_START}"
            ),
            DETAIL_SUMMARY_TAG_END(
                "</summary>",
                "\${CMDCLICK_SUMMARY_TAG_END}"
            )
        }
        fun putHolder(
            title: String,
            spanTagCon: String,
        ): String {
            return listOf(
                DetailTagToHolder.DETAIL_TAG_SATRT.holderMark,
                "${DetailTagToHolder.DETAIL_SUMMARY_TAG_START.holderMark}${title}${DetailTagToHolder.DETAIL_SUMMARY_TAG_END.holderMark}",
                makePreTagHolder(spanTagCon),
                DetailTagToHolder.DETAIL_TAG_END.holderMark,
            ).joinToString("\n")
        }

        fun putOpenHolder(
            title: String,
            spanTagCon: String,
        ): String {
            return listOf(
                DetailTagToHolder.DETAIL_TAG_SATRT_OPEN.holderMark,
                "${DetailTagToHolder.DETAIL_SUMMARY_TAG_START.holderMark}${title}${DetailTagToHolder.DETAIL_SUMMARY_TAG_END.holderMark}",
                makePreTagHolder(spanTagCon),
                DetailTagToHolder.DETAIL_TAG_END.holderMark,
            ).joinToString("\n")
        }
        fun replace(
            detailHolderCon: String,
        ): String {
            var replaceDetailTagCon = detailHolderCon
            DetailTagToHolder.values().forEach {
                replaceDetailTagCon = replaceDetailTagCon.replace(
                    it.holderMark,
                    it.tag,
                )
            }
            return replaceDetailTagCon
        }
    }
}