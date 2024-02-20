package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.AppProcessManager

class JsProcessKiller(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun kill_S(
        currentAppDirPath: String,
        fannelName: String,
    ){
        AppProcessManager.killDialog(
            terminalFragment,
            currentAppDirPath,
            fannelName
        )
    }
}
