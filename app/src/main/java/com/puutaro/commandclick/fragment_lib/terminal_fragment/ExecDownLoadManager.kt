package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.ActivityNotFoundException
import android.content.Context
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
            try {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                terminalFragment.startActivity(i)
                Toast.makeText(
                    context,
                    "Select download browser",
                    Toast.LENGTH_SHORT
                ).show()
            } catch(e: Exception){
                openChorme(
                    context,
                    webView.url,
                )
            }
        })
    }

    private fun openChorme(
        context: Context?,
        urlString: String?,
    ){
        if(
            context == null
        ) return
        if(
            urlString.isNullOrEmpty()
        ) return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            context.startActivity(intent)
            Toast.makeText(
                context,
                "Select download link by chrome",
                Toast.LENGTH_SHORT
            ).show()
        } catch (ex: ActivityNotFoundException) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }
}
