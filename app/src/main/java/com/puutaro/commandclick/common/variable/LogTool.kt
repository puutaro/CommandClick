package com.puutaro.commandclick.common.variable

import TsvImportManager
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
import java.io.File

object LogTool {
    const val logPrefix = "### "
    const val separator = "----------"
    const val errMark = "ERROR"
    const val errRedCode = "#ff0000"

    enum class JsOrActionMark(
        val mark: String
    ){
        NORMAL_JS("[Normal JS]"),
        JS_ACTION("[JsAction]"),
    }

    object DebugMapManager {
        private enum class DebugMapKey(
            val key: String
        ) {
            ERR_EVIDENCE("errEvidence"),
            DEBUG_TOP_BOARD("debugTopBoard"),
            SRC_WITH_PRE_TAG("srcWithPreTag"),
            GENERATED_WITH_DETAIL_TAG("generatedWithDetailTag"),
            JS_CON_WITH_DETAIL_TAG("jsConWithDetailTag"),
        }

        private val debugMapCon = DebugMapKey.values().map {
            it.key to String()
        }.toMap().toMutableMap()

        fun readErrEvidence(): String {
            return debugMapCon.get(DebugMapKey.ERR_EVIDENCE.key)
                ?: String()
        }

        fun readDebugTopBoardCon(): String {
            return debugMapCon.get(DebugMapKey.DEBUG_TOP_BOARD.key)
                ?: String()
        }

        fun readSrcPreTagCon(): String {
            return debugMapCon.get(DebugMapKey.SRC_WITH_PRE_TAG.key)
                ?: String()
        }

        fun readGeneratedDetailTagCon(): String {
            return debugMapCon.get(DebugMapKey.GENERATED_WITH_DETAIL_TAG.key)
                ?: String()
        }

        fun readJsConWithDetailTag(): String {
            return debugMapCon.get(DebugMapKey.JS_CON_WITH_DETAIL_TAG.key)
                ?: String()
        }

        fun writeDebugCons(
            errEvidenceArg: String? = null,
            debugTopBoardConArg: String? = null,
            srcPreTagConArg: String? = null,
            generatedDetailTagConArg: String? = null,
            jsConWithDetailTagArg: String? = null,
        ) {
            errEvidenceArg?.let {
                debugMapCon[DebugMapKey.ERR_EVIDENCE.key] = it
            }
            debugTopBoardConArg?.let {
                debugMapCon[DebugMapKey.DEBUG_TOP_BOARD.key] = it
            }
            srcPreTagConArg?.let {
                debugMapCon[DebugMapKey.SRC_WITH_PRE_TAG.key] = it
            }
            generatedDetailTagConArg?.let {
                debugMapCon[DebugMapKey.GENERATED_WITH_DETAIL_TAG.key] = it
            }
            jsConWithDetailTagArg?.let {
                debugMapCon[DebugMapKey.JS_CON_WITH_DETAIL_TAG.key] = it
            }
        }
    }

    object FirstJsActionLogSaver {
        private enum class DisplayGenre(
            val genre: String,
            val order: Int,
        ) {
            TYPE(JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key, 0),
            JS_CON(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key, 1),
            GENERATED("Generated", 2),
            SRC("Src", 3),
        }

        fun save(
            jsAcKeyToSubKeyCon: String?,
            displayActionImportedAcCon: String,
            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
            keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
            jsActionMapToJsConOnlyReplace: Pair<Map<String, String>, String?>?
        ) {
            val logJsActionMap =
                LogJsActionMapMaker.makeLogJsActionMap(
                    jsActionMapToJsConOnlyReplace
                )
            val typeLogConWithTag = makeTypeWithTag(logJsActionMap)
            val jsConLogConWithTag = makeJsConWithTag(logJsActionMap)

            val generatedDetailTagCon = generatedWithTagCon(
                keyToSubKeyMapListWithoutAfterSubKey,
                keyToSubKeyMapListWithAfterSubKey,
            ).let {
                LogVisualManager.putColorJsActionImportConSubKey(it)
            }
            val srcPreTagCon =
                makeSrcConWithTag(
                    displayActionImportedAcCon,
                ).let {
                    LogVisualManager.putColorJsActionImportConSubKey(it)
                }
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
            val debugTopBoardConArg = listOf(
                "${JsOrActionMark.JS_ACTION.mark}\n",
                typeLogConWithTag,
            ).joinToString("\n")
            DebugMapManager.writeDebugCons(
                errEvidenceArg = String(),
                debugTopBoardConArg = debugTopBoardConArg,
                srcPreTagConArg = srcPreTagCon,
                generatedDetailTagConArg = generatedDetailTagCon,
                jsConWithDetailTagArg = jsConLogConWithTag ?: String(),
            )
        }

        private fun makeTypeWithTag(
            logJsActionMap: Map<String, String>?
        ): String? {
            val typeKeyName = JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
            return logJsActionMap?.get(
                typeKeyName
            )?.let {
                val spanTagCon = makeWithTagConForType(
                    JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key,
                    DisplayGenre.TYPE.order,
                    it,
                )
                LogVisualManager.makePreTagHolder(spanTagCon)
            }
        }

        private fun makeWithTagCon(
            keyName: String,
            order: Int,
            body: String,
        ): String {
            val colorStrPair = LogVisualManager.makeColorCode(order)
            val displayGenre =
                keyName.replaceFirstChar { it.uppercase() }
                    .trim()
            val displayGenreCon = body.replace(
                Regex("([ \t]*\n)+"),
                "\n"
            )
            return LogVisualManager.makeSpanTagHolder(
                colorStrPair,
                "${displayGenre}: \n ${displayGenreCon}",
            )
        }

        private fun makeSrcConWithTag(
//            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,?
            displayActionImportedAcConSrc: String,
//            jsAcKeyToSubKeyCon: String?,
        ): String {
//            val displayJsAcSrc =
//                DisplayActionImportedJsAcSrc.make(
//                    actionImportedKeyToSubKeyCon
//                )
//                DisplayJsAcSrc.make(
//                jsAcKeyToSubKeyCon
//            )
            val displayActionImportedAcCon =
                displayActionImportedAcConSrc.split("\n").filter {
                    it.trim().isNotEmpty()
                }.joinToString("\n")
            val colorStrPair = LogVisualManager.makeColorCode(DisplayGenre.SRC.order)
            val srcSpanTagCon = LogVisualManager.makeSpanTagHolder(
                colorStrPair,
                "${DisplayGenre.SRC.genre}:\n ${displayActionImportedAcCon}"
            )
            return srcSpanTagCon
//            return makeDetailOpenTagHolder(srcSpanTagCon)
        }

