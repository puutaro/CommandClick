package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsBackstack(
    terminalFragment: TerminalFragment
) {
    val activity = terminalFragment.activity

    @JavascriptInterface
    fun count(): Int {
        val backstackCount = activity
            ?.supportFragmentManager
            ?.backStackEntryCount
            ?: 0
        return backstackCount
    }
}