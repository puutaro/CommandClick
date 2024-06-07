package com.puutaro.commandclick.proccess.js_macro_libs.common_libs

import TsvImportManager
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File


object JsActionKeyManager {

    enum class JsActionsKey(
        val key: String
    ) {
        JS("js"),
        JS_VAR("var"),
        JS_FUNC("func"),
        JS_PATH("jsPath"),
        JS_IMPORT("jsImport"),
        TSV_IMPORT("tsvImport"),
        ACTION_IMPORT("actionImport"),
    }

    enum class CommonPathKey(
        val key: String
    ) {
        IMPORT_PATH("importPath"),
        USE(TsvImportManager.tsvImportUsePhrase),
    }


    enum class JsSubKey(
        val key: String
    ) {
        FUNC("func"),
//        METHOD("method"),
//        METHOD_ARGS("methodArgs"),
        ID("id"),
        ARGS("args"),
        LOOP_ARG_NAMES("loopArgNames"),
        ON_RETURN("onReturn"),
        IF("if"),
        VAR("var"),
        VAR_VALUE("value"),
        AFTER_JS_CON("afterJsCon"),
        AFTER("after"),
        DESC("desc"),
        DELAY("delay"),
        ON_LOG("onLog"),
//        DELETE_VAR("deleteVar"),
        IF_BRACKET_START("ifBracketStart"),
        IF_BRACKET_END("ifBracketEnd"),
        FUNC_BRACKET_START("funcBracketStart"),
        FUNC_BRACKET_END("funcBracketEnd"),
        FUNC_VAR("funcVar"),
        FORBIDDEN_JS_KEY_DIRECT_SPECIFY("FORBIDDEN_JS_KEY_DIRECT_SPECIFY")
    }

    enum class OnlyVarSubKey(
        val key: String
    ){
        EXIT("exit"),
        VAR_RETURN("varReturn"),
    }

    const val prevPronoun = "prev"
    const val noDefinitionBeforeVarNameByPrev = "NO_DIFINITION_BEFORE_VAR_NAME_BY_${prevPronoun}"


    const val noQuotePrefix = "NO_QUOTE:"
    private val jsConPrefix = "con:"
    enum class VirtualSubKey(
        val key: String
    ){
        ACTION_IMPORT_CON("actionImportCon"),
        VAR_NOT_INIT("varNotInit"),
    }

    object PathExistChecker {

        const val notFoundCodePrefix = "CMDCLICK_NOT_FOUND_PATH"
        const val notFoundCodeTemplate = "${notFoundCodePrefix}: !%s!"
        fun makeCodeOrPath(jsPathCon: String?): String {
            if(
                jsPathCon.isNullOrEmpty()
            ) return notFoundCodeTemplate.format(jsPathCon)
            val isJsFile = File(jsPathCon).isFile
            return when(isJsFile){
                false -> notFoundCodeTemplate.format(jsPathCon)
                else -> jsPathCon
            }
        }
    }

    object PathNotRegisterInRepValChecker {

        const val notRegisterCodePrefix = "CMDCLICK_NOT_REGISTER_REP_VAL_PATH"
        const val notRegisterCodeTemplate = "${notRegisterCodePrefix}: !%s!"

        fun echoErrSignal (
            importPath: String,
            beforeActionImportMap: Map<String, String>,
            replaceVariableMapCon: String,
        ): String? {
            if(
                importPath.isEmpty()
            ) return null
            val isNotMultiTimesUse =
                !beforeActionImportMap.contains(importPath)
            if(
                isNotMultiTimesUse
            ) return null
            val containsReplaceVariableMapCon = replaceVariableMapCon.contains(
                importPath
            )
            if(
                containsReplaceVariableMapCon
            ) return null
            return notRegisterCodeTemplate.format(importPath)


        }
        fun makeCodeOrPath(jsPathCon: String?): String {
            if(
                jsPathCon.isNullOrEmpty()
            ) return notRegisterCodeTemplate.format(jsPathCon)
            val isJsFile = File(jsPathCon).isFile
            return when(isJsFile){
                false -> notRegisterCodeTemplate.format(jsPathCon)
                else -> jsPathCon
            }
        }
    }

    object ActionImportManager {

        private const val mainKeySeparator = '|'
        private const val subKeySeparator = '?'
        const val useAfterAllow = "=>"
        const val useVarAllow = "=>"

