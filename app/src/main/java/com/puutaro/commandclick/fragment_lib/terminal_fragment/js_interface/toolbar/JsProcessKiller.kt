package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsProcessKiller(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun kill_S(
        fannelName: String,
    ){
        /*
        ## Description

        Kill process by dialog

        ## Corresponding macro

        -> [KILL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#kill)

        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return
        AppProcessManager.killDialog(
            terminalFragment,
//            currentAppDirPath,
            fannelName
        )
    }
}
