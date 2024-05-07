package com.puutaro.commandclick.proccess.js_macro_libs.common_libs

import TsvImportManager
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap


object JsActionKeyManager {

    enum class JsActionsKey(
        val key: String
    ) {
        JS("js"),
        JS_VAR("var"),
        JS_CON("jsCon"),
        JS_PATH("jsPath"),
        JS_IMPORT("jsImport"),
        TSV_IMPORT("tsvImport"),
        ACTION_IMPORT("actionImport"),
        OVERRIDE("override"),
        REPLACE("replace")
    }

    enum class CommonPathKey(
        val key: String
    ) {
        PATH("path"),
        USE(TsvImportManager.tsvImportUsePhrase),
    }

    enum class JsSubKey(
        val key: String
    ) {
        FUNC("func"),
        METHOD("method"),
        METHOD_ARGS("methodArgs"),
        OVERRIDE("override"),
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
        EXIT_JUDGE("exitJudge"),
        EXIT_TOAST("exitToast"),
        START_TOAST("startToast"),
        END_TOAST("endToast"),
        ON_LOG("onLog"),
    }

    const val noQuotePrefix = "NO_QUOTE:"
    private val jsConPrefix = "con:"
    const val actionImportVirtualSubKey = "actionImportCon"


    object ActionImportManager {