        enum class ActionImportKey(
            val key: String,
        ){
            IMPORT_PATH(CommonPathKey.IMPORT_PATH.key),
            REPLACE("replace"),
            USE_VAR("useVar"),
            AFTER(JsSubKey.AFTER.key),
            WHEN("when"),
//            NOT_MATCH_SRC_AFTER_TO_USE_AFTER("NOT_MATCH_SRC_AFTER_TO_USE_AFTER"),
//            MISS_AFTER_KEY("MISS_AFTER_KEY"),
//            IRREGULAR_AFTER_ID("IRREGULAR_AFTER_ID"),
//            NOT_MATCH_SRC_VAR_TO_USE_VAR("NOT_MATCH_SRC_VAR_TO_USE_VAR"),

            INVALID_AFTER_IN_AC_IMPORT("INVALID_AFTER_IN_AC_IMPORT"),
            MISS_LAST_USE_VAR_KEY("MISS_LAST_USE_VAR_KEY"),
            MISS_LAST_VAR_KEY("MISS_LAST_VAR_KEY"),
            MISS_LAST_RETURN_KEY("MISS_LAST_RETURN_KEY"),
//            RETURN_RUN_VAR_PREFIX_FORBIDDEN("RETURN_RUN_VAR_PREFIX_FORBIDDEN"),
        }
        const val errConSeparator = " to "
        const val errConSuffix = " to"

        fun putActionImportSubKey(mainAndSubKeyCon: String): String {
            if(mainAndSubKeyCon.isEmpty()
            ) return String()
            return QuoteTool.splitBySurroundedIgnore(
                mainAndSubKeyCon,
                mainKeySeparator
            ).map {
                val subKeyToConList = QuoteTool.splitBySurroundedIgnore(
                    it,
                    subKeySeparator
                )
                val acImportMark = "${subKeySeparator}${VirtualSubKey.ACTION_IMPORT_CON.key}="
                val firstCon = subKeyToConList.firstOrNull()
                val firstConWithAcImportMark = when(firstCon.isNullOrEmpty()) {
                    true -> String()
                    else -> "${firstCon}${acImportMark}"
                }
                val secondLaterConList = when(subKeyToConList.isNotEmpty()){
                    true -> subKeyToConList.filterIndexed { index, s ->
                        index > 0
                    }
                    else -> emptyList()
                }
                listOf(
                    listOf(firstConWithAcImportMark),
                    secondLaterConList
                ).flatten().joinToString(subKeySeparator.toString())
            }.joinToString(mainKeySeparator.toString())
        }
    }

//    object MethodManager {
//
//        fun makeMethod(
//            jsMap: Map<String, String>
//        ): String {
//            val method =
//                jsMap.get(JsSubKey.METHOD.key)
//            val methodVarArgs = ArgsManager.makeVarArgs(
//                jsMap,
//                JsSubKey.METHOD_ARGS.key,
//            )
//            val isBlank = method.isNullOrEmpty()
//            return when(isBlank){
//                true -> String()
//                else -> ".${method}(${methodVarArgs})"
//            }
//        }
//    }

    object ArgsManager {
        enum class ArgsSetting(
            val str: String,
        ) {
            NO_QUOTE_PREFIX(noQuotePrefix),
        }

        fun makeVarArgs(
            jsMap: Map<String, String>,
            argsKey: String,
        ): String {
            val argsSeparator = '&'
            val argList = CmdClickMap.createMap(
                jsMap.get(argsKey),
                argsSeparator
            ).map{
                if(
                    it.first.isEmpty()
                ) return@map String()
                val argSrc = it.second
                NoQuoteHandler.make(argSrc)
            }.filter { it.isNotEmpty() }
            return argList.joinToString(",")
        }
    }

    enum class FuncFrag(
        val flag: String,
    ){
        VAR("var"),
    }

    enum class OnLogValue{
        OFF
    }

    enum class OnReturnValue {
        ON
    }

    object JsFuncManager {


        enum class LoopMethodFlag(
            val flag: String
        ) {
            JS_INTERFACE_PREFIX("js"),
            JS_CON_PREFIX(jsConPrefix)
        }

        const val loopArgsDefinitionErrMarkPrefix = "LOOP_METHOD_ARGS_DIFINITION_ERR: "
        const val errConSeparator = "\t"

        enum class EnableLoopMethodType(
            val suffix: String
        ){
            FILTER_METHOD_TYPE(".filter"),
            MAP_METHOD_TYPE(".map"),
            FOR_EACH_METHOD_TYPE(".forEach"),
        }

        enum class LoopMethodArgNameIndexToErrMsg(
            val index: Int,
            val errMsg: String,
        ){
            LOOP_ARG_NAMES(-1, "${JsSubKey.LOOP_ARG_NAMES.key} not difinition"),
            ELEMENT(0, "Element var name not difinition"),
            INDEX(1, "Index var name not difinition"),
            BOOL(2, "Bool var name not difinition"),
        }

