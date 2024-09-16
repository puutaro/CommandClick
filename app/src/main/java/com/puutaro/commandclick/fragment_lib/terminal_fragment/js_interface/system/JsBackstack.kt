package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsBackstack(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun count(): Int {
        val terminalFragment = terminalFragmentRef.get()
            ?: return 0
        val activity = terminalFragment.activity
        val backstackCount = activity
            ?.supportFragmentManager
            ?.backStackEntryCount
            ?: 0
        return backstackCount
    }
}