        private const val mainKeySeparator = '|'
        private const val subKeySeparator = '?'
        fun putActionImportSubKey(mainAndSubKeyCon: String): String {
            return QuoteTool.splitBySurroundedIgnore(
                mainAndSubKeyCon,
                mainKeySeparator
            ).map {
                val subKeyToConList = QuoteTool.splitBySurroundedIgnore(
                    it,
                    subKeySeparator
                )
                val acImportMark = "${subKeySeparator}${actionImportVirtualSubKey}="
                val firstCon = subKeyToConList.firstOrNull()?: String()
                val firstConWithAcImportMark = "${firstCon}${acImportMark}"
                val secondLaterConList = when(subKeyToConList.size > 0){
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

//        fun extractActionImportMarkPair(
//            subKeyToConPairList: List<Pair<String, String>>?,
//        ): Pair<
//                Map<String, String>,
//                List<Pair<String, String>>?
//                > {
//            val defaultBlankMap = emptyMap<String, String>()
//            if(
//                subKeyToConPairList.isNullOrEmpty()
//            ) return defaultBlankMap to subKeyToConPairList
//            val actionImportSubKeyToConMap = subKeyToConPairList.filter {
//                    keyToCon ->
//                val subKeyName = keyToCon.first
//                if (
//                    subKeyName == actionImportMarkSubKey
//                ) return@filter true
//                false
//            }.toMap()
//            if(
//                actionImportSubKeyToConMap.isEmpty()
//            ) return defaultBlankMap to subKeyToConPairList
//            val subKeyToConPairListWithoutAfter = subKeyToConPairList.filter {
//                val subKeyName = it.first
//                val isNotActinImportMarkKey =
//                    subKeyName == actionImportMarkSubKey
//                isNotActinImportMarkKey
//            }
//            return actionImportSubKeyToConMap to subKeyToConPairListWithoutAfter
//        }

    }

    object MethodManager {

        fun makeMethod(
            jsMap: Map<String, String>
        ): String {
            val method =
                jsMap.get(JsSubKey.METHOD.key)
            val methodVarArgs = ArgsManager.makeVarArgs(
                jsMap,
                JsSubKey.METHOD_ARGS.key,
            )
            val isBlank = method.isNullOrEmpty()
            return when(isBlank){
                true -> String()
                else -> ".${method}(${methodVarArgs})"
            }
        }
    }

    object OverrideManager {
        enum class NoOverrideJsSubKey(val key: String){
            ID(JsSubKey.ID.key),
            AFTER(JsSubKey.AFTER.key),
//            OVERRIDE(JsSubKey.OVERRIDE.key),
        }

        fun makeOverrideMap(
            overrideMapList: List<Map<String, String>>,
            id: String,
        ): Map<String, String> {
            val idKeyName =
                JsSubKey.OVERRIDE.key
            val overrideMapSrc = overrideMapList.lastOrNull { map ->
                val overrideIdList = map.get(
                    idKeyName
                )?.split("&")
                    ?: return@lastOrNull false
                overrideIdList.contains(id)
            } ?: return emptyMap()
            return overrideMapSrc.map {
                val keyName = it.key
                val disableOverride =
                    JsActionKeyManager.OverrideManager.NoOverrideJsSubKey.values()
                        .firstOrNull {
                            it.key == keyName
                        } != null
                if(
                    disableOverride
                ) return@map String() to String()
                keyName to it.value
            }.toMap().filterKeys { it.isNotEmpty() }
        }
    }

    object ArgsManager {
        enum class ARGS_SETTING(
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
            FILTER_METHOD_SUFFIX_(".filter"),
            MAP_METHOD_SUFFIX_(".map"),
            FOR_EACH_METHOD_SUFFIX_(".forEach"),
            JS_CON_PREFIX(jsConPrefix)
        }

        private enum class PipUnableFuncSuffix(
            val suffix: String
        ){
            MACRO("_S"),
        }

//        fun howPipAbleFunc(
//            firstFuncName: String?
//        ): Boolean {
//            if(
//                firstFuncName.isNullOrEmpty()
//            ) return false
//            return true
////            PipUnableFuncSuffix.values().any {
////                !firstFuncName.endsWith(it.suffix)
////            }
//        }



        fun isJsCon(
            functionName: String?
        ): Boolean {
            return functionName?.startsWith(
                LoopMethodFlag.JS_CON_PREFIX.flag
            ) == true
        }

        fun isLoopMethod(
            functionName: String?,
            loopMethodSuffix: String,
        ): Boolean {
            val isNotJsInterFace =
                functionName?.startsWith(LoopMethodFlag.JS_INTERFACE_PREFIX.flag) != true
            val isLoopMethodStr =
                functionName?.endsWith(loopMethodSuffix) == true
            return isNotJsInterFace && isLoopMethodStr
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
                ArgsManager.ARGS_SETTING.NO_QUOTE_PREFIX.str
            return when(str.startsWith(noQuotePrefix)){
                true -> str.removePrefix(noQuotePrefix)
                else -> "`${str}`"
            }
        }

        fun makeForVarReturn(str: String): String {
            val noQuotePrefix =
                ArgsManager.ARGS_SETTING.NO_QUOTE_PREFIX.str
            return when(str.startsWith(noQuotePrefix)){
                true -> "return ${str.removePrefix(noQuotePrefix)}"
                else -> "return `${str}`"
            }
        }
    }

    object JsVarManager {

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

    object JsConManager {
        enum class Flag(
            val flag: String
        ) {
            JS_CON_PREFIX(jsConPrefix),
        }
    }


    object JsPathManager {
        enum class Flag(
            val flag: String
        ) {
            JS_INTERFACE_PREFIX("js"),
        }

        fun isJsInterface(
            jsPathCon: String
        ): Boolean {
            return jsPathCon.startsWith(
                Flag.JS_INTERFACE_PREFIX.flag
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
                ifCondition
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
        ifCondition: String?
    ): String {
        return when (
            ifCondition.isNullOrEmpty()
        ) {
            false -> """
            |if(${ifCondition}){ 
            |    %s 
            |}   
            |""".trimMargin()

            else -> "%s"
        }
    }

    object OnlySubKeyMapForShortSyntax {


        private val subKeyForCommon = listOf(
            JsSubKey.ID.key,
            JsSubKey.LOOP_ARG_NAMES.key,
            JsSubKey.AFTER.key,
            JsSubKey.DESC.key,
            JsSubKey.EXIT_JUDGE.key,
            JsSubKey.EXIT_TOAST.key,
            JsSubKey.START_TOAST.key,
            JsSubKey.END_TOAST.key,
            JsSubKey.ON_LOG.key,
            JsSubKey.METHOD.key,
            JsSubKey.METHOD_ARGS.key,
            actionImportVirtualSubKey,
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
            )


        private val onlySubKeyListForFunc = subKeyForCommon + listOf(
            JsSubKey.ON_RETURN.key,
            JsSubKey.IF.key,
        )
        private val useKeyListForFunc = onlySubKeyListForFunc + listOf(
            JsActionsKey.JS_PATH.key,
            JsSubKey.IF.key,
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