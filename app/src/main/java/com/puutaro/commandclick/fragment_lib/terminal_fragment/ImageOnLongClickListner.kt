package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.puutaro.commandclick.fragment.TerminalFragment

class ImageOnLongClickListner {

    companion object {
        fun set(
            terminalFragment: TerminalFragment
        ) {
            val activity = terminalFragment.activity
            val binding = terminalFragment.binding

            activity?.registerForContextMenu(binding.terminalWebView)
            binding.terminalWebView.setOnLongClickListener() { view ->
                val hitTestResult = binding.terminalWebView.hitTestResult
                val currentPageUrl = binding.terminalWebView.url
                val httpsStartStr = "https://"
                val httpStartStr = "http://"
                when (hitTestResult.type) {
                    WebView.HitTestResult.IMAGE_TYPE -> {
                        if (
                            currentPageUrl?.startsWith(httpsStartStr) == true
                            || currentPageUrl?.startsWith(httpStartStr) == true
                        ) {
                            val openUrlIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(currentPageUrl)
                            )
                            terminalFragment.startActivity(openUrlIntent)
                        }
                    }
                    WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE,
                    WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                        if (
                            currentPageUrl?.startsWith(httpsStartStr) == true
                            || currentPageUrl?.startsWith(httpStartStr) == true
                        ) {
                            val openUrlIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(currentPageUrl)
                            )
                            terminalFragment.startActivity(openUrlIntent)
                        }

                    }
                }
                false
            }
        }
    }
}