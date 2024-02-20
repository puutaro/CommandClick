package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class JsMonitorRefresh(
    terminalFragment: TerminalFragment
) {

    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    @JavascriptInterface
    fun refresh(){
        TermRefresh.refresh(
            terminalViewModel.currentMonitorFileName
        )
    }
}