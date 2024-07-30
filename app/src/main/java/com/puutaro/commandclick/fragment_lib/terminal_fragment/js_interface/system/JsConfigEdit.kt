package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ConfigEdit

class JsConfigEdit(
    private val terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun edit_S() {

        /*
        Edit Config in CommandClick
        */

        ConfigEdit.edit(terminalFragment)
    }
}