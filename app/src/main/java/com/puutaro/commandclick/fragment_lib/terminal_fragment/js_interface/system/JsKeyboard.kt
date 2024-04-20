package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment


class JsKeyboard(
    private val terminalFragment: TerminalFragment
) {

    val context = terminalFragment.context
    val activity = terminalFragment.activity

    @JavascriptInterface
    fun show(){
        val focusWebView = getFocusWebView()
        val isFocus = focusWebView.requestFocus()
        if (!isFocus) return
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as? InputMethodManager
        imm?.showSoftInput(focusWebView,0)
    }

    private fun getFocusWebView(): WebView {
        val webViewDialog = terminalFragment.webViewDialogInstance
        val focusPocketWebView = terminalFragment.webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        )
        if(
            webViewDialog != null
            && webViewDialog.isShowing
            && focusPocketWebView != null
            && focusPocketWebView.isVisible
        ) {
            return focusPocketWebView
        }
        return terminalFragment.binding.terminalWebView
    }
}