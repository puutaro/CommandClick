package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.map.CmdClickMap
import java.lang.ref.WeakReference

class JsMap(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {

    companion object {
        fun createMapFromCon(
            mapCon: String,
            separator: String,
        ): Map<String, String>? {
            return separator.getOrNull(0)?.let {
                CmdClickMap.createMap(
                    mapCon,
                    it
                ).toMap()
            }
        }
    }

    @JavascriptInterface
    fun get(
        mapCon: String,
        separator: String,
        key: String
    ): String {
        val keyCon = separator.getOrNull(0)?.let {
            CmdClickMap.createMap(
                mapCon,
                it
            ).toMap().get(key) ?: String()
        } ?: String()
        return keyCon
    }

    @JavascriptInterface
    fun update(
        mapCon: String,
        separator: String,
        keyValueStr: String,
    ): String {
        val separatorChar = separator.getOrNull(0)
            ?: return mapCon
        val keyValList = keyValueStr.split("=")
        val targetKey = keyValList.firstOrNull()
            ?: return mapCon
        val targetValue = keyValList.getOrNull(1)
            ?: String()
        val updateMap = CmdClickMap.createMap(
            mapCon,
            separatorChar
        ).toMap().toMutableMap()
        updateMap.put(
            targetKey,
            targetValue
        )
        val updatedMapCon = updateMap.map {
            "${it.key}=${it.value}"
        }.joinToString(separator)
        return updatedMapCon
    }
}