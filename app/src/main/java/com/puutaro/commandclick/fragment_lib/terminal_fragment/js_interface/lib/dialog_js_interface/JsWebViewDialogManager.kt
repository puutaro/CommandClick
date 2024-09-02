package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsWebViewDialogManager(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun dismiss(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.pocketWebViewManager?.stopWebView(false)
//        terminalFragment.pocketWebViewManager.dismiss()
//        terminalFragment.webViewDialogInstance = null
    }
}