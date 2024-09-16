package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsDiff(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun diff(
        baseCon: String,
        compareCon: String,
        separator: String,
    ): String {
        val baseConList = baseCon.split(separator)
        val diffCon = try {
            compareCon.split(separator).filterIndexed { compareIndex, compareEl ->
                val baseHitLine = baseConList.getOrNull(compareIndex)
                    ?: return@filterIndexed true
                val isDiff = compareEl != baseHitLine
                isDiff
            }.joinToString(separator)
        } catch (e: Exception){
            return String()
        }
        return diffCon
    }

    @JavascriptInterface
    fun uniq(
        baseCon: String,
        compareCon: String,
        separator: String,
    ): String {
        val baseConList = baseCon.split(separator)
        val uniqCon = try {
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
        return uniqCon
    }

}