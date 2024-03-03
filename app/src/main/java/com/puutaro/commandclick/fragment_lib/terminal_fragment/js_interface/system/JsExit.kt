package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl

class JsExit(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context

    @JavascriptInterface
    fun exit(){
        val exitConSrc = """
            exitZero();
        """.trimIndent()
        val exitCon = JavaScriptLoadUrl.makeFromContents(
            context,
            exitConSrc.split("\n"),
        ) ?: return
        BroadCastIntent.sendUrlCon(
            terminalFragment,
            exitCon
        )
    }

    @JavascriptInterface
    fun exitWithToast(
        message: String,
    ){
        val exitConSrc = """
            jsToast.short("${message}");
            exitZero();
        """.trimIndent()
        val exitCon = JavaScriptLoadUrl.makeFromContents(
            context,
            exitConSrc.split("\n"),
        ) ?: return
        BroadCastIntent.sendUrlCon(
            terminalFragment,
            exitCon
        )
    }
}