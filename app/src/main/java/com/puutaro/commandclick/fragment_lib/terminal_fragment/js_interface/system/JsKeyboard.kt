package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment

class JsKeyboard(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context

    @JavascriptInterface
    fun show(){
        val imm = terminalFragment.activity?.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        val webView = terminalFragment.webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        ) ?: return
        if(!webView.isVisible) return
        val show = imm.showSoftInput(webView, 0)
        Toast.makeText(
            context,
            "show: ${show}",
            Toast.LENGTH_SHORT
        ).show()
        if (!show) {
            webView.setFocusable(true)
            webView.setFocusableInTouchMode(true)
            val focus: Boolean = webView.requestFocus()
            if (focus) {
                imm.showSoftInput(webView, 0)
            }
        }
    }
}