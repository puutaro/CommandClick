package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Context
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
        return JavaScriptLoadUrl.make(
            terminalFragment.context,
            jsPath
        ) ?: String()
    }

    @JavascriptInterface
    fun loadUrl(
        urlStr: String
    ) {
        //            TODO register scrollY position
        BroadCastIntent.send(
            terminalFragment,
            urlStr
        )
    }
}