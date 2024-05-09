package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.webkit.WebView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WebViewJsDialog

object PocketWebViewUrlLoader {
    fun load(
        terminalFragment: TerminalFragment,
        url: String?,
    ){
        val webView = terminalFragment.webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        ) ?: return
        if(
            url.isNullOrEmpty()
        ) return
        WebViewJsDialog.loadUrlHandler(
            terminalFragment,
            webView,
            url,
        )

    }
}