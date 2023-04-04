package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl

class JsUrl(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun makeJsUrl(
        jsPath: String
    ): String {
        return JavaScriptLoadUrl.make(jsPath)
            ?: String()
    }

    @JavascriptInterface
    fun loadUrl(
        urlStr: String
    ) {
        BroadCastIntent.send(
            terminalFragment,
            urlStr
        )
    }
}