package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsMonitorRefresh(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun refresh() {

        /*
        ## Description

        Refresh current monitor

        ## Corresponding macro

        -> [REFRESH_MONITOR](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#refresh_monitor)

        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        TermRefresh.refresh(
            terminalViewModel.currentMonitorFileName
        )
    }
}
