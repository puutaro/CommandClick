package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.net.Uri
import android.view.View
import android.webkit.*
import com.puutaro.commandclick.fragment.TerminalFragment


class WebChromeClientSetter {
    companion object {
        fun set(
            terminalFragment: TerminalFragment
        ){

            val binding = terminalFragment.binding
            val progressBar = binding.progressBar

            binding.terminalWebView.setWebChromeClient(object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE)
                    } else {
                        progressBar.setVisibility(View.VISIBLE)
                        progressBar.setProgress(newProgress)
                    }
                }
                
                override fun onShowFileChooser(
                    mWebView:WebView,
                    filePathCallback:ValueCallback<Array<Uri>>,
                    fileChooserParams: WebChromeClient.FileChooserParams
                ):Boolean {

                    if(!terminalFragment.isVisible) return false
                    val listener =
                        terminalFragment.context as? TerminalFragment.OnFileChooseListener
                    listener?.onFileCooose(
                        filePathCallback,
                        fileChooserParams
                    )
                    return true
                }
            })
        }

    }
}
