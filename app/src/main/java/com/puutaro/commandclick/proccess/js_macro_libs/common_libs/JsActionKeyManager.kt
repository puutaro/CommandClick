package com.puutaro.commandclick.proccess.js_macro_libs.common_libs

import com.puutaro.commandclick.util.map.CmdClickMap


object JsActionKeyManager {

    enum class JsActionsKey(
        val key: String
    ) {
        JS("js"),
        JS_CON("jsCon"),
        JS_PATH("jsPath"),
        JS_IMPORT("jsImport"),
        JS_TSV_IMPORT("jsTsvImport"),
        JS_ACTIONS_IMPORT("jsActionsImport"),
    }

    enum class JsSubKey(
        val key: String
    ) {
        FUNC("func"),
        METHOD("method"),
        ID("id"),
        ARGS("args"),
        LOOP_ARG_NAMES("loopArgNames"),
        ON_RETURN("onReturn"),
        IF("if"),
        VAR("var"),
        AFTER("after"),
        DESC("desc"),
        EXIT_JUDGE("exitJudge"),
        EXIT_TOAST("exitToast"),
        START_TOAST("startToast"),
        END_TOAST("endToast"),
        ON_LOG("onLog")
    }

    object ArgsManager {
        enum class ARGS_SETTING(
            val str: String,
        ) {
            NO_QUOTE_PREFIX("NO_QUOTE:"),
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
        VAR_PREFIX("var"),
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
            JS_CON_PREFIX("con:")
        }

        private enum class PipUnableFuncSuffix(
            val suffix: String
        ){
            MACRO("_S"),
        }

        fun howPipAbleFunc(
            firstFuncName: String?
        ): Boolean {
            if(
                firstFuncName.isNullOrEmpty()
            ) return false
            return PipUnableFuncSuffix.values().any {
                !firstFuncName.endsWith(it.suffix)
            }
        }



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


    object JsPathManager {
        enum class Flag(
            val flag: String
        ) {
            JS_INTERFACE_PREFIX("js"),
            JS_CON_PREFIX("con:"),
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