package com.puutaro.commandclick.proccess

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment


object IntentAction {
    fun handle(
        terminalFragment: TerminalFragment,
    ) {
        val activity = terminalFragment.activity
        val intent = activity?.intent
        val intentAction =
            activity?.intent?.action
        val webView = terminalFragment.binding.terminalWebView
        when (
            intentAction
        ) {
            Intent.ACTION_VIEW,
            Intent.ACTION_MAIN -> {
                if (
                    intent?.dataString.isNullOrEmpty()
                ) return
                val urlString = activity.intent?.dataString
                urlString?.let {
                    terminalFragment.firstDisplayUpdate = false
                    webView.loadUrl(it)
                }
            }
            Intent.ACTION_WEB_SEARCH -> {
                val query = intent
                    ?.extras
                    ?.getString(
                        SearchManager.QUERY,
                        null
                    )
                    ?: return
                terminalFragment.firstDisplayUpdate = false
                webView.loadUrl("${WebUrlVariables.queryUrl}${query}")
            }
        }
    }

    fun judge(
        activity: Activity?,
    ): Boolean {
        val intent = activity?.intent
        val intentAction =
            activity?.intent?.action
        when (
            intentAction
        ) {
            Intent.ACTION_VIEW,
            Intent.ACTION_MAIN -> {
                return !intent?.dataString.isNullOrEmpty()
            }

            Intent.ACTION_WEB_SEARCH -> {
                val query = intent
                    ?.extras
                    ?: return false
                return !query.isEmpty
            }
            else -> {
                return false
            }
        }
    }
}