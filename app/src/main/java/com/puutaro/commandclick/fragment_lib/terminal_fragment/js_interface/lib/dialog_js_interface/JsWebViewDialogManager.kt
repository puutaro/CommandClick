package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsWebViewDialogManager(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun dismiss(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.pocketWebViewManager?.stopWebView()
//        terminalFragment.pocketWebViewManager.dismiss()
//        terminalFragment.webViewDialogInstance = null
    }

    @JavascriptInterface
    fun visibleSelectionBar(isShow: Boolean){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.pocketWebViewManager?.textSelectionHideShow(isShow)
    }
}