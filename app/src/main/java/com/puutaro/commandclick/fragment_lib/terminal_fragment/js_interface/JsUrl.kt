package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ScrollPosition
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        CoroutineScope(Dispatchers.Main).launch{
            ScrollPosition.save(terminalFragment.activity)
        }
        BroadCastIntent.send(
            terminalFragment,
            urlStr
        )
    }

    @JavascriptInterface
    fun sLoadUrl(
        urlStr: String
    ) {
        BroadCastIntent.send(
            terminalFragment,
            urlStr
        )
    }
}