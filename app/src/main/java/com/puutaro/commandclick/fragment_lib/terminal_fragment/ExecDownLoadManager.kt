package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Intent
import android.net.Uri
import android.webkit.DownloadListener
import android.webkit.WebView
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment


object ExecDownLoadManager {
    fun set(
        terminalFragment: TerminalFragment,
        webView: WebView,
    ){
        val context = terminalFragment.context
        webView.setDownloadListener(DownloadListener {
                url, userAgent, contentDisposition, mimetype, contentLength ->
            Toast.makeText(
                context,
                "Select download browser",
                Toast.LENGTH_SHORT
            ).show()
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            terminalFragment.startActivity(i)
        })
    }
}