        private fun generatedWithTagCon(
            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
            keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
        ): String {
            val displayJsAcGeneratedCon = DisplayJsAcGenerate.make(
                keyToSubKeyMapListWithoutAfterSubKey,
                keyToSubKeyMapListWithAfterSubKey,
            )
            val colorStrPairForGenerated = LogVisualManager.makeColorCode(DisplayGenre.GENERATED.order)
            val generatedSpanTagCon =
                LogVisualManager.makeSpanTagHolder(
                    colorStrPairForGenerated,
                    "${DisplayGenre.GENERATED.genre}:\n ${displayJsAcGeneratedCon}"
                )
            return generatedSpanTagCon
//            return makeDetailOpenTagHolder(
//                generatedSpanTagCon
//            )
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
                    DisplayGenre.JS_CON.order,
                    it,
                )
                spanTagCon
//            makeDetailTagHolder(spanTagCon)
            }
        }

        private fun makeWithTagConForType(
            keyName: String,
            order: Int,
            body: String,
        ): String {
            val colorStrPair = LogVisualManager.makeColorCode(order)
            val displayGenre =
                keyName.replaceFirstChar { it.uppercase() }
                    .trim()
            return LogVisualManager.makeSpanTagHolder(
                colorStrPair,
                "${displayGenre}: ${body}",
            )
        }

        private fun makeDetailTagHolder(
            spanTagCon: String
        ): String {
            val spanTagConList = spanTagCon.split("\n")
            val displayGenrePreTagCon =
                spanTagConList.firstOrNull()
                    ?: String()
            val bodyPreTagCon =
                when (spanTagConList.size > 0) {
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

        private fun makeDetailOpenTagHolder(
            spanTagCon: String
        ): String {
            val spanTagConList = spanTagCon.split("\n")
            val displayGenrePreTagCon =
                spanTagConList.firstOrNull()
                    ?: String()
            val bodyPreTagCon =
                when (spanTagConList.size > 0) {
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
    }

    object SecondErrLogSaver {
        fun saveErrLogCon(
            errMsg: String,
        ) {

            val debugTopBoardCon = ErrWord.replace(
                DebugMapManager.readDebugTopBoardCon(),
                errMsg,
            )

            val errConWithLabel = "\n[${errMark}]\n\n${errMsg}\n"
            val errEvidenceSrc = LogVisualManager.makeTopPreTagLogTagHolder(
                errRedCode,
                errConWithLabel
            )
            val errEvidence = makeErrEvidence(
                errEvidenceSrc,
                debugTopBoardCon,
            )
            val srcPreTagCon = ErrWord.replace(
                DebugMapManager.readSrcPreTagCon(),
                errMsg,
            )
            val generatedDetailTagCon = ErrWord.replace(
                DebugMapManager.readGeneratedDetailTagCon(),
                errMsg,
            )
            val jsConWithDetailTag = ErrWord.replace(
                DebugMapManager.readJsConWithDetailTag(),
                errMsg,
            )
            DebugMapManager.writeDebugCons(
                errEvidenceArg = errEvidence,
                debugTopBoardConArg = debugTopBoardCon,
                srcPreTagConArg = srcPreTagCon,
                generatedDetailTagConArg = generatedDetailTagCon,
                jsConWithDetailTagArg = jsConWithDetailTag,
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
        }

        private fun makeErrEvidence(
            errEvidenceSrc: String,
            debugTopBoardCon: String,
        ): String {
            val isErrMarkFirst3Line = debugTopBoardCon.contains(errMark)
            return when (isErrMarkFirst3Line) {
                true -> String()
                else -> errEvidenceSrc
            }
        }
    }

    object FinalSaver {

        fun saveForJsAction(){
            saveJsSrcActionDebugReport()
            saveJsGenActionDebugReport()
            saveJsConDebugReport()
        }
        private fun saveJsSrcActionDebugReport() {
            val jsAcDebugCon = listOf(
                DebugMapManager.readSrcPreTagCon(),
            ).joinToString("\n")
            saveDebugReportCon(
                UsePath.jsSrcAcDebugReportPath,
                jsAcDebugCon
            )
        }

        private fun saveJsGenActionDebugReport() {
            val jsAcDebugCon = listOf(
                DebugMapManager.readGeneratedDetailTagCon(),
            ).joinToString("\n")
            saveDebugReportCon(
                UsePath.jsGenAcDebugReportPath,
                jsAcDebugCon
            )
        }

        fun saveJsConDebugReport() {
            val jsDebugCon = listOf(
                DebugMapManager.readJsConWithDetailTag(),
            ).joinToString("\n")
            saveDebugReportCon(
                UsePath.jsDebugReportPath,
                jsDebugCon
            )
        }

        private fun saveDebugReportCon(
            path: String,
            con: String,
        ) {
            val topBoardCon = listOf(
                DebugMapManager.readErrEvidence(),
                DebugMapManager.readDebugTopBoardCon()
            ).joinToString("\n")
            val conWithTopBoard = listOf(
                topBoardCon,
                con,
            ).joinToString("\n")
            val saveCon =
                DetailTagManager.replace(conWithTopBoard)
            FileSystems.writeFile(
                path,
                saveCon,
            )
        }
    }

    object LogVisualManager {

        val logGreenPair = "#086312" to "#04b017"
        private val leadLogGreenPair = "#04b017" to "#077814"
        private val logBlackPair = "#000000" to "#0e6266"
        private val leadLogBlackPair = "#545454" to "#4f574d"
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

        fun makePreTagHolder(
            conSrc: String,
        ): String {
            val preTagHolder = "<pre>%s</pre>"
            return preTagHolder.format(conSrc)
        }

        fun makeSpanTagHolder(
            colorPair: Pair<String, String>,
            conSrc: String,
        ): String {
            return conSrc.split("\n").mapIndexed { index, line ->
                val colorStr = when (
                    index % 2 == 0
                ) {
                    true -> colorPair.first
                    else -> colorPair.second
                }
                execMakeSpanTagHolder(colorStr, line)
            }.joinToString("\n")
        }

        fun execMakeSpanTagHolder(
            color: String,
            con: String,
        ): String {
            val spanTagHolder = "<span style=\"color:%s;\">%s</span>"
            return spanTagHolder.format(color, con)
        }

        fun makeColorCode(
            times: Int,
        ): Pair<String, String> {
            val logGreenCodePair = logGreenPair
            val logBlackCodePair = logBlackPair
            val isEven = times % 2 == 0
            return when (isEven) {
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
            return when (isEven) {
                false -> leadLogGreenCodePair
                else -> leadLogBlackCodePair
            }
        }

        fun putColorJsActionImportConSubKey(
            con: String,
        ): String {
            val deepLikeBlue = "#1f68de"
            val replaceActionImportVirtualSubKeyToColor =
                "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=" to deepLikeBlue
            return con.replace(
                replaceActionImportVirtualSubKeyToColor.first,
                execMakeSpanTagHolder(
                    replaceActionImportVirtualSubKeyToColor.second,
                    replaceActionImportVirtualSubKeyToColor.first,
                )

            )
        }
    }

    object EscapeErrMessage {
        fun howEscapeErrMessage(errMessage: String): Boolean {
            return escapeErrMessageList.any {
                errMessage.contains(it)
            }
        }

        private val escapeErrMessageList = listOf(
            "Java exception was raised during method invocation"
        )
    }

    object DisplayJsAcGenerate {

        fun make(
            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
            keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
        ): String {
            val normalCon = KeyToSubKeyConTool.makeCon(keyToSubKeyMapListWithoutAfterSubKey)
            val afterCon = KeyToSubKeyConTool.makeCon(keyToSubKeyMapListWithAfterSubKey).replace(
                Regex("^"),
                " "
            ).replace(
                Regex("\n"),
                "\n "
            )
            val jsAcConGenerated = listOf(
                "normal -> ",
                normalCon,
                "\n after -> ",
                afterCon
            ).joinToString("\n")
            return jsAcConGenerated
        }
    }

    object DisplayActionImportedJsAcSrc {
        fun make(
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
        ): String {
            if (
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return String()
            val jsAcKeyToSubKeyConWithLogSepa =
                toSepa(actionImportedKeyToSubKeyConList)
            return jsAcKeyToSubKeyConWithLogSepa
        }

        private fun toSepa(
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
        ): String {
            val separatorPairForLog = listOf(
                '?' to "\n ?",
                '&' to "\n  &",
            )
            return actionImportedKeyToSubKeyConList.map {
                keyToSubKeyCon ->
                val mainKey = keyToSubKeyCon.first
                val subKeyCon = keyToSubKeyCon.second

                var displaySubKeyCon = subKeyCon
                separatorPairForLog.forEach {
                    displaySubKeyCon = displaySubKeyCon.replace(
                        it.first.toString(),
                        it.second,
                    )
                }
                listOf(
                    ">|${mainKey}=",
                    displaySubKeyCon
                ).joinToString("\n")
            }.joinToString("\n")
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

        fun makeMap(
            keyToSubKeyMapList: List<Pair<String, Map<String, String>>>?,
        ): List<Map<String, String>> {
            if (
                keyToSubKeyMapList.isNullOrEmpty()
            ) return emptyList()
            return keyToSubKeyMapList.map {
                val subKeyMap = it.second
                subKeyMap
            }
        }

        fun makeEvaluateAcCon(
            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
            keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
//            jsRepValHolderMap: Map<String, String>?,
        ): String {
            val evaluateAcConSrc = makeCon(keyToSubKeyMapListWithAfterSubKey,) +
                    makeCon(keyToSubKeyMapListWithoutAfterSubKey)
            return evaluateAcConSrc
//            return CmdClickMap.replaceHolderForJsAction(
//                evaluateAcConSrc,
//                jsRepValHolderMap
//            )
        }

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
            return subKeyMap.filter {
                it.key.trim().isNotEmpty()
            }.map {
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
//                ).joinToString("\n\n\n")
//            )
            val repConWithRepTagToTagStrToMarkList = makeConWithReplaceTag(
                con,
            )
            val repConWithEndTagStr = repConWithRepTagToTagStrToMarkList.first
            val tagStrToMarkList = repConWithRepTagToTagStrToMarkList.second
            if(
                errWord.isNullOrEmpty()
            ) {
                 val repConWithLogMark = putColorByErrMsg(
                    repConWithEndTagStr,
                    errMessage,
                )
                return replaceWithTagMark(
                    repConWithLogMark,
                    tagStrToMarkList
                )
            }
            val errWordWithRedSpan =
                LogVisualManager.execMakeSpanTagHolder(errRedCode, errWord)
            val repConWithErrWord = repConWithEndTagStr.replace(
                errWord,
                errWordWithRedSpan
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath,"debug.txt").absolutePath,
//                listOf(
//                    "errWord: ${errWord}",
//                    "con: ${con}",
//                    "repConWithTagStr: ${repConWithStartTagStr}",
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

        private fun makeConWithReplaceTag(
            con: String,
        ): Pair<String, List<Pair<String, String>>> {
            val repConAndStartTagStrToMarkList =
                replaceStartTag(con)
            val repConWithStartTagStr =
                repConAndStartTagStrToMarkList.first
            val startTagStrToMarkList =
                repConAndStartTagStrToMarkList.second
            val repConAndEndTagStrToMarkList =
                replaceEndTag(repConWithStartTagStr)
            val repConWithEndTagStr =
                repConAndEndTagStrToMarkList.first
            val endTagStrToMarkList =
                repConAndEndTagStrToMarkList.second
            val tagStrToMarkList =
                startTagStrToMarkList + endTagStrToMarkList
            return repConWithEndTagStr to tagStrToMarkList
        }



        private fun replaceStartTag(
            con: String,
        ): Pair<String, List<Pair<String, String>>> {
            val startTagRegex = Regex("<[^>]*>")
            val startTagRegexResultList = startTagRegex.findAll(con).map {
                it.value
            }
            val startTagTempMarkBase = "CMCCLICK_HTML_START_TAG"
            return replaceTag(
                con,
                startTagRegexResultList,
                startTagTempMarkBase
            )
        }

        private fun replaceEndTag(
            con: String,
        ): Pair<String, List<Pair<String, String>>> {
            val endTagRegex = Regex("<[^>]*>")
            val endTagRegexResultList = endTagRegex.findAll(con).map {
                it.value
            }
            val endTagTempMarkBase = "CMCCLICK_HTML_END_TAG"
            return replaceTag(
                con,
                endTagRegexResultList,
                endTagTempMarkBase
            )
        }

        private fun replaceTag(
            srcCon: String,
            tagRegexResultList: Sequence<String>,
            startOrEndTagTempMarkBase: String,
        ): Pair<String, List<Pair<String, String>>> {
            val tagStrToMarkList = mutableListOf<Pair<String, String>>()
            var markNo = 0
            var repCon = srcCon
            tagRegexResultList.forEach {
                    tagStr ->
                val tagTempMark = "\${${startOrEndTagTempMarkBase}${markNo}}"
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
                Regex("([^ \t\n]+?) is not defined"),
                Regex("([^\n]*) is not a function"),
                Regex("SyntaxError: Missing initializer in (const) declaration"),
                Regex("SyntaxError: Unexpected token '([^\n]*)'"),
                Regex("SyntaxError: Missing ([^\n]*) in template expression"),
                Regex("SyntaxError: missing ([^\n]*) after argument list"),
                Regex("Uncaught SyntaxError: Unexpected identifier '([^\n]*)'"),
                Regex("Cannot read properties of undefined \\(reading '([^\n]*)'\\)")
            )
            val errConFirstLine = errCon.split("\n")
                .firstOrNull()
                ?: String()
            errWordExtractRegexList.forEach {
                regex ->
                try {
                    execExtractErrWord(
                        errConFirstLine,
                        regex,
                    ).let {
                        if (
                            !it.isNullOrEmpty()
                        ) {
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//                                listOf(
//                                    "errCon: ${errCon}",
//                                    "regex: ${regex}",
//                                    "errWord: ${it}",
//                                ).joinToString("\n\n") + "\n----------\n"
//                            )
                            return it.trim()
                        }
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
            val errWordResult = errWordResultSrc.trim()
                .replace(Regex("[\n ]"), "\t")
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
            val putColorConByQuoteOdd = QuoteNumCheck.makePutColorCon(
                srcJsCon,
                errMessage
            )
            val putColorConByMissAfterKeyErr = MissAfterKeyErr.makePutColorCon(
                putColorConByQuoteOdd,
                errMessage,
            )
            val putColorConByNotMatchToUseAfter = NotMatchToUseAfter.makePutColorCon(
                putColorConByMissAfterKeyErr,
                errMessage,
            )
            val putColorConByPathNotFound = PathNotFound.makePutColorCon(
                putColorConByNotMatchToUseAfter,
                errMessage,
            )
            val putColorConByLoopMethodOrArgsNotExist = LoopMethodOrArgsNotExist.makePutColorCon(
                putColorConByPathNotFound,
                errMessage
            )
            val putColorConByPrevNotExist = PrevNotExist.makePutColorCon(
                putColorConByLoopMethodOrArgsNotExist,
                errMessage,
            )
            val putColorConByVarNotInit = VarNotInit.makePutColorCon(
                putColorConByPrevNotExist,
                errMessage,
            )
            val putColorConByIrregularFuncValue = IrregularFuncValue.makePutColorCon(
                putColorConByVarNotInit,
                errMessage,
            )
            val putColorConBySyntaxCheckEnum =
                makePutColorConBySyntaxCheckEnum(
                    srcJsCon,
                    putColorConByIrregularFuncValue,
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
//            val putColorConByMisMatchCollMethodStartAndEnd = MisMatchCollectionMethodStartAndEnd.makePutColorCon(
//                putColorConByOneTimesVarWord,
//                errMessage,
//            )
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
            val oneTimesVarWord = errMessage.replace(
                messageExtractRegex,
                "$1"
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsLogColorCon.txt").absolutePath,
//                listOf(
//                    "onTimesVarWord: ${onTimesVarWord}",
//                    "curPutColorCon: ${curPutColorCon}",
//                ).joinToString("\n")
//            )
            return curPutColorCon.replace(
                Regex("([^a-zA-Z0-9_])${oneTimesVarWord}([^a-zA-Z0-9_])"),
                "$1<span style=\"color:${errRedCode};\">${oneTimesVarWord}</span>$2"
            )
        }
        fun checkJsAsSyntaxForVarNotUse(
            context: Context?,
            jsCon: String?,
        ): Boolean {
            val isOneWord = !checkOneTimesVarName(
                context,
                jsCon,
            ).isNullOrEmpty()
            return isOneWord
        }
        private fun checkOneTimesVarName(
            context: Context?,
            jsCon: String?,
        ): String? {
            if(
                jsCon.isNullOrEmpty()
            ) return null
            val oneVarName = findOneTimesVarName(jsCon)
            val errMessage = varNotUseErrMessage.format(oneVarName)
            if(
                oneVarName.isNullOrEmpty()
            ) return null
            SecondErrLogSaver.saveErrLogCon(
                errMessage,
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

    object PathNotFound {

        private const val errMessagePrefix = "Not found path "
        private const val errMessageTemplate = "${errMessagePrefix}'%s'"
        private val extractPathRegex = Regex("${errMessagePrefix}'(.*)'")
        private const val errCodePrefix =
            "${JsActionKeyManager.PathExistChecker.notFoundCodePrefix}:"
        private val findRegex = Regex("${errCodePrefix} ![^!]+!")
        private val errCodeTemplate = JsActionKeyManager.PathExistChecker.notFoundCodeTemplate
        private val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val notFountPath = errMessage.replace(
                extractPathRegex,
                "$1"
            )
            val errCodeExtractRegex =
                Regex("${errCodePrefix} !${notFountPath}!")
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//                listOf(
//                    "errMessage: ${errMessage}",
//                    "notFountPath: ${notFountPath}",
//                    "curPutColorCon: ${curPutColorCon}"
//                ).joinToString("\n\n")
//
//            )
            return curPutColorCon
                .replace(
                    errCodeExtractRegex,
                    notFountPath,
                )
                .replace(
                    notFountPath,
                    "<span style=\"color:${errRedCode};\">${notFountPath}</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
//            keyToSubKeyCon: String?,
//            jsRepValHolderMap: Map<String, String>?,
        ): Boolean {
            checkJsAcGeneCon(
                context,
                evaluateGeneCon,
            ).let {
                if(it) return true
            }
            checkJsAcSrcCon(
                context,
                actionImportedKeyToSubKeyConList,
//                keyToSubKeyCon,
//                jsRepValHolderMap,
            ).let {
                if(it) return true
            }
            return false
        }
        private fun checkJsAcGeneCon(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            if(
                evaluateGeneCon.isEmpty()
            ) return false
            findAcImportNotFoundPath(evaluateGeneCon).let{
                if(
                    it.isNullOrEmpty()
                ) return@let
                val errMessage = errMessageTemplate.format(it)
                saveFirstLog(
                    context,
                    errMessage,
                )
                return true
            }
            findImportNotExistPathForGene(evaluateGeneCon).let {
                if(
                    it.isNullOrEmpty()
                ) return@let
                val errMessage = errMessageTemplate.format(it)
                saveFirstLog(
                    context,
                    errMessage,
                )
                return true
            }
            return false
        }

        private fun findAcImportNotFoundPath(evaluateAcCon: String): String? {
            return try {
                findRegex.findAll(evaluateAcCon).firstOrNull()
                    ?.value
                    ?.removePrefix(errCodePrefix)
                    ?.trim()
                    ?.trim('!')
                    ?.trim()
            } catch (e: Exception){
                null
            }
        }

        private fun findImportNotExistPathForGene(
            evaluateAcGeneCon: String
        ): String? {
            val importPrefix = "${importPathKey}="
            val findJsImportRegex = Regex(
                "\\?${importPrefix}[^\n|?&]*"
            )
            return findImportNotExistPath(
                evaluateAcGeneCon,
                findJsImportRegex,
                "?${importPrefix}",
            )
        }

        private fun checkJsAcSrcCon(
            context: Context?,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
//            keyToSubKeyCon: String?,
//            jsRepValHolderMap: Map<String, String>?,
        ): Boolean {
            if(
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return false
//            if(
//                keyToSubKeyCon.isNullOrEmpty()
//            ) return false
            val evaluateSrcCon = actionImportedKeyToSubKeyConList.map {
                val mainKey = it.first
                val subKeyCon = it.second
                "|${mainKey}=${subKeyCon}"
            }.joinToString("\n")
//            val evaluateSrcCon =
//                CmdClickMap.replaceHolderForJsAction(
//                    actionImportedKeyToSubKeyCon,
//                    jsRepValHolderMap,
//                )
            if(
                evaluateSrcCon.isEmpty()
            ) return false
            findTsvImportNotExistPath(evaluateSrcCon).let{
                if(
                    it.isNullOrEmpty()
                ) return@let
                val errMessage = errMessageTemplate.format(it)
                saveFirstLog(
                    context,
                    errMessage,
                )
                return true
            }
            findJsImportNotExistPath(evaluateSrcCon).let {
                if(
                    it.isNullOrEmpty()
                ) return@let
                val errMessage = errMessageTemplate.format(it)
                saveFirstLog(
                    context,
                    errMessage,
                )
                return true
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "notfound.txt").absolutePath,
//                listOf(
//                    "evaluateSrcCon: ${evaluateSrcCon}",
////                    "errPath: ${errPath}",
////                    "isNotErr: ${isNotErr}",
//                ).joinToString("\n\n")
//            )
            return false
        }

        private fun findJsImportNotExistPath(
            evaluateAcGeneCon: String
        ): String? {
            val jsImportKeyPrefix = "${JsActionKeyManager.JsActionsKey.JS_IMPORT.key}="
            val findTsvImportRegex = Regex(
                "${jsImportKeyPrefix}[^\n|?&]*"
            )
            return findImportNotExistPath(
                evaluateAcGeneCon,
                findTsvImportRegex,
                jsImportKeyPrefix
            )
        }

        private fun findTsvImportNotExistPath(
            evaluateAcCon: String
        ): String? {
            val tsvImportKeyPrefix = "${JsActionKeyManager.JsActionsKey.TSV_IMPORT.key}="
            val findTsvImportRegex = Regex(
                "${tsvImportKeyPrefix}[^\n|?&]*"
            )
            return findImportNotExistPath(
                evaluateAcCon,
                findTsvImportRegex,
                tsvImportKeyPrefix,
            )
        }

        private fun findImportNotExistPath(
            evaluateAcCon: String,
            findRegex: Regex,
            removePrefix: String,
        ): String? {
            val matchResult = try {
                findRegex.findAll(
                    evaluateAcCon
                )
            } catch(e: Exception){
                return null
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "notfound_findImportNotExistPath.txt").absolutePath,
//                listOf(
//                    "match: ${matchResult.map { it.value }.joinToString("---")}",
////                    "isNotErr: ${isNotErr}",
//                ).joinToString("\n\n")
//            )
            return matchResult.map {
                val jsImportLine = it.value
                val importPath =
                    QuoteTool.trimBothEdgeQuote(
                        jsImportLine.removePrefix(removePrefix)
                    )
                val isExistPath = File(importPath).isFile
                if(
                    isExistPath
                ) return@map null
                importPath
            }.firstOrNull { !it.isNullOrEmpty() }
        }
    }


    object IrregularFuncValue {

        private const val errMessagePrefix = "Func name is only alphanumeric chars, '_' and '.': "
        private const val errMessageTemplate = "${errMessagePrefix}'%s'"
        private val extractIrregularRegex = Regex("${errMessagePrefix}'(.*)'")
        private val funcKey = JsActionKeyManager.JsSubKey.FUNC.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val irregularFuncValue = errMessage.replace(
                extractIrregularRegex,
                "$1"
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//                listOf(
//                    "errMessage: ${errMessage}",
//                    "notFountPath: ${notFountPath}",
//                    "curPutColorCon: ${curPutColorCon}"
//                ).joinToString("\n\n")
//
//            )
            return curPutColorCon
                .replace(
                    irregularFuncValue,
                    "<span style=\"color:${errRedCode};\">${irregularFuncValue}</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            checkJsAcGeneCon(
                context,
                evaluateGeneCon,
            ).let {
                if(it) return true
            }
            return false
        }
        private fun checkJsAcGeneCon(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            if(
                evaluateGeneCon.isEmpty()
            ) return false
            findIrregularFuncValue(evaluateGeneCon).let {
                if(
                    it.isNullOrEmpty()
                ) return@let
                val errMessage = errMessageTemplate.format(it)
                saveFirstLog(
                    context,
                    errMessage,
                )
                return true
            }
            return false
        }

        private fun findIrregularFuncValue(
            evaluateAcGeneCon: String
        ): String? {
            val funcPrefix = "${funcKey}="
            val findFuncRegex = Regex(
                "\\?${funcPrefix}[^\n|?&]*"
            )
            return execFindIrregularFuncValue(
                evaluateAcGeneCon,
                findFuncRegex,
                "?${funcPrefix}",
            )
        }

        private fun execFindIrregularFuncValue(
            evaluateAcCon: String,
            findRegex: Regex,
            removePrefix: String,
        ): String? {
            val matchResult = try {
                findRegex.findAll(
                    evaluateAcCon
                )
            } catch(e: Exception){
                return null
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "notfound_findImportNotExistPath.txt").absolutePath,
//                listOf(
//                    "match: ${matchResult.map { it.value }.joinToString("---")}",
////                    "isNotErr: ${isNotErr}",
//                ).joinToString("\n\n")
//            )
            val irregularFuncValueRegex = Regex("[^a-zA-Z0-9_.]")
            return matchResult.map {
                val jsImportLine = it.value
                val funcValue =
                    QuoteTool.trimBothEdgeQuote(
                        jsImportLine.removePrefix(removePrefix)
                    )

                val isNotIrregularFuncValue = !irregularFuncValueRegex.containsMatchIn(funcValue)
                if(
                    isNotIrregularFuncValue
                ) return@map null
                funcValue
            }.firstOrNull { !it.isNullOrEmpty() }
        }
    }


    object VarNotInit {

        private const val errMessagePrefix = "Var must be init by value or func key:\n "
        private const val errMessageTemplate = "${errMessagePrefix}'%s'"
        private val extractNotInitVarNameRegex = Regex("${errMessagePrefix}'(.*)'")
        private val varKey = JsActionKeyManager.JsSubKey.VAR.key
        private val varNotInitVirtualKey = JsActionKeyManager.VirtualSubKey.VAR_NOT_INIT.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val notInitVarName = errMessage.replace(
                extractNotInitVarNameRegex,
                "$1"
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsErrNotInit.txt").absolutePath,
//                listOf(
//                    "errMessage: ${errMessage}",
//                    "notInitVarName: ${notInitVarName}"
//                ).joinToString("\n")
//            )
            val varNotInitVirtualKeyValue =
                "?${varNotInitVirtualKey}=${notInitVarName}"
            val varDefinitionKeyValue =
                "?${varKey}=${notInitVarName}"
            val varDefinitionStr =
                "var ${notInitVarName} ="
            return curPutColorCon
                .replace(
                    varNotInitVirtualKeyValue,
                    String()
                ).replace(
                    varDefinitionKeyValue,
                    "<span style=\"color:${errRedCode};\">${varDefinitionKeyValue}</span>",
                ).replace(
                    varDefinitionStr,
                    "<span style=\"color:${errRedCode};\">${varDefinitionStr}</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            checkJsAcGeneCon(
                context,
                evaluateGeneCon,
            ).let {
                if(it) return true
            }
            return false
        }
        private fun checkJsAcGeneCon(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            if(
                evaluateGeneCon.isEmpty()
            ) return false
            findVarNotInitVirtualKey(evaluateGeneCon).let {
                if(
                    it.isNullOrEmpty()
                ) return@let
                val errMessage = errMessageTemplate.format(it)
                saveFirstLog(
                    context,
                    errMessage,
                )
                return true
            }
            return false
        }

        private fun findVarNotInitVirtualKey(
            evaluateAcGeneCon: String
        ): String? {
            val varNotInitPrefix = "${varNotInitVirtualKey}="
            val findFuncRegex = Regex(
                "\\?${varNotInitPrefix}[^\n|?&]*"
            )
            return execFindVarNotInitVirtualKey(
                evaluateAcGeneCon,
                findFuncRegex,
                "?${varNotInitPrefix}",
            )
        }

        private fun execFindVarNotInitVirtualKey(
            evaluateAcCon: String,
            findRegex: Regex,
            removePrefix: String,
        ): String? {
            val matchResult = try {
                findRegex.findAll(
                    evaluateAcCon
                )
            } catch(e: Exception){
                return null
            }
            if(
                matchResult.count() == 0
            ) return null
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "notfound_findImportNotExistPath.txt").absolutePath,
//                listOf(
//                    "match: ${matchResult.map { it.value }.joinToString("---")}",
////                    "isNotErr: ${isNotErr}",
//                ).joinToString("\n\n")
//            )
            matchResult.forEach {
                val varNotInitKeyValueLine = it.value
                val notInitVarName =
                    QuoteTool.trimBothEdgeQuote(
                        varNotInitKeyValueLine.removePrefix(removePrefix)
                    )
                return notInitVarName
            }
            return null
        }
    }

    object PrevNotExist {

        private const val prevNotExistErrMessage = "Prev var not exist"
        private val prevPronoun = JsActionKeyManager.prevPronoun
        private val noDefinitionBeforeVarNameByPrev =
            JsActionKeyManager.noDefinitionBeforeVarNameByPrev
        private val varKey = JsActionKeyManager.JsSubKey.VAR.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(prevNotExistErrMessage)
            if (
                isNotErr
            ) return curPutColorCon
            val varDifinitionWithErrSignalOnJsAcRepPair =
                "${varKey}=${noDefinitionBeforeVarNameByPrev}" to
                        "${varKey}=${prevPronoun}"
            val varDifinitionWithErrSignalOnJsRepPair =
                "${varKey} ${noDefinitionBeforeVarNameByPrev} =" to
                        "${varKey} ${prevPronoun} ="
            return curPutColorCon
                .replace(errMessage, String())
                .replace(
                    varDifinitionWithErrSignalOnJsAcRepPair.first,
                    "<span style=\"color:${errRedCode};\">${varDifinitionWithErrSignalOnJsAcRepPair.second}</span>",
                )
                .replace(
                    varDifinitionWithErrSignalOnJsRepPair.first,
                    "<span style=\"color:${errRedCode};\">${varDifinitionWithErrSignalOnJsRepPair.second}</span>",
                )
                .replace(
                    Regex("([^a-zA-Z0-9_])(${prevPronoun})([^a-zA-Z0-9_])"),
                    "$1<span style=\"color:${errRedCode};\">$2</span>$3",
                )
        }

        fun check(
            context: Context?,
            jsCon: String?,
        ): Boolean {
            if(
                jsCon.isNullOrEmpty()
            ) return false
            val isNotPrevNotExist = !jsCon.contains(
                noDefinitionBeforeVarNameByPrev
            )
            if(
                isNotPrevNotExist
            ) return false
            saveFirstLog(
                context,
                prevNotExistErrMessage,
            )
            return true
        }
    }

    object NotMatchToUseAfter {

        private const val notMatchSrcAfterToUseAfterErrMessagePart =
            "Not match srcAfter to useAfter:"
        private const val notMatchSrcAfterToUseAfterErrMessagePrefix =
            "${notMatchSrcAfterToUseAfterErrMessagePart}\n"
        private const val notMatchSrcAfterToUseAfterErrMessageTemplate =
            "${notMatchSrcAfterToUseAfterErrMessagePrefix}%s"
        private val notMatchSrcAfterToUseAfterErrMarkKeyEqual =
            "${JsActionKeyManager.ActionImportManager.ActionImportKey.NOT_MATCH_SRC_AFTER_TO_USE_AFTER.key}="
        private val afterKey = JsActionKeyManager.JsSubKey.AFTER.key
        private val useAfterKey =
            JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key

        private enum class ErrSchema(
            val schema: String
        ){
            SRC_AFTER("srcAfter:"),
            USE_AFTER("useAfter:"),
        }

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(notMatchSrcAfterToUseAfterErrMessagePart)
            if (
                isNotErr
            ) return curPutColorCon
//            val srcAfterToUseAfter = extractSrcAfterAndUseAfterPair(
//                errMessage
//            )
//            val srcAfter = srcAfterToUseAfter.first.trim()
//            val useAfter = srcAfterToUseAfter.second.trim()
            val useAfterAllow = JsActionKeyManager.ActionImportManager.useAfterAllow
            val notMatchToUseAfterErrMarkKeyEqualLineRegex =
                Regex("(${notMatchSrcAfterToUseAfterErrMarkKeyEqual}[^\n<>]+)")
            return curPutColorCon
                .replace(
                    notMatchToUseAfterErrMarkKeyEqualLineRegex,
                    "<span style=\"color:${errRedCode};\">$1</span>",
                )
//                .replace(
//                    Regex("(\\?${afterKey}=[\"` ]*${srcAfter}[\"` ]*)"),
//                    "<span style=\"color:${errRedCode};\">$1</span>",
//                )
                .replace(
                    Regex("(\\?${useAfterKey}=[`\" ]*[a-zA-Z0-9_]+ ${useAfterAllow} [a-zA-Z0-9_]+[`\" ]*[^\n><]*)"),
                    "<span style=\"color:${errRedCode};\">$1</span>",
                ).replace(
                    Regex("(\\?${useAfterKey}=[`\" ]*[a-zA-Z0-9_]+[`\" ]*[^\n><]*)"),
                    "<span style=\"color:${errRedCode};\">$1</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String?,
        ): Boolean {
            if(
                evaluateGeneCon.isNullOrEmpty()
            ) return false
            val isNotPrevNotExist = !evaluateGeneCon.contains(
                notMatchSrcAfterToUseAfterErrMarkKeyEqual
            )
            if(
                isNotPrevNotExist
            ) return false
            val extractErrConRegex =
                Regex("${notMatchSrcAfterToUseAfterErrMarkKeyEqual}[^\n]+")
            val displayErrDetail =
                extractErrConRegex.findAll(evaluateGeneCon).firstOrNull()?.let  {
                val errLine = it.value
                makeErrMsg(errLine)
            } ?: return false
            saveFirstLog(
                context,
                notMatchSrcAfterToUseAfterErrMessageTemplate.format(displayErrDetail),
            )
            return true
        }

        private fun makeErrMsg(
            errLine: String
        ): String {
            val funcConAndErrConList = errLine
                .removePrefix(notMatchSrcAfterToUseAfterErrMarkKeyEqual).split(
                    JsActionKeyManager.ActionImportManager.errConSeparator
                )
            val srcAfter = funcConAndErrConList
                .firstOrNull()
                ?.split(JsActionKeyManager.ActionImportManager.errConSuffix)
                ?.firstOrNull() ?: String()
            val useAfter = funcConAndErrConList.getOrNull(1) ?: String()
            return listOf(
                " ${ErrSchema.SRC_AFTER.schema} ${srcAfter}",
                " ${ErrSchema.USE_AFTER.schema} ${useAfter}",
            ).joinToString("\n")
        }

        private fun extractSrcAfterAndUseAfterPair(
            errMsg: String
        ): Pair<String, String> {
            val errConLines =
                errMsg.removePrefix(
                    notMatchSrcAfterToUseAfterErrMessagePrefix
                ).split("\n")
            val srcAfterSchema = ErrSchema.SRC_AFTER.schema
            val srcAfterId = errConLines.firstOrNull {
                it.trim().startsWith(srcAfterSchema)
            }?.trim()?.removePrefix(srcAfterSchema)?.trim() ?: String()
            val useAfterSchema = ErrSchema.USE_AFTER.schema
            val useAfterId = errConLines.firstOrNull {
                it.trim().startsWith(useAfterSchema)
            }?.trim()?.removePrefix(useAfterSchema)?.trim() ?: String()
            return srcAfterId to useAfterId
        }
    }

    object MissAfterKeyErr {

        private val missAfterKeyErrMarkKeyEqual =
            "${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_AFTER_KEY.key}="
        private val actionImportKey = JsActionKeyManager.JsActionsKey.ACTION_IMPORT.key
        private val tsvImportKey = JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
        private val jsImportKey = JsActionKeyManager.JsActionsKey.JS_IMPORT.key
        private val afterKey = JsActionKeyManager.JsSubKey.AFTER.key
        private val missAfterKeyErrMessagePrefix =
            "Miss '${afterKey}' key in ${actionImportKey} file\n" +
                    " bellow section without '${missAfterKeyErrMarkKeyEqual}'" +
                    " exclude ${tsvImportKey} and ${jsImportKey}"
        private val useAfterKey =
            JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key


        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.startsWith(missAfterKeyErrMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val missAfterKeyErrMarkKeyEqualLineRegex =
                Regex("(${missAfterKeyErrMarkKeyEqual}[^\n<>]+)")
            return curPutColorCon
                .replace(
                    missAfterKeyErrMarkKeyEqualLineRegex,
                    "<span style=\"color:${errRedCode};\">$1</span>",
                )
//                .replace(
//                    Regex("(\\?${afterKey}=)"),
//                    "<span style=\"color:${errRedCode};\">$1</span>",
//                ).replace(
//                    Regex("(\\?${useAfterKey}=)"),
//                    "<span style=\"color:${errRedCode};\">$1</span>",
//                )
        }

        fun check(
            context: Context?,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
        ): Boolean {
            if(
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return false
            val actionImportedCon = actionImportedKeyToSubKeyConList.map {
                val mainKey = it.first
                val subKeyCon = it.second
                "|${mainKey}=${subKeyCon}"
            }.joinToString("\n")
            val isNotMissAfterErrMarkKeyEqual = !actionImportedCon.contains(
                missAfterKeyErrMarkKeyEqual
            )
            if(
                isNotMissAfterErrMarkKeyEqual
            ) return false
            saveFirstLog(
                context,
                missAfterKeyErrMessagePrefix
            )
            return true
        }
    }


    object LoopMethodOrArgsNotExist {

        private const val loopMethodOrArgsNotExistMsgPrefix =
            "Loop method definition err:\n"
        private const val loopMethodOrArgsNotExistMsgTemplate =
            "${loopMethodOrArgsNotExistMsgPrefix}%s"
        private const val loopArgsDefinitionErrMarkPrefix =
            JsActionKeyManager.JsFuncManager.loopArgsDefinitionErrMarkPrefix
        private val funcKey =
            JsActionKeyManager.JsSubKey.FUNC.key

        private enum class ErrSchema(
            val schema: String
        ){
            FUNC("${JsActionKeyManager.JsSubKey.FUNC.key}:"),
            DETAIL("detail:"),
        }

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.startsWith(loopMethodOrArgsNotExistMsgPrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val funcNameToDetail = extractFuncAndDetailPair(
                errMessage
            )
            val funcName = funcNameToDetail.first.trim()
            val loopMethod = funcName.split(".").lastOrNull()
                ?: String()
            val itPronoun = JsActionKeyManager.JsVarManager.itPronoun
            val varItFuncDifinition = "${funcKey}=${itPronoun}.${loopMethod}"
            val varFuncDefinition = "${funcKey}=${funcName}"
            return curPutColorCon
                .replace(
                    varFuncDefinition,
                    "<span style=\"color:${errRedCode};\">${varFuncDefinition}</span>",
                ).replace(
                    Regex("(${loopArgsDefinitionErrMarkPrefix}[^\n<>]+)"),
                    "<span style=\"color:${errRedCode};\">$1</span>",
                )
                .replace(
                    Regex("(${varItFuncDifinition}[^\n<>]+)"),
                    "<span style=\"color:${errRedCode};\">$1</span>",
                )
        }

        fun check(
            context: Context?,
            jsCon: String?,
        ): Boolean {
            if(
                jsCon.isNullOrEmpty()
            ) return false
            val isNotPrevNotExist = !jsCon.contains(
                loopArgsDefinitionErrMarkPrefix
            )
            if(
                isNotPrevNotExist
            ) return false
            val extractErrConRegex = Regex("${loopArgsDefinitionErrMarkPrefix}[^\n]+")
            val displayErrDetail = extractErrConRegex.findAll(jsCon).firstOrNull()?.let  {
                val errLine = it.value
                makeErrMsg(errLine)
            } ?: return false
            saveFirstLog(
                context,
                loopMethodOrArgsNotExistMsgTemplate.format(displayErrDetail),
            )
            return true
        }

        private fun makeErrMsg(
            errLine: String
        ): String {
            val funcConAndErrConList = errLine
                .removePrefix(loopArgsDefinitionErrMarkPrefix).split(
                    JsActionKeyManager.JsFuncManager.errConSeparator
                )
            val funcCon = funcConAndErrConList.firstOrNull() ?: String()
            val errCon = funcConAndErrConList.getOrNull(1) ?: String()
            return listOf(
                " ${ErrSchema.FUNC.schema} ${funcCon}",
                " ${ErrSchema.DETAIL.schema} ${errCon}",
            ).joinToString("\n")
        }

        private fun extractFuncAndDetailPair(
            errMsg: String
        ): Pair<String, String> {
            val errConLines =
                errMsg.removePrefix(loopMethodOrArgsNotExistMsgPrefix).split("\n")
            val funcSchema = ErrSchema.FUNC.schema
            val funcName = errConLines.firstOrNull {
                it.trim().startsWith(funcSchema)
            } ?: String()
            val detailSchema = ErrSchema.DETAIL.schema
            val detailName = errConLines.firstOrNull {
                it.trim().startsWith(detailSchema)
            } ?: String()
            return funcName to detailName
        }
    }


//    object MisMatchCollectionMethodStartAndEnd {
//
//        private const val displayErrMessage =
//            "Mismatch collection method start and end return"
////        private const val errMessageTemplate = "${errMessage}'%s'"
////        private val extractIrregularRegex = Regex("${errMessage}'(.*)'")
//        private val collMethodStartKey = JsActionKeyManager.OnlyVarSubKey.COLLECTION_METHOD_START.key
//        private val collMethodEndReturnKey = JsActionKeyManager.OnlyVarSubKey.COLLECTION_METHOD_END_RETURN.key
//
//        fun makePutColorCon(
//            curPutColorCon: String,
//            errMessage: String,
//        ): String {
//            val isNotErr = !errMessage.contains(displayErrMessage)
//            if (
//                isNotErr
//            ) return curPutColorCon
//            val collMethodStartKeyRegex = Regex("\\?(${collMethodStartKey}=[^?|&\n]+)")
//            val collMethodEndReturnKeyRegex = Regex("\\?(${collMethodEndReturnKey}=[^?|&\n]*)")
////            FileSystems.writeFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
////                listOf(
////                    "errMessage: ${errMessage}",
////                    "notFountPath: ${notFountPath}",
////                    "curPutColorCon: ${curPutColorCon}"
////                ).joinToString("\n\n")
////
////            )
//            return curPutColorCon
//                .replace(
//                    collMethodStartKeyRegex,
//                    "<span style=\"color:${errRedCode};\">?$1</span>",
//                )
//                .replace(
//                    collMethodEndReturnKeyRegex,
//                    "<span style=\"color:${errRedCode};\">?$1</span>",
//                )
//        }
//
//        fun check(
//            context: Context?,
//            displayActionImportedAcCon: String,
//        ): Boolean {
//            checkJsAcGeneCon(
//                context,
//                displayActionImportedAcCon,
//            ).let {
//                if(it) return true
//            }
//            return false
//        }
//        private fun checkJsAcGeneCon(
//            context: Context?,
//            displayActionImportedAcCon: String,
//        ): Boolean {
//            if(
//                displayActionImportedAcCon.isEmpty()
//            ) return false
//            val collMethodStartKeyRegex = Regex("\\?${collMethodStartKey}=")
//            val collMethodEndReturnKeyRegex = Regex("\\?${collMethodEndReturnKey}=")
//            val collMethodStartKeyNum = collMethodStartKeyRegex.findAll(displayActionImportedAcCon).count()
//            val collMethodEndReturnKeyNum = collMethodEndReturnKeyRegex.findAll(displayActionImportedAcCon).count()
//            val isNotMismatch =
//                collMethodStartKeyNum == collMethodEndReturnKeyNum
//            if(isNotMismatch) return false
//            saveFirstLog(
//                context,
//                displayErrMessage,
//            )
//           return true
//        }
//
//
//        private fun execFindIrregularFuncValue(
//            evaluateAcCon: String,
//            findRegex: Regex,
//            removePrefix: String,
//        ): String? {
//            val matchResult = try {
//                findRegex.findAll(
//                    evaluateAcCon
//                )
//            } catch(e: Exception){
//                return null
//            }
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "notfound_findImportNotExistPath.txt").absolutePath,
////                listOf(
////                    "match: ${matchResult.map { it.value }.joinToString("---")}",
//////                    "isNotErr: ${isNotErr}",
////                ).joinToString("\n\n")
////            )
//            val irregularFuncValueRegex = Regex("[^a-zA-Z0-9_.]")
//            return matchResult.map {
//                val jsImportLine = it.value
//                val funcValue =
//                    QuoteTool.trimBothEdgeQuote(
//                        jsImportLine.removePrefix(removePrefix)
//                    )
//
//                val isNotIrregularFuncValue = !irregularFuncValueRegex.containsMatchIn(funcValue)
//                if(
//                    isNotIrregularFuncValue
//                ) return@map null
//                funcValue
//            }.firstOrNull { !it.isNullOrEmpty() }
//        }
//    }

    object QuoteNumCheck {

        private val errMessagePrefix = " quote must be even num:\n "
        private val errMessageTemplate = "'%s'${errMessagePrefix}%s"
        private val extractRegexForErrMessage = Regex("'([\"`])'${errMessagePrefix}(.*)")

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val quoteAndSentenceSeparator = "CMDCLICK_QUOTE_AND_SENTENCE_SEPARATOR"
            val quoteAndTargetSentence = errMessage.replace(
                extractRegexForErrMessage,
                "$1${quoteAndSentenceSeparator}$2"
            ).split(quoteAndSentenceSeparator)
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "quoteErr.txt").absolutePath,
//                listOf(
//                    "quoteAndTargetSentence: ${quoteAndTargetSentence}"
//                ).joinToString("\n\n")
//            )
            val targetQuote =
                quoteAndTargetSentence.firstOrNull()
                    ?: return curPutColorCon
            val targetSentence =
                quoteAndTargetSentence.lastOrNull()


//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//                listOf(
//                    "errMessage: ${errMessage}",
//                    "targetQuote: ${targetQuote}",
//                    "curPutColorCon: ${curPutColorCon.replace(
//                        targetQuote,
//                        "<span style=\"color:${errRedCode};\">${targetQuote}</span>",
//                    )}"
//                ).joinToString("\n\n")
//
//            )
            return when(
                targetSentence.isNullOrEmpty()
            ){
                true -> curPutColorCon
                else -> curPutColorCon.replace(
                    targetSentence,
                    "<span style=\"color:${errRedCode};\">${targetSentence}</span>",
                )
            }.replace(
                targetQuote,
                "<span style=\"color:${errRedCode};\">${targetQuote}</span>",
            )
        }

        fun check(
            context: Context?,
            keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
            keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
//            keyToSubKeyMapListWithReplace: List<Pair<String, Map<String, String>>>?,
        ): Boolean {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "qRep.txt").absolutePath,
//                listOf(
//                    "keyToSubKeyMapListWithReplace: ${keyToSubKeyMapListWithReplace}"
//                ).joinToString("\n")
//            )
            checkKeyToSubKeyMapList(
                keyToSubKeyMapListWithoutAfterSubKey
            ).let {
                if(
                    it == null
                ) return@let
                saveFirstLog(
                    context,
                    errMessageTemplate.format(
                        it.first.toString(),
                        it.second
                    )
                )
                return true
            }
            checkKeyToSubKeyMapList(
                keyToSubKeyMapListWithAfterSubKey
            ).let {
                if(
                    it == null
                ) return@let
                saveFirstLog(
                    context,
                    errMessageTemplate.format(
                        it.first.toString(),
                        it.second
                    )
                )
                return true
            }
//            checkKeyToSubKeyMapList(
//                keyToSubKeyMapListWithReplace
//            ).let {
//                if(
//                    it == null
//                ) return@let
//                saveFirstLog(
//                    context,
//                    errMessageTemplate.format(
//                        it.first.toString(),
//                        it.second
//                    )
//                )
//                return true
//            }
            return false
        }

        private fun checkKeyToSubKeyMapList(
            keyToSubKeyMapList: List<Pair<String, Map<String, String>>>?,
        ): Pair<Char, String>? {
            KeyToSubKeyConTool.makeMap(
                keyToSubKeyMapList
            ).forEach {
                howOdd(
                    it.keys
                ).let {
                    if(
                        it == null
                    ) return@let
                    return it
                }
                howOdd(
                    it.values
                ).let {
                    if(
                        it == null
                    ) return@let
                    return it
                }
            }
            return null
        }

        private fun howOdd(
            colls: Collection<String>,
        ): Pair<Char, String>? {
            colls.forEach {
                checkQuoteNum(it).let {
                    if(
                        it == null
                    ) return@let
                    return it
                }
            }
            return null
        }

        private fun checkQuoteNum(
            el: String,
        ): Pair<Char, String>? {
            val backQuote = '`'
            countQuoteNum(
                el,
                backQuote
            ).let {
                if(it) return@let
                return backQuote to el
            }
            val doubleQuote = '"'
            countQuoteNum(
                el,
                doubleQuote
            ).let {
                if(it) return@let
                return doubleQuote to el
            }
            return null
        }

        private fun countQuoteNum(
            con: String,
            quote: Char,
        ): Boolean {
            val countSrcStr = con.replace(
                "\\${quote}",
                String()
            )
            val conByRemoveTargetQuote = countSrcStr.replace(
                quote.toString(),
                String()
            ).length
            val isEven =
                (countSrcStr.length - conByRemoveTargetQuote) % 2 == 0
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "nQuoteNumCount.txt").absolutePath,
//                listOf(
//                    "countSrcStr: ${countSrcStr}",
//                    "countSrcStr.length: ${countSrcStr.length}",
//                    "countSrcStrRep: ${countSrcStr.replace(
//                        quote.toString(),
//                        String()
//                    )}",
//                    "countSrcStr: ${countSrcStr}",
//                    "isEven: ${isEven}",
//                ).joinToString("\n\n")
//            )
            return isEven
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
                "Method name must be alphanumeric characters",
                Regex("`[^\n]*[a-zA-Z0-9_]+?\\.[a-zA-Z0-9_]+?[^a-zA-Z0-9_;()\"`]+?[^\n]*?\\([^)\n]*?\\)[^\n]*`"),
                Regex("[a-zA-Z0-9_]+?\\.[a-zA-Z0-9_]+?[^a-zA-Z0-9_;()\"`]+?[^\n]*?\\([^)\n]*?\\)"),
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
                SecondErrLogSaver.saveErrLogCon(
                    errName,
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
                LogVisualManager.makePreTagHolder(spanTagCon),
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
                LogVisualManager.makePreTagHolder(spanTagCon),
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


    private fun saveFirstLog(
        context: Context?,
        errMessage: String,
    ){
        SecondErrLogSaver.saveErrLogCon(
            errMessage,
        )
        LogSystems.stdErr(
            context,
            errMessage,
            debugNotiJanre = BroadCastIntentExtraForJsDebug.DebugGenre.JS_ERR.type
        )
    }
}
