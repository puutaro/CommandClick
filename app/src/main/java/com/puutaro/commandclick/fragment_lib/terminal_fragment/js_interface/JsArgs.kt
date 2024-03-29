package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class JsArgs(
    terminalFragment: TerminalFragment
) {
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    @JavascriptInterface
    fun get(): String {
        val jsArguments = terminalViewModel.jsArguments + "\t"
        terminalViewModel.jsArguments = String()
        return jsArguments
    }

    @JavascriptInterface
    fun set(args: String) {
        terminalViewModel.jsArguments = args
    }
}