package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsSelectionText(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun updateText(
        text: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.selectionText = text
    }
}