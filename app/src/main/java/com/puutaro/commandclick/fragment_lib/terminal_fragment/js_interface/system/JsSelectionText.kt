package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsSelectionText(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun updateRegisterText(
        text: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.selectionText = text
    }

    @JavascriptInterface
    fun updateSelectionTextView(updateText: String){
        val terminalFragment = terminalFragmentRef.get() ?: return
        val context = terminalFragment.context ?: return
        val listener = context as TerminalFragment.OnUpdateSelectionTextViewListenerForTerm
        listener.onUpdateSelectionTextViewForTerm(updateText)
    }
}