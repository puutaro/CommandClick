package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsVar(
    terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun echo(
        valueStr: String
    ): String {
        return valueStr
    }
}