        fun makeLoopArgsDefinitionErrMark(
            functionName: String?,
            con: String,
        ): String {
            return listOf(
                loopArgsDefinitionErrMarkPrefix,
                "${functionName}${errConSeparator}${con}"
            ).joinToString(String())
        }

        fun isJsCon(
            functionName: String?
        ): Boolean {
            return functionName?.startsWith(
                LoopMethodFlag.JS_CON_PREFIX.flag
            ) == true
        }

        fun howLoopMethod(
            functionName: String?,
        ): EnableLoopMethodType? {
            val isJsInterFace =
                functionName?.startsWith(LoopMethodFlag.JS_INTERFACE_PREFIX.flag) == true
            if(
                isJsInterFace
            ) return null
            return EnableLoopMethodType.values().firstOrNull {
                functionName?.endsWith(it.suffix) == true
            }
        }

        enum class DefaultLoopArgsName(
            val default: String
        ){
            EL("el"),
            INDEX("index"),
            BOOL("bool"),
        }
    }

    object NoQuoteHandler {
        fun make(str: String): String {
            val noQuotePrefix =
                ArgsManager.ArgsSetting.NO_QUOTE_PREFIX.str
            return when(str.startsWith(noQuotePrefix)){
                true -> str.removePrefix(noQuotePrefix)
                else -> "`${str}`"
            }
        }

        fun makeForVarReturn(str: String): String {
            val noQuotePrefix =
                ArgsManager.ArgsSetting.NO_QUOTE_PREFIX.str
            return when(str.startsWith(noQuotePrefix)){
                true -> "return ${str.removePrefix(noQuotePrefix)}"
                else -> "return `${str}`"
            }
        }
    }

    object JsVarManager {

        const val itPronoun = "it"
        const val escapeRunPrefix = "run"

        fun makeVarValue(
            jsMap: Map<String, String>
        ): String {
            val varValue = QuoteTool.trimBothEdgeQuote(
                jsMap.get(JsSubKey.VAR_VALUE.key)
            )
            return NoQuoteHandler.make(
                varValue
            )
        }
    }

    object AfterJsConMaker {

        const val ifSentence = "if"
        private const val ifAfterMinnerKey = "ifAfter"
        private const val afterValSeprator = ":"
        private const val ifAfterPrefix = "${ifAfterMinnerKey}${afterValSeprator}"
        const val afterJsConSeparator = '&'
        enum class SignalPrefix(
            val signal: String
        ){
            QUOTE("QUOTE:"),
        }

        fun make(
            jsMap: Map<String, String>,
        ): String {
            val varNameToJsConPairCon = jsMap.get(
                JsSubKey.AFTER_JS_CON.key
            )
            val varNameJsConPairList = CmdClickMap.createMap(
                varNameToJsConPairCon,
                afterJsConSeparator
            )
            val afterJsConList = varNameJsConPairList.mapIndexed {
                    index, jsConSrc ->
                val last2IndexPairList =
                    takeLast2IndexInThis(
                        varNameJsConPairList,
                        index,
                    )
                execMakeAfterJsCon(
                    jsConSrc,
                    last2IndexPairList,
                )
            }
            return afterJsConList.joinToString("\n")
        }

        private fun takeLast2IndexInThis(
            varNameJsConPairList: List<Pair<String, String>>,
            index: Int,
        ): List<Pair<String, String>> {
            return varNameJsConPairList.filterIndexed {
                    innerIndex, el ->
                if(
                    innerIndex >= index
                ) return@filterIndexed false
                !QuoteTool.trimBothEdgeQuote(
                    el.first
                ).startsWith(ifAfterPrefix)
            }.takeLast(2)
        }

        private fun execMakeAfterJsCon(
            varNameToJsCon: Pair<String, String>,
            last2IndexPairList: List<Pair<String, String>?>
        ): String {
            val varName = varNameToJsCon.first.trim()
            val jsCon = varNameToJsCon.second.trim()
            val isIfSentence =
                varName == ifSentence
            val afterAndValCon = QuoteTool.trimBothEdgeQuote(varName)
            val isIfAfter =
                afterAndValCon.startsWith(
                    ifAfterPrefix
                )
            return when(true){
                isIfSentence -> String()
                isIfAfter -> execMakeIfAfterJsCon(
                    afterAndValCon,
                    jsCon,
                    last2IndexPairList,
                )
                else -> execMakeAfterJsCon(
                    varName,
                    jsCon,
                    last2IndexPairList.lastOrNull(),
                )
            }
        }

