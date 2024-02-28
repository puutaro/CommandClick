package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsWebViewDialogManager(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun dismiss(){
        terminalFragment.webViewDialogInstance?.dismiss()
    }
}