package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsCon(
    terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun sortFromThis(
        con: String,
        thisLine: String,
    ): String {
        val conList = con.split("\n")
        val thisIndex = conList.indexOf(thisLine)
        if(
            thisIndex <= 0
        ) return conList.joinToString("\n")
        val subConListUntilThis = conList.filterIndexed { index, s ->
            index < thisIndex
        }
        val subConListFromThis = conList.filterIndexed { index, s ->
            index >= thisIndex
        }
        val conListFromThis = subConListFromThis + subConListUntilThis
        val conBySrotedFromThis = conListFromThis.joinToString("\n")
        return conBySrotedFromThis
    }
}