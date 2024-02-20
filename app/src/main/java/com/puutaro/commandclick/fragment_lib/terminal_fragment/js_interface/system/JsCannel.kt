package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsCancel(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun cancel(){
        val listener =
            context as? TerminalFragment.onBackstackWhenTermLongInRestartListener
                ?: return
        listener.onBackstackWhenTermLongInRestart()
    }
}