        private fun execMakeIfAfterJsCon(
            afterAndValCon: String,
            jsCon: String,
            last2IndexPairList: List<Pair<String, String>?>
        ): String {
            val varName =
                afterAndValCon.removePrefix(ifAfterPrefix)
            val beforePreIfPair =
                last2IndexPairList.firstOrNull()
            val isBeforeIf =
                beforePreIfPair?.first == ifSentence
                        && last2IndexPairList.getOrNull(1)?.first != ifSentence
            if(
                !isBeforeIf
            ) return String()
            val funcTemplate = makeFuncTemplaceForAfterJsCon(
                beforePreIfPair
            )
            val logJsCon = makeLogConForAfterJsCon(varName)
            val ifAfterJsCon = makeAfterJsCon(
                varName,
                jsCon,
            )
            return makeIfCon(
                funcTemplate,
                logJsCon,
                ifAfterJsCon,
            )
        }

        private fun execMakeAfterJsCon(
            varName: String,
            jsCon: String,
            preIndexPair: Pair<String, String>?,
        ): String {
            val logJsCon = makeLogConForAfterJsCon(varName)
            val afterJsCon = makeAfterJsCon(
                varName,
                jsCon,
            )
            val funcTemplate = makeFuncTemplaceForAfterJsCon(
                preIndexPair
            )
            return makeIfCon(
                funcTemplate,
                logJsCon,
                afterJsCon,
            )
        }

        fun makeIfCon(
            funcTemplate: String,
            logJsCon: String,
            afterJsCon: String,
        ): String {
            val isIf = funcTemplate.contains(ifSentence)
            val afterJsConWithLog = listOf(
                logJsCon,
                afterJsCon
            ).joinToString("\n").let{
                when(isIf){
                    true -> it.replace("\n", "\n\t")
                    else -> it
                }
            }
            return funcTemplate.format(
                afterJsConWithLog
            )
        }

        private fun makeAfterJsCon(
            varName: String,
            jsConSrc: String,
        ): String {
            val disableVar = varName
                .contains(" ")
                    || varName.contains("\"")
                    || varName.contains("\'")
                    || varName.contains("`")
                    || varName.isEmpty()
            val varNameCon = when(disableVar) {
                true -> String()
                else -> listOf(
                    "var",
                    varName,
                    "="
                ).joinToString(" ")
            }
            val jsCon =
                makeJsConForAfterJsCon(jsConSrc)
            return listOf(
                varNameCon,
                UsePath.compExtend(
                    jsCon,
                    ";"
                ),
            ).joinToString(" ").trim()
        }

        private fun makeJsConForAfterJsCon(
            jsConSrc: String
        ): String {
            val quotePrefix = SignalPrefix.QUOTE.signal
            return when(true) {
                jsConSrc.startsWith(quotePrefix)
                -> "`${jsConSrc.removePrefix(quotePrefix)}`"
                else -> jsConSrc
            }
        }

        private fun makeFuncTemplaceForAfterJsCon(
            preIndexPair: Pair<String, String>?
        ): String {
            val preIndexVarName = preIndexPair?.first
            val isIf =
                preIndexVarName == ifSentence
            val ifCondition = when(isIf){
                true -> QuoteTool.trimBothEdgeQuote(preIndexPair?.second)
                else -> String()
            }
            return makeFuncTemplateForIf(
                ifCondition,
                null
            )
        }

        private fun makeLogConForAfterJsCon(
            varName: String,
        ): String {
            val logVarName = QuoteTool.trimBothEdgeQuote(
                varName
            )
            return listOf(
                "//_/_/_/ ${logVarName} start",
                "jsFileSystem.stdLog(\"${logVarName}\");",
            ).joinToString("\n")
        }
    }


    fun makeFuncTemplateForIf(
        ifCondition: String?,
        delayMiliSec: Int?,
    ): String {
        val insertJsCon =
            when(delayMiliSec == null){
            true -> {
                if(ifCondition.isNullOrEmpty()) "%s"
                else "\t%s"
            }
            else -> """
                |setTimeout(function(){
                |    %s
                |},${delayMiliSec})
                |""".trimMargin()
        }
        return when (
            ifCondition.isNullOrEmpty()
        ) {
            false -> """
            |if(${ifCondition}){ 
            |${insertJsCon} 
            |}   
            |""".trimMargin()
            else -> insertJsCon
        }
    }

    object OnlySubKeyMapForShortSyntax {

        enum class CommonOnlySubKey(
            val key: String
        ){
            WHEN("when"),
        }

