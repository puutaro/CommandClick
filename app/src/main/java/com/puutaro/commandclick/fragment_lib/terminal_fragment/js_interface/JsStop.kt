package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class JsStop(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun how(): String {
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        return terminalViewModel.isStop.toString()
    }
}