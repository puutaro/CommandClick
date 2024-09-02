package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsArgs(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun get(): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val jsArguments = terminalViewModel.jsArguments + "\t"
        terminalViewModel.jsArguments = String()
        return jsArguments
    }

    @JavascriptInterface
    fun set(args: String) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.jsArguments = args
    }
}