        private val subKeyForCommon = listOf(
            JsSubKey.ID.key,
            JsSubKey.LOOP_ARG_NAMES.key,
            JsSubKey.AFTER.key,
            JsSubKey.DESC.key,
            JsSubKey.ON_LOG.key,
            JsSubKey.DELAY.key,
            CommonOnlySubKey.WHEN.key,
            VirtualSubKey.ACTION_IMPORT_CON.key,
            ActionImportManager.ActionImportKey.INVALID_AFTER_IN_AC_IMPORT.key,
            ActionImportManager.ActionImportKey.MISS_LAST_USE_VAR_KEY.key,
            ActionImportManager.ActionImportKey.MISS_LAST_RETURN_KEY.key,
            ActionImportManager.ActionImportKey.MISS_LAST_VAR_KEY.key,
            ActionImportManager.ActionImportKey.USE_VAR.key,
        )

        private val onlySubKeyListForVar =
            subKeyForCommon
        private val useKeyListForVar =
            onlySubKeyListForVar + listOf(
                JsActionsKey.JS_VAR.key,
                JsSubKey.IF.key,
                JsSubKey.VAR.key,
                JsSubKey.VAR_VALUE.key,
                JsSubKey.FUNC.key,
                JsSubKey.ARGS.key,
                OnlyVarSubKey.VAR_RETURN.key,
                OnlyVarSubKey.EXIT.key,
            )

        enum class  UseKeyForAfterJsConForVar(
            val key: String
        ) {
            VAR_VALUE(JsSubKey.VAR_VALUE.key),
            FUNC(JsSubKey.FUNC.key),
            VAR_RETURN(OnlyVarSubKey.VAR_RETURN.key),
            EXIT(OnlyVarSubKey.EXIT.key),
        }

        enum class  UseVarKeyForAfterJsConForVar(
            val key: String
        ) {
            VAR_VALUE(JsSubKey.VAR_VALUE.key),
            FUNC(JsSubKey.FUNC.key),
        }


        private val onlySubKeyListForFunc = subKeyForCommon + listOf(
//            JsSubKey.ON_RETURN.key,
            JsSubKey.IF.key,
        )
        private val useKeyListForFunc = onlySubKeyListForFunc + listOf(
            JsActionsKey.JS_FUNC.key,
            JsSubKey.FUNC.key,
            JsSubKey.ARGS.key,
        )

        fun filterForVar(
            subKeyToConPairList: List<Pair<String, String>>?,
        ): List<Pair<String, String>>? {
            return subKeyToConPairList?.filter {
                val subKeyName = it.first
                useKeyListForVar.contains(subKeyName)
            }
        }

        fun filterForFunc(
            subKeyToConPairList: List<Pair<String, String>>?,
        ): List<Pair<String, String>>? {
            return subKeyToConPairList?.filter {
                val subKeyName = it.first
                useKeyListForFunc.contains(subKeyName)
            }
        }

        fun extractForVar(
            subKeyToConPairList: List<Pair<String, String>>?,
        ): Pair<
                Map<String, String>,
                List<Pair<String, String>>?
                > {
            return extractOnlySubKeyPair(
                subKeyToConPairList,
                onlySubKeyListForVar
            )
        }

        fun extractForFunc(
            subKeyToConPairList: List<Pair<String, String>>?,
        ): Pair<
                Map<String, String>,
                List<Pair<String, String>>?
                > {
            return extractOnlySubKeyPair(
                subKeyToConPairList,
                onlySubKeyListForFunc
            )
        }

        private fun extractOnlySubKeyPair(
            subKeyToConPairList: List<Pair<String, String>>?,
            targetSubKeyList: List<String>
        ): Pair<
                Map<String, String>,
                List<Pair<String, String>>?
                > {
            val defaultBlankMap = emptyMap<String, String>()
            if(
                subKeyToConPairList.isNullOrEmpty()
            ) return defaultBlankMap to subKeyToConPairList
            val targetSubKeyToConMap = subKeyToConPairList.filter {
                    keyToCon ->
                val subKeyName = keyToCon.first
                if (
                    targetSubKeyList.contains(subKeyName)
                ) return@filter true
               false
            }.toMap()
            if(
                targetSubKeyToConMap.isEmpty()
            ) return defaultBlankMap to subKeyToConPairList
            val subKeyToConPairListWithoutAfter = subKeyToConPairList.filter {
                val subKeyName = it.first
                !targetSubKeyToConMap.containsKey(subKeyName)
            }
            return targetSubKeyToConMap to subKeyToConPairListWithoutAfter
        }
    }
}