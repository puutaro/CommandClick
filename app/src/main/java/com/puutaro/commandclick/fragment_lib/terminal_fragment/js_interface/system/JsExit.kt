package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsExit(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun exit(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val exitConSrc = """
            exitZero();
        """.trimIndent()
        val exitCon = JavaScriptLoadUrl.makeFromContents(
            context,
            exitConSrc.split("\n"),
        ) ?: return
        BroadCastIntent.sendUrlCon(
            context,
            exitCon
        )
    }

    @JavascriptInterface
    fun exitWithToast(
        message: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val exitConSrc = """
            jsToast.short("${message}");
            exitZero();
        """.trimIndent()
        val exitCon = JavaScriptLoadUrl.makeFromContents(
            context,
            exitConSrc.split("\n"),
        ) ?: return
        BroadCastIntent.sendUrlCon(
            context,
            exitCon
        )
    }
}