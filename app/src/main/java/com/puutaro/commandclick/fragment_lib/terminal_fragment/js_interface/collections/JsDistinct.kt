package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsDistinct(terminalFragment: TerminalFragment) {

    @JavascriptInterface
    fun distinct(
        con: String,
        separator: String,
    ): String {
        return con.split(separator)
            .sorted()
            .distinct()
            .joinToString(separator)
    }
}