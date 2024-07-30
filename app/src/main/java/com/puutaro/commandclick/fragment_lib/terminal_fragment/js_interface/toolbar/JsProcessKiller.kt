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
        /*
        Kill process by dialog

        ### Corresponding macro

        -> [KILL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#kill)

        */
        AppProcessManager.killDialog(
            terminalFragment,
            currentAppDirPath,
            fannelName
        )
    }
}
