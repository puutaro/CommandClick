package com.puutaro.commandclick.proccess.js_macro_libs.common_libs

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
    }

    enum class JsSubKey(
        val key: String
    ) {
        FUNC("func"),
        METHOD("method"),
        OVERRIDE("override"),
        ID("id"),
        ARGS("args"),
        PREFIX("prefix"),
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
        ON_LOG("onLog")
    }

    private val noQuotePrefix = "NO_QUOTE:"
    private val jsConPrefix = "con:"

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
            jsMap: Map<String, String>
        ): String {
            val noQuotePrefix =
                ARGS_SETTING.NO_QUOTE_PREFIX.str
            val argsSeparator = '&'
            val argList = CmdClickMap.createMap(
                jsMap.get(JsSubKey.ARGS.key),
                argsSeparator
            ).map{
                val argSrc = it.second
                if(
                    argSrc.startsWith(noQuotePrefix)
                ) return@map argSrc.removePrefix(noQuotePrefix)
                "`${argSrc}`"
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

    object JsVarManager {

        fun makeVarValue(
            jsMap: Map<String, String>
        ): String {
            val noQuotePrefix =
                ArgsManager.ARGS_SETTING.NO_QUOTE_PREFIX.str
            val varValue = QuoteTool.trimBothEdgeQuote(
                jsMap.get(JsSubKey.VAR_VALUE.key)
            )
            return when(varValue.startsWith(noQuotePrefix)){
                true -> varValue.removePrefix(noQuotePrefix)
                else -> "`${varValue}`"
            }
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
}