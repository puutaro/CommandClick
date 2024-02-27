package com.puutaro.commandclick.proccess.js_macro_libs.common_libs

import com.puutaro.commandclick.util.map.CmdClickMap

object JsActionDataMapKeyObj {

    enum class JsActionDataMapKey(
        val key: String
    ) {
        TYPE("type"),
        JS_CON("jsCon"),
        MACRO_ARGS("macroArgs"),
    }

    enum class JsActionDataTypeKey(
        val key: String
    ) {
        MACRO("macro"),
        JS_CON("js")
    }

    fun getJsMacroArgs(
        jsActionMap: Map<String, String>?,
        separator: Char = '&'
    ): Map<String, String>? {
        return jsActionMap?.get(
            JsActionDataMapKey.MACRO_ARGS.key
        )?.let {
            CmdClickMap.createMap(
                it,
                separator
            )
        }?.toMap()
    }

}