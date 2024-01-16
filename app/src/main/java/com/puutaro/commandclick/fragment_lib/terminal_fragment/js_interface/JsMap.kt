package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.map.CmdClickMap

class JsMap(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun get(
        mapCon: String,
        separator: String,
        key: String
    ): String {
        return CmdClickMap.createMap(
            mapCon,
            separator
        ).toMap().get(key) ?: String()

    }

    @JavascriptInterface
    fun update(
        mapCon: String,
        separator: String,
        keyValueStr: String,
    ): String {
        val keyValList = keyValueStr.split("=")
        val targetKey = keyValList.firstOrNull()
            ?: return mapCon
        val targetValue = keyValList.getOrNull(1)
            ?: String()
        val updateMap = CmdClickMap.createMap(
            mapCon,
            separator
        ).toMap().toMutableMap()
        updateMap.put(
            targetKey,
            targetValue
        )
        return updateMap.map {
            "${it.key}=${it.value}"
        }.joinToString(separator)

    }
}