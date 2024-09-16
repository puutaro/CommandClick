package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsVar(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun echo(
        valueStr: String
    ): String {

        /*
        Echo valueStr contents
        This exist for debug
        */

        return valueStr
    }
}