package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment

class JsCmdValSaveAndBack(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun run_S(){
        val listener = context as? TerminalFragment.OnCmdValSaveAndBackListenerForTerm
        listener?.onSettingOkButtonForTerm()
    }
}