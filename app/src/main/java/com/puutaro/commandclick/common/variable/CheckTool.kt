package com.puutaro.commandclick.common.variable

import TsvImportManager
import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.import.JsImportManager
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.RegexTool
import com.puutaro.commandclick.util.str.SpeedReplacer
import java.io.File

object CheckTool {
    const val logPrefix = "### "
    const val separator = "----------"
    const val errMark = "ERROR"
    const val errRedCode = "#ff0000"
    const val errBrown = "#804903"
    const val lightBlue = "#0cadf2"

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
            AC_IMPORT_WITH_OPEN_DETAIL_TAG("acImportWithOpenDetailTag"),
            SRC_WITH_OPEN_DETAIL_TAG("srcWithDetailTag"),
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

        fun readAcImportWithOpenDetailTagCon(): String {
            return debugMapCon.get(DebugMapKey.AC_IMPORT_WITH_OPEN_DETAIL_TAG.key)
                ?: String()
        }

        fun readSrcDetailTagCon(): String {
            return debugMapCon.get(DebugMapKey.SRC_WITH_OPEN_DETAIL_TAG.key)
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
            acImportOpenDetailTagConArg : String? = null,
            srcOpenDetailTagConArg: String? = null,
            generatedDetailTagConArg: String? = null,
            jsConWithDetailTagArg: String? = null,
        ) {
            errEvidenceArg?.let {
                debugMapCon[DebugMapKey.ERR_EVIDENCE.key] = it
            }
            debugTopBoardConArg?.let {
                debugMapCon[DebugMapKey.DEBUG_TOP_BOARD.key] = it
            }
            acImportOpenDetailTagConArg?.let {
                debugMapCon[DebugMapKey.AC_IMPORT_WITH_OPEN_DETAIL_TAG.key] = it
            }
            srcOpenDetailTagConArg?.let {
                debugMapCon[DebugMapKey.SRC_WITH_OPEN_DETAIL_TAG.key] = it
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
            AC_IMPORTED("AcImported", 1),
            SRC("Src", 2),
            GENERATED("Generated", 2),
            JS_CON(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key, 3),
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
            val typeLogConWithTag =
                makeTypeWithTag(logJsActionMap)
            val acImportedOpenDetailTagCon =
                makeActionImportedConWithTag(
                    displayActionImportedAcCon,
                ).let {
                    LogVisualManager.putColorJsActionImportRefConSubKey(it)
                }
            val srcOpenDetailTagCon = makeSrcConWithDetailTag(
                jsAcKeyToSubKeyCon
            )
            val generatedDetailTagCon = generatedWithTagCon(
                keyToSubKeyMapListWithoutAfterSubKey,
                keyToSubKeyMapListWithAfterSubKey,
            ).let {
                LogVisualManager.putColorJsActionImportRefConSubKey(it)
            }
            val jsConLogConWithTag = makeJsConWithTag(logJsActionMap)
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
                acImportOpenDetailTagConArg = acImportedOpenDetailTagCon,
                srcOpenDetailTagConArg = srcOpenDetailTagCon,
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

        private fun makeActionImportedConWithTag(
            displayActionImportedAcConSrc: String,
        ): String {
            val displayActionImportedAcCon =
                displayActionImportedAcConSrc.split("\n").filter {
                    it.trim().isNotEmpty()
                }.joinToString("\n")
            val colorStrPair = LogVisualManager.makeColorCode(DisplayGenre.AC_IMPORTED.order)
            val srcSpanTagCon = LogVisualManager.makeSpanTagHolder(
                colorStrPair,
                "${DisplayGenre.AC_IMPORTED.genre}:\n${displayActionImportedAcCon}"
            )
//            return srcSpanTagCon
            return makeDetailOpenTagHolder(srcSpanTagCon)
        }

        private fun makeSrcConWithDetailTag(
            jsAcKeyToSubKeyCon: String?,
        ): String {
            val displayJsAcSrc =
                DisplayJsAcSrc.make(
                jsAcKeyToSubKeyCon
            )
            val displayJsAcSrcWithTrim =
                displayJsAcSrc.split("\n").filter {
                    it.trim().isNotEmpty()
                }.joinToString("\n")
            val colorStrPair = LogVisualManager.makeColorCode(DisplayGenre.SRC.order)
            val srcSpanTagCon = LogVisualManager.makeSpanTagHolder(
                colorStrPair,
                "${DisplayGenre.SRC.genre}:\n${displayJsAcSrcWithTrim}"
            )
//            return srcSpanTagCon
            return makeDetailOpenTagHolder(srcSpanTagCon)
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
                    "${DisplayGenre.GENERATED.genre}:\n${displayJsAcGeneratedCon}"
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
            val acImportWithOpenDetailTagCon = ErrWord.replace(
                DebugMapManager.readAcImportWithOpenDetailTagCon(),
                errMsg,
            )
            val srcPreTagCon = ErrWord.replace(
                DebugMapManager.readSrcDetailTagCon(),
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
                acImportOpenDetailTagConArg = acImportWithOpenDetailTagCon,
                srcOpenDetailTagConArg = srcPreTagCon,
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
                DebugMapManager.readAcImportWithOpenDetailTagCon(),
                DebugMapManager.readSrcDetailTagCon(),
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

        fun putColorJsActionImportRefConSubKey(
            con: String,
        ): String {
            val deepLikeBlue = "#1f68de"
//            val warningBrown = "#cf7a36"

            val replaceActionImportVirtualSubKeyToColor =
                "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=" to deepLikeBlue
//            val replaceInvalidAfterInAcImportToColor =
//                JsActionKeyManager.ActionImportManager.ActionImportKey.INVALID_AFTER_IN_AC_IMPORT.key to
//                        warningBrown
            return con.replace(
                replaceActionImportVirtualSubKeyToColor.first,
                execMakeSpanTagHolder(
                    replaceActionImportVirtualSubKeyToColor.second,
                    replaceActionImportVirtualSubKeyToColor.first,
                )
            )
//                .replace(
//                Regex("(\\?${replaceInvalidAfterInAcImportToColor.first}=[^?|<>\n]+)"),
//                "<span style=\"color:%s;\">$1</span>".format(
//                    replaceInvalidAfterInAcImportToColor.second,
//                )
//                        execMakeSpanTagHolder(
//                            replaceInvalidAfterInAcImportToColor.second,
//                            replaceInvalidAfterInAcImportToColor.first,
//                        )
//            )
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

//                var displaySubKeyCon = subKeyCon.replace(
//                    Regex("[?]+"),
//                    "?"
//                )
                val displaySubKeyCon = SpeedReplacer.replace(
                    subKeyCon.replace(
                        Regex("[?]+"),
                        "?"
                    ),
                    separatorPairForLog.map {
                        it.first.toString() to it.second
                    },
                )
//                val builder = StringBuilder(
//                    subKeyCon.replace(
//                        Regex("[?]+"),
//                        "?"
//                    )
//                )
//                separatorPairForLog.forEach {
//                    val oldString = it.first.toString()
//                    val newString = it.second
//                    var index = builder.indexOf(oldString)
//                    while (index != -1) {
//                        builder.replace(index, index + oldString.length, newString)
//                        index = builder.indexOf(oldString, index + newString.length)
//                    }
//                }
//                separatorPairForLog.forEach {
//                    displaySubKeyCon = displaySubKeyCon.replace(
//                        it.first.toString(),
//                        it.second,
//                    )
//                }
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
            val jsAcKeyToSubKeyConForJs = SpeedReplacer.replace(
                jsAcKeyToSubKeyCon.replace(
                    Regex("[?]+"),
                    "?"
                ),
                separatorPairForLog.map {
                    it.first.toString() to it.second
                },
            )
//            val builder = StringBuilder(
//                jsAcKeyToSubKeyCon.replace(
//                    Regex("[?]+"),
//                    "?"
//                )
//            )
//            separatorPairForLog.forEach {
//                val oldString = it.first.toString()
//                val newString = it.second
//                var index = builder.indexOf(oldString)
//                while (index != -1) {
//                    builder.replace(index, index + oldString.length, newString)
//                    index = builder.indexOf(oldString, index + newString.length)
//                }
//            }
//            var jsAcKeyToSubKeyConForJs = jsAcKeyToSubKeyCon.replace(
//                Regex("[?]+"),
//                "?"
//            )
//            separatorPairForLog.forEach {
//                jsAcKeyToSubKeyConForJs = jsAcKeyToSubKeyConForJs.replace(
//                    it.first.toString(),
//                    it.second,
//                )
//            }
            return QuoteTool.splitBySurroundedIgnore(
                jsAcKeyToSubKeyConForJs,
                '|'
            ).filter { it.trim().isNotEmpty() }.map {
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
        ): String {
            val evaluateAcConSrc = makeCon(keyToSubKeyMapListWithAfterSubKey,) +
                    makeCon(keyToSubKeyMapListWithoutAfterSubKey)
            return evaluateAcConSrc
        }

        fun makeCon(
            keyToSubKeyMapList: List<Pair<String, Map<String, String>>>?,
        ): String {
            if (
                keyToSubKeyMapList.isNullOrEmpty()
            ) return String()
            return keyToSubKeyMapList.map {
                val mainKeyName = "\n\n>|${it.first}="
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcLogTool.txt").absolutePath,
//                listOf(
//                    "errWord: ${errWord}"
//                ).joinToString("\n\n")
//            )
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
            val isEscapeErrWord =
                Regex("^[{}\$]$").containsMatchIn(errWord)
            val repConWithErrWord = when(isEscapeErrWord) {
                true -> repConWithEndTagStr
                else -> repConWithEndTagStr.replace(
                    errWord,
                    errWordWithRedSpan
                )
            }
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
            val repConWithTag = replaceWithTagMark(
                repConWithErrWord,
                tagStrToMarkList
            )
            return when(isEscapeErrWord){
                true -> repConWithTag.replace(
                    errWord,
                    errWordWithRedSpan
                )
                else -> repConWithTag
            }
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
            return SpeedReplacer.replace(
                repConWithErrWord,
                tagStrToMarkList.map {
                   it.second to it.first
                },
            )
//            var repCon = repConWithErrWord
//            tagStrToMarkList.forEach {
//                val tagMark = it.second
//                val tagStr = it.first
//                repCon = repCon.replace(
//                    tagMark,
//                    tagStr
//                )
//            }
//            return repCon
        }

        private fun errExtractHandler(
            errCon: String
        ): String? {
            val errWordExtractRegexList = listOf(
                Regex("([^ \t\n]+?) is not defined$"),
                Regex("([^\n]*) is not a function$"),
                Regex("SyntaxError: Missing initializer in (const) declaration$"),
                Regex("SyntaxError: Unexpected token '([^\n]*)'"),
                Regex("SyntaxError: Missing ([^\n]*) in template expression$"),
                Regex("SyntaxError: missing ([^\n]*) after argument list$"),
                Regex("Uncaught SyntaxError: Unexpected identifier '([^\n]*)'"),
                Regex("Cannot read properties of undefined \\(reading '([^\n]*)'\\)")
            )
            val errConFirstLine = errCon.split("\n")
                .firstOrNull()?.trim()
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
            val putColorConByFuncMainKeyTwoOverErr = FuncMainKeyTwoOverErr.makePutColorCon(
                srcJsCon,
                errMessage,
            )
            val putColorConByForbiddenJsKeyDirectSpecify = ForbiddenJsKeyDirectSpecifyErr.makePutColorCon(
                putColorConByFuncMainKeyTwoOverErr,
                errMessage,
            )
            val putColorConByIdDuplicateErr = IdDuplicateErr.makePutColorCon(
                putColorConByForbiddenJsKeyDirectSpecify,
                errMessage,
            )
            val putColorConByNotStartVerticalVarMainKey = NotStartVerticalVarMainKey.makePutColorCon(
                putColorConByIdDuplicateErr,
                errMessage,
            )
            val putColorConByQuoteOdd = QuoteNumCheck.makePutColorCon(
                putColorConByNotStartVerticalVarMainKey,
                errMessage
            )
            val putColorConByIrregularStrKeyCon = IrregularStrKeyCon.makePutColorCon(
                putColorConByQuoteOdd,
                errMessage,
            )
            val putColorConByIrregularFuncValue = IrregularFuncValue.makePutColorCon(
                putColorConByIrregularStrKeyCon,
                errMessage,
            )
            val putColorConByVarNotInit = VarNotInit.makePutColorCon(
                putColorConByIrregularFuncValue,
                errMessage,
            )
            val putColorConByRunVarPrefixUsedErr = RunVarPrefixUsedAsArgErr.makePutColorCon(
                putColorConByVarNotInit,
                errMessage,
            )
            val putColorConByMissImportPathErr = MissImportPathErr.makePutColorCon(
                putColorConByRunVarPrefixUsedErr,
                errMessage,
            )
            val putColorConByMissLastVarKeyErrForAcVar = MissLastVarKeyErrForAcVar.makePutColorCon(
                putColorConByMissImportPathErr,
                errMessage,
            )
            val putColorConByMissVarKeyErr = MissLastReturnKeyErrForAcImport.makePutColorCon(
                putColorConByMissLastVarKeyErrForAcVar,
                errMessage,
            )
            val putColorConByInvalidAfterInAcImportErr = InvalidAfterIdInAcImportErr.makePutColorCon(
                putColorConByMissVarKeyErr,
                errMessage,
            )
            val putColorConByPathNotFound = PathNotFound.makePutColorCon(
                putColorConByInvalidAfterInAcImportErr,
                errMessage,
            )
            val putColorConByPathNotRegisterInRepValErr = AcImportPathNotRegisterInRepValErr.makePutColorCon(
                putColorConByPathNotFound,
                errMessage,
            )
            val putColorConByLoopMethodOrArgsNotExist = LoopMethodOrArgsNotExist.makePutColorCon(
                putColorConByPathNotRegisterInRepValErr,
                errMessage
            )
            val putColorConBySyntaxCheckEnum =
                makePutColorConBySyntaxCheckEnum(
                    srcJsCon,
                    putColorConByLoopMethodOrArgsNotExist,
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

    object FuncMainKeyTwoOverErr {

        private val funcMainKey = JsActionKeyManager.JsActionsKey.JS_FUNC.key
        private val funcMainKeyTwoOverErrMessage = "'${funcMainKey}' key must use in one main key"
        private val funcMainKeyPrefix = "|${funcMainKey}="

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(funcMainKeyTwoOverErrMessage)
            if (
                isNotErr
            ) return curPutColorCon
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsLogColorCon.txt").absolutePath,
//                listOf(
//                    "onTimesVarWord: ${onTimesVarWord}",
//                    "curPutColorCon: ${curPutColorCon}",
//                ).joinToString("\n")
//            )
            return curPutColorCon.replace(
                funcMainKeyPrefix,
                "<span style=\"color:${errRedCode};\">${funcMainKeyPrefix}</span>"
            )
        }

        fun check(
            context: Context?,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
            actionImportedCon: String,
        ): Boolean {
            val judgeActionImportedCon = "\n${actionImportedCon}"
            val mainKeyNum = actionImportedKeyToSubKeyConList.size
            val isNotFuncMainKey =
                !judgeActionImportedCon.contains(funcMainKeyPrefix)
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_func_maienKey.txt").absolutePath,
//                listOf(
//                    "judgeActionImportedCon: ${judgeActionImportedCon}",
//                    "actionImportedKeyToSubKeyConList: ${actionImportedKeyToSubKeyConList}",
//                    "mainKeyNum: ${mainKeyNum}",
//                    "isNotFuncMainKey: ${isNotFuncMainKey}"
//                ).joinToString("\n\n")
//            )
            if(
                mainKeyNum <= 1
            ) return false
            if(
                isNotFuncMainKey
            ) return false
            saveFirstLog(
                context,
                funcMainKeyTwoOverErrMessage,
            )
            return true
        }
    }

    object VarNotUse {

        private const val varNotUseErrMessagePrefix = "Not use var "
        private const val varNotUseErrMessage = "${varNotUseErrMessagePrefix}'%s'"
        private val messageExtractRegex = Regex("${varNotUseErrMessagePrefix}'(.*)'")
        private const val escapeRunPrefix = JsActionKeyManager.JsVarManager.escapeRunPrefix


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
                if(
                    varName.startsWith(escapeRunPrefix)
                ) return@firstOrNull false
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
        private val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val notFountPath = try {
                errMessage.replace(
                    extractPathRegex,
                    "$1"
                )
            } catch(e: Exception){
                errMessage
            }
            val errCodeExtractRegex =
                RegexTool.convert("${errCodePrefix} [!]${notFountPath}[!]")
            return try {
                curPutColorCon
                    .replace(
                        errCodeExtractRegex,
                        notFountPath,
                    )
            } catch(e: Exception){
                curPutColorCon
            }.replace(
                    notFountPath,
                    "<span style=\"color:${errRedCode};\">${notFountPath}</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
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
            val findRegex = Regex("${errCodePrefix} [!][^!]+[!]")
            return try {
                findRegex.find(evaluateAcCon)
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
                "\\?${importPrefix}[^\n|?&\"`]*"
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
        ): Boolean {
            if(
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return false
            val evaluateSrcCon = actionImportedKeyToSubKeyConList.map {
                val mainKey = it.first
                val subKeyCon = it.second
                "|${mainKey}=${subKeyCon}"
            }.joinToString("\n")
            if(
                evaluateSrcCon.isEmpty()
            ) return false
//            findTsvImportNotExistPath(evaluateSrcCon).let{
//                if(
//                    it.isNullOrEmpty()
//                ) return@let
//                val errMessage = errMessageTemplate.format(it)
//                saveFirstLog(
//                    context,
//                    errMessage,
//                )
//                return true
//            }
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
                "${jsImportKeyPrefix}[^\n|?&\"`]*"
            )
            return findImportNotExistPath(
                evaluateAcGeneCon,
                findTsvImportRegex,
                jsImportKeyPrefix
            )
        }

//        private fun findTsvImportNotExistPath(
//            evaluateAcCon: String
//        ): String? {
//            val tsvVarsKeyPrefix = "${JsActionKeyManager.JsActionsKey.TSV_VARS.key}="
//            val findTsvVarsKeyRegex = Regex(
//                "${tsvVarsKeyPrefix}[^\n|?&\"`]*"
//            )
//            val importPathKeyPrefix = "${JsActionKeyManager.CommonPathKey.IMPORT_PATH.key}="
//            val findTsvImportPathRegex = Regex(
//                "\\?${importPathKeyPrefix}[^\n|?&\"`]*"
//            )
//            return findImportNotExistPath(
//                evaluateAcCon,
//                findTsvImportPathRegex,
//                tsvVarsKeyPrefix,
//            )
//        }

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

    object AcImportPathNotRegisterInRepValErr {

        private const val errMessagePrefix =
            "Multiple use action import path must be register in 'setReplaceVariable' "
        private const val errMessageTemplate = "${errMessagePrefix}'%s'"
        private val extractPathRegex = Regex("${errMessagePrefix}'(.*)'")
        private const val errCodePrefix =
            "${JsActionKeyManager.PathNotRegisterInRepValChecker.notRegisterCodePrefix}:"
        private val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val notFountPath = try {
                errMessage.replace(
                    extractPathRegex,
                    "$1"
                )
            } catch(e: Exception){
                errMessage
            }
            val errCodeExtractRegex =
                RegexTool.convert("${errCodePrefix} [!]${notFountPath}[!]")
            return try {
                curPutColorCon
                    .replace(
                        errCodeExtractRegex,
                        notFountPath,
                    )
            } catch(e: Exception){
                curPutColorCon
            }.replace(
                notFountPath,
                "<span style=\"color:${errRedCode};\">${notFountPath}</span>",
            )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
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
//            findImportNotExistPathForGene(evaluateGeneCon).let {
//                if(
//                    it.isNullOrEmpty()
//                ) return@let
//                val errMessage = errMessageTemplate.format(it)
//                saveFirstLog(
//                    context,
//                    errMessage,
//                )
//                return true
//            }
            return false
        }

        private fun findAcImportNotFoundPath(evaluateAcCon: String): String? {
            val findRegex = Regex("${errCodePrefix} [!][^!]+[!]")
            return try {
                findRegex.find(evaluateAcCon)
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
                "\\?${importPrefix}[^\n|?&\"`]*"
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
        ): Boolean {
            if(
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return false
            val evaluateSrcCon = actionImportedKeyToSubKeyConList.map {
                val mainKey = it.first
                val subKeyCon = it.second
                "|${mainKey}=${subKeyCon}"
            }.joinToString("\n")
            if(
                evaluateSrcCon.isEmpty()
            ) return false
//            findTsvImportNotExistPath(evaluateSrcCon).let{
//                if(
//                    it.isNullOrEmpty()
//                ) return@let
//                val errMessage = errMessageTemplate.format(it)
//                saveFirstLog(
//                    context,
//                    errMessage,
//                )
//                return true
//            }
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
                "${jsImportKeyPrefix}[^\n|?&\"`]*"
            )
            return findImportNotExistPath(
                evaluateAcGeneCon,
                findTsvImportRegex,
                jsImportKeyPrefix
            )
        }

//        private fun findTsvImportNotExistPath(
//            evaluateAcCon: String
//        ): String? {
//            val tsvImportKeyPrefix = "${JsActionKeyManager.JsActionsKey.TSV_VARS.key}="
//            val findTsvImportRegex = Regex(
//                "${tsvImportKeyPrefix}[^\n|?&\"`]*"
//            )
//            return findImportNotExistPath(
//                evaluateAcCon,
//                findTsvImportRegex,
//                tsvImportKeyPrefix,
//            )
//        }

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

    object InvalidAfterIdInAcImportErr {

        private val actionVar =
            JsActionKeyManager.JsActionsKey.ACTION_VAR.key
        private val afterKey = JsActionKeyManager.JsSubKey.AFTER.key
        private val errMessagePrefix =
            "Specify defined ID by '${afterKey}' in ${actionVar} file: "
        private val errMessageTemplate = "${errMessagePrefix}'%s'"
        private val extractInValidAfterIdRegex = Regex("${errMessagePrefix}'(.*)'")
        private val invalidAfterKeyMark =
            JsActionKeyManager.ActionImportManager.ActionImportKey.INVALID_AFTER_IN_AC_IMPORT.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(errMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val invalidAfterId = try {
                errMessage.replace(
                    extractInValidAfterIdRegex,
                    "$1"
                )
            } catch(e: Exception){
                errMessage
            }
            return try {
                curPutColorCon
                    .replace(
                        Regex(
                            "(\\?${afterKey}=[\"`' ]{0,2}${invalidAfterId}[\"`' ]{0,2})"
                        ),
                        "<span style=\"color:${errRedCode};\">$1</span>",
                    )
            } catch(e: Exception){
                curPutColorCon
            }
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            val invalidAfterKeyPrefix =
                "?${invalidAfterKeyMark}="
            val findInvalidAfterKeyMarkRegex =
                Regex("\\?${invalidAfterKeyMark}=[^?|\n<>]+")
            val invalidAfterKeyValueCon = findInvalidAfterKeyMarkRegex.find(
                evaluateGeneCon
            )?.value?.removePrefix(invalidAfterKeyPrefix)?.let {
                QuoteTool.trimBothEdgeQuote(it)
            }
            if(
                invalidAfterKeyValueCon.isNullOrEmpty()
            ) return false
            val errMessage =
                errMessageTemplate.format(invalidAfterKeyValueCon)
            saveFirstLog(
                context,
                errMessage,
            )
            return true
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

                val isNotIrregularFuncValue =
                    !irregularFuncValueRegex.containsMatchIn(funcValue)
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

    object RunVarPrefixUsedAsArgErr {

        private const val runVarPrefixUsedErrMessagePrefix =
            "Var name with 'run' prefix is used: "
        private const val runVarPrefixUsedErrMessageTemplate =
            "${runVarPrefixUsedErrMessagePrefix}'%s'"
        private val varKey = JsActionKeyManager.JsSubKey.VAR.key
        private const val escapeRunPrefix = JsActionKeyManager.JsVarManager.escapeRunPrefix
        private const val subKeySeparator = '?'

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr =
                !errMessage.startsWith(runVarPrefixUsedErrMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val extractVarNameWithRunVarPrefix = Regex(
                "${runVarPrefixUsedErrMessagePrefix}'(.*)'"
            )
            val varNameWithRunVarPrefix = extractVarNameWithRunVarPrefix.replace(
                errMessage,
                "$1"
            )
            return curPutColorCon.replace(
                varNameWithRunVarPrefix,
                "<span style=\"color:${errRedCode};\">${varNameWithRunVarPrefix}</span>",
            )
        }

        fun check(
            context: Context?,
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>
        ): Boolean {
            if(
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return false
            val varNameListWithRunVarPrefix = makeVarNameListWithRunVarPrefix(
                actionImportedKeyToSubKeyConList
            )
            val usedRunVarName = howHasUsedRunVarName(
                actionImportedKeyToSubKeyConList,
                varNameListWithRunVarPrefix
            )
            if(
                usedRunVarName.isNullOrEmpty()
            ) return false
            saveFirstLog(
                context,
                runVarPrefixUsedErrMessageTemplate.format(usedRunVarName),
            )
            return true
        }

        private fun makeVarNameListWithRunVarPrefix(
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>
        ): List<String> {
            return actionImportedKeyToSubKeyConList.map {
                    keyToSubKeyCon ->
                val mainKey = keyToSubKeyCon.first
                val subKeyCon = keyToSubKeyCon.second
                val jsKeyMap = CmdClickMap.createMap(
                    "${mainKey}=${subKeyCon}",
                    subKeySeparator
                ).toMap()
                val valName = QuoteTool.trimBothEdgeQuote(
                    jsKeyMap.get(varKey)
                )
                val isRunVarPrefix = valName.startsWith(escapeRunPrefix)
                when(isRunVarPrefix){
                    true -> valName
                    else -> String()
                }
            }.filter {
                it.isNotEmpty()
            }
        }

        private fun howHasUsedRunVarName(
            actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
            varNameListWithRunVarPrefix: List<String>
        ): String? {
            if(
                actionImportedKeyToSubKeyConList.isEmpty()
            ) return null
            val varReturnKeyEqual =
                "?${JsActionKeyManager.OnlyVarSubKey.VAR_RETURN.key}="
            val varReturnRegex =
                Regex("\\?${JsActionKeyManager.OnlyVarSubKey.VAR_RETURN.key}=[^?|\n]+")
            actionImportedKeyToSubKeyConList.forEach {
                    keyToSubKeyCon ->
                val mainKey = keyToSubKeyCon.first
                val subKeyCon = keyToSubKeyCon.second
                val jsKeyMap = CmdClickMap.createMap(
                    "${mainKey}=${subKeyCon}",
                    subKeySeparator
                ).toMap()
                val valName = QuoteTool.trimBothEdgeQuote(
                    jsKeyMap.get(varKey)
                )
                varNameListWithRunVarPrefix.forEach has@ {
                        varNameWithRunPrefix ->
                    if(
                        varNameWithRunPrefix == valName
                    ) {
                        varReturnRegex.findAll(subKeyCon).forEach {
                            val hasEscapeRunPrefix = QuoteTool.trimBothEdgeQuote(
                                it.value.removePrefix(varReturnKeyEqual)
                            ).startsWith(escapeRunPrefix)
                            when(hasEscapeRunPrefix){
                                true -> return varNameWithRunPrefix
                                else -> {}
                            }
                        }
                        return@has
                    }
                    val findVarNameWithRunPrefixRegex =
                        Regex("[^a-zA-Z0-9_]${varNameWithRunPrefix}[^a-zA-Z0-9_]")
                    val hasNotVarNameWithRunPrefix =
                        !findVarNameWithRunPrefixRegex.containsMatchIn("%${subKeyCon}%")
                    if(
                        hasNotVarNameWithRunPrefix
                    ) return@has
                    return varNameWithRunPrefix
                }
            }
            return null
        }
    }

    object IrregularStrKeyCon {

        private const val irregularStrKeyConErrMsgPrefix =
            "Follow keyCon is only alphanumeric chars: \n "
        private const val irregularStrKeyConErrMsgTemplate =
            "${irregularStrKeyConErrMsgPrefix}%s"

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr =
                !errMessage.contains(irregularStrKeyConErrMsgPrefix)
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcLog00.txt").absolutePath,
//                listOf(
//                    "irregularStrKeyConErrMsgPrefix: ${irregularStrKeyConErrMsgPrefix}",
//                    "errMessage: ${errMessage}",
//                    "isNotErr: ${isNotErr}",
//                ).joinToString("\n\n")
//            )
            if (
                isNotErr
            ) return curPutColorCon
            val irregularStrKeyCon =
                errMessage.removePrefix(irregularStrKeyConErrMsgPrefix).replace(
                    Regex("^[a-zA-Z0-9_]+="),
                    String()
                )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcLog.txt").absolutePath,
//                listOf(
//                    "irregularStrKeyCon: ${irregularStrKeyCon}"
//                ).joinToString("\n\n")
//            )
            if(
                irregularStrKeyCon.isEmpty()
            ) return curPutColorCon
            return curPutColorCon
                .replace(
                    irregularStrKeyCon,
                    "<span style=\"color:${errRedCode};\">${irregularStrKeyCon}</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String?,
        ): Boolean {
            if(
                evaluateGeneCon.isNullOrEmpty()
            ) return false
            val checkKeyList = listOf(
                JsActionKeyManager.JsActionsKey.JS_VAR.key,
                JsActionKeyManager.JsSubKey.ID.key,
                JsActionKeyManager.JsSubKey.AFTER.key,
            )
            val mainKeyOrRegexStr =
                checkKeyList.joinToString("|")
            val irregularStrKeyConRegex =
                Regex("[|?](${mainKeyOrRegexStr})=[^|?\n]*[^a-zA-Z0-9_\n][^|?\n]*")
            val irregularStrKeyValue = irregularStrKeyConRegex.find(
                "|${evaluateGeneCon}"
            )?.value?.replace(
                Regex("^[|?]"),
                String()
            ) ?: return false
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcMask.txt").absolutePath,
//                listOf(
//                    "mainKeyOrRegexStr: ${mainKeyOrRegexStr}",
//                    "maskSurroundSeparatorCon: ${maskSurroundSeparatorCon}",
//                    "notStartMainKey: ${notStartVerticalVarMainKeyRegex.find(
//                        maskSurroundSeparatorCon
//                    )?.value}"
//                ).joinToString("\n\n")
//            )
            saveFirstLog(
                context,
                irregularStrKeyConErrMsgTemplate.format(
                    irregularStrKeyValue
                ),
            )
            return true
        }
    }

    object NotStartVerticalVarMainKey {

        private const val notStartVerticalVarMainKeyPrefix =
            "Not start '|' in main key:"
        private const val notStartVerticalVarMainKeyErrMsgTemplate =
            "${notStartVerticalVarMainKeyPrefix}'%s'"

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.contains(notStartVerticalVarMainKeyPrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val extractNotStartVerticalVarMainKeyRegex = Regex(
                "${notStartVerticalVarMainKeyPrefix}'(.*)'"
            )
            val notStartVerticalVarMainKey = errMessage.replace(
                extractNotStartVerticalVarMainKeyRegex,
                "$1"
            )
            if(
                notStartVerticalVarMainKey.isEmpty()
            ) return curPutColorCon
            return curPutColorCon
                .replace(
                    Regex("([^|?])(${notStartVerticalVarMainKey})"),
                    "$1<span style=\"color:${errRedCode};\">$2</span>",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String?,
        ): Boolean {
            if(
                evaluateGeneCon.isNullOrEmpty()
            ) return false
            val maskSurroundSeparatorCon = QuoteTool.maskSurroundQuote(
                evaluateGeneCon
            )
            val mainKeyOrRegexStr =
                JsActionKeyManager.JsActionsKey.values().map{
                    it.key
                }.joinToString("|")
            val notStartVerticalVarMainKeyRegex =
                Regex("[^|?](${mainKeyOrRegexStr})=")
            val notStartVerticalVarMainKey = notStartVerticalVarMainKeyRegex.find(
                "|${maskSurroundSeparatorCon}"
            )?.value?.replace(
                Regex("^[^|?]"),
                String()
            ) ?: return false
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcMask.txt").absolutePath,
//                listOf(
//                    "mainKeyOrRegexStr: ${mainKeyOrRegexStr}",
//                    "maskSurroundSeparatorCon: ${maskSurroundSeparatorCon}",
//                    "notStartMainKey: ${notStartVerticalVarMainKeyRegex.find(
//                        maskSurroundSeparatorCon
//                    )?.value}"
//                ).joinToString("\n\n")
//            )
            saveFirstLog(
                context,
                notStartVerticalVarMainKeyErrMsgTemplate.format(
                    notStartVerticalVarMainKey
                ),
            )
            return true
        }
    }

    object MissImportPathErr {

        private val actionVarKey =
            JsActionKeyManager.JsActionsKey.ACTION_VAR.key
        private val importPathSubKey =
            JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_IMPORT_PATH.key
        private val missImportPathErrMark = "?${importPathSubKey}="
        private val missImportPathErrMessage =
            "Miss '${importPathSubKey}' in ${actionVarKey} section"

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.startsWith(missImportPathErrMessage)
            if (
                isNotErr
            ) return curPutColorCon
            return curPutColorCon
                .replace(
                    missImportPathErrMark,
                    "<span style=\"color:${errRedCode};\">${missImportPathErrMark}</span>",
                )
        }

        fun check(
            context: Context?,
            actionImportedCon: String,
        ): Boolean {
            if(
                actionImportedCon.isEmpty()
            ) return false
            val isNotmissImportPathErrMarkKey = !actionImportedCon.contains(
                missImportPathErrMark
            )
            if(
                isNotmissImportPathErrMarkKey
            ) return false
            saveFirstLog(
                context,
                missImportPathErrMessage
            )
            return true
        }
    }
    object MissLastVarKeyErrForAcVar {

        private val actionVarKey =
            JsActionKeyManager.JsActionsKey.ACTION_VAR.key
        private val missLastVarKeyErrMark =
            "?${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_LAST_VAR_KEY.key}="
        private val varKey = JsActionKeyManager.JsSubKey.VAR.key
        private val missLastVarKeyErrMessagePrefix =
            "Miss '${varKey}' key in last sec in ${actionVarKey} file"


        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.startsWith(missLastVarKeyErrMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            return curPutColorCon
                .replace(
                    missLastVarKeyErrMark,
                    "<span style=\"color:${errRedCode};\">${missLastVarKeyErrMark}</span>",
                )
        }

        fun check(
            context: Context?,
            actionImportedCon: String,
        ): Boolean {
            if(
                actionImportedCon.isEmpty()
            ) return false
            val isNotMissLastVarKeyErrMarkKey = !actionImportedCon.contains(
                missLastVarKeyErrMark
            )
            if(
                isNotMissLastVarKeyErrMarkKey
            ) return false
            saveFirstLog(
                context,
                missLastVarKeyErrMessagePrefix
            )
            return true
        }
    }

    object MissLastReturnKeyErrForAcImport {

        private val actionVarKey =
            JsActionKeyManager.JsActionsKey.ACTION_VAR.key
        private val missLastReturnKeyErrMark =
            "?${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_LAST_RETURN_KEY.key}="
        private val varReturnKey = JsActionKeyManager.OnlyVarSubKey.VAR_RETURN.key
        private val missLastVarKeyErrMessage =
            "Miss '${varReturnKey}' key in last sec in ${actionVarKey} file"


        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr = !errMessage.startsWith(missLastVarKeyErrMessage)
            if (
                isNotErr
            ) return curPutColorCon
            return curPutColorCon
                .replace(
                    missLastReturnKeyErrMark,
                    "<span style=\"color:${errRedCode};\">${missLastReturnKeyErrMark}</span>",
                )
        }

        fun check(
            context: Context?,
            actionImportedCon: String,
        ): Boolean {
            if(
                actionImportedCon.isEmpty()
            ) return false
            val isNotMissLastVarKeyErrMarkKey = !actionImportedCon.contains(
                missLastReturnKeyErrMark
            )
            if(
                isNotMissLastVarKeyErrMarkKey
            ) return false
            saveFirstLog(
                context,
                missLastVarKeyErrMessage
            )
            return true
        }
    }

    object IdDuplicateErr {

        private val IdDuplicateErrMessagePrefix =
            "Duplicated Id err: "
        private val IdDuplicateErrMessageTemplate =
            "${IdDuplicateErrMessagePrefix}'%s'"
        private val idSubKey = JsActionKeyManager.JsSubKey.ID.key

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr =
                !errMessage.startsWith(IdDuplicateErrMessagePrefix)
            if (
                isNotErr
            ) return curPutColorCon
            val extractDuplicateId =
                errMessage.removePrefix(IdDuplicateErrMessagePrefix).let {
                    QuoteTool.trimBothEdgeQuote(it)
                }
            return curPutColorCon
                .replace(
                    Regex("(\\?${idSubKey}=)[ \"'`]{0,2}(${extractDuplicateId})[ \"'`]{0,2}"),
                    "<span style=\"color:${errRedCode};\">$1$2</span>",
                )
        }

        fun check(
            context: Context?,
            actionImportedCon: String,
        ): Boolean {
            if(
                actionImportedCon.isEmpty()
            ) return false
            val idSubKeyPrefix = "?${idSubKey}="
            val findIdRegex = Regex(
                "\\?${idSubKey}=[^?|\n<>]+"
            )
            val idList = findIdRegex.findAll(actionImportedCon).map {
                QuoteTool.trimBothEdgeQuote(
                    it.value.removePrefix(idSubKeyPrefix)
                )
            }
            val duplicateId =
                idList.groupingBy {
                    it
                }.eachCount()
                    .filter { (_, v) -> v >= 2 }
                    .keys.firstOrNull()
            if(
                duplicateId.isNullOrEmpty()
            ) return false
            saveFirstLog(
                context,
                IdDuplicateErrMessageTemplate.format(
                    duplicateId
                )
            )
            return true
        }
    }

    object ForbiddenJsKeyDirectSpecifyErr {

        private const val forbiddenJsKeyDirectSpecifyErrMsg =
            "Forbidden 'js' main Key direct specify"
        private val forbiddenJsKeyDirectSpecifyErrMarkKeyEqual =
            "${JsActionKeyManager.JsSubKey.FORBIDDEN_JS_KEY_DIRECT_SPECIFY.key}="
        private val jsMainKeyEqual =
            "${JsActionKeyManager.JsActionsKey.JS.key}="

        fun makePutColorCon(
            curPutColorCon: String,
            errMessage: String,
        ): String {
            val isNotErr =
                !errMessage.contains(forbiddenJsKeyDirectSpecifyErrMsg)
            if (
                isNotErr
            ) return curPutColorCon
            return curPutColorCon
                .replace(
                    Regex("([^a-zA-Z0-9_])(\\?${forbiddenJsKeyDirectSpecifyErrMarkKeyEqual})([^a-zA-Z0-9_])"),
                    "$1<span style=\"color:${errRedCode};\">$2</span>$3",
                ).replace(
                    Regex("([^a-zA-Z0-9_])(${jsMainKeyEqual})([^a-zA-Z0-9_])"),
                    "$1<span style=\"color:${errRedCode};\">$2</span>$3",
                )
        }

        fun check(
            context: Context?,
            evaluateGeneCon: String,
        ): Boolean {
            if(
                evaluateGeneCon.isEmpty()
            ) return false
            val isNotMatchToAcVar = !evaluateGeneCon.contains(
                "?${forbiddenJsKeyDirectSpecifyErrMarkKeyEqual}"
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_notMatchSrcVarToUseVarErrMarkKeyEqual.txt").absolutePath,
//                listOf(
//                    "actionImportedCon: ${actionImportedCon}",
//                    "notMatchSrcVarToUseVarErrMarkKeyEqual: ${notMatchSrcVarToUseVarErrMarkKeyEqual}",
//                    "isNotMatchToUseVar: ${isNotMatchToUseVar}"
//                ).joinToString("\n\n")
//            )
            if(
                isNotMatchToAcVar
            ) return false
            saveFirstLog(
                context,
                forbiddenJsKeyDirectSpecifyErrMsg
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
            val tmpPronoun = JsActionKeyManager.JsVarManager.tmpPronoun
            val varTmpFuncDifinition = "${funcKey}=${tmpPronoun}.${loopMethod}"
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
                    Regex("(${varTmpFuncDifinition}[^\n<>]+)"),
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
            val isNotloopArgsDefinition = !jsCon.contains(
                loopArgsDefinitionErrMarkPrefix
            )
            if(
                isNotloopArgsDefinition
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
