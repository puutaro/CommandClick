package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.TerminalFragment

class JsMonitorSizing(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context

    @JavascriptInterface
    fun change(){
        val linearLayoutParam =
            terminalFragment.view?.layoutParams as? LinearLayout.LayoutParams
                ?: return
        if(
            linearLayoutParam.weight == 0f
        ) return
        val listener = context as? TerminalFragment.OnMonitorSizeChangeingForTerm
            ?: return
        listener.onMonitorSizeChangeingForTerm(
            terminalFragment.fannelInfoMap
        )
    }
}