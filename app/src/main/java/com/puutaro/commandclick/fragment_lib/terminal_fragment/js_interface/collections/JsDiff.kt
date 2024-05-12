package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsDiff(terminalFragment: TerminalFragment) {

    @JavascriptInterface
    fun diff(
        baseCon: String,
        compareCon: String,
        separator: String,
    ): String {
        val baseConList = baseCon.split(separator)
        return try {
            compareCon.split(separator).filterIndexed { compareIndex, compareEl ->
                val baseHitLine = baseConList.getOrNull(compareIndex)
                    ?: return@filterIndexed true
                val isDiff = compareEl != baseHitLine
                isDiff
            }.joinToString(separator)
        } catch (e: Exception){
            return String()
        }
    }

    @JavascriptInterface
    fun uniq(
        baseCon: String,
        compareCon: String,
        separator: String,
    ): String {
        val baseConList = baseCon.split(separator)
        return try {
            compareCon.split(separator).filter { compareEl ->
                if(
                    compareEl.isEmpty()
                ) return@filter false
                val isNotContain = !baseConList.contains(compareEl)
                isNotContain
            }.joinToString(separator)
        } catch (e: Exception){
            return String()
        }
    }

}