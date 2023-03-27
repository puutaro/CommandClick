package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.JavaScriptLoadUrl

class JsUrl(
    terminalFragment: TerminalFragment
) {
    private val binding = terminalFragment.binding

    @JavascriptInterface
    fun makeJsUrl(
        jsPath: String
    ): String {
        return JavaScriptLoadUrl.make(jsPath)
            ?: String()
    }
}