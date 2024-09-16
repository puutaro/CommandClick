package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsCmdValSaveAndBack(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun run_S(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val listener = context as? TerminalFragment.OnCmdValSaveAndBackListenerForTerm
        listener?.onSettingOkButtonForTerm()
    }
}