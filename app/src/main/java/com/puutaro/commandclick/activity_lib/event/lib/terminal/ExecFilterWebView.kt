package com.puutaro.commandclick.activity_lib.event.lib.terminal

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment

object ExecFilterWebView {
    fun invoke(
        activity: MainActivity,
        filterText: String
    ) {
        val terminalFragment = try {
            activity.supportFragmentManager.findFragmentByTag(
                activity.getString(R.string.index_terminal_fragment)
            ) as TerminalFragment
        } catch (e: Exception) {
            return
        }
        val webView = terminalFragment.binding.terminalWebView
        if (!webView.isVisible) return
        webView.loadUrl("javascript:terminalFilter('${filterText}')")
//        loadDataWithBaseURL("javascript:myfunc2('${filterText}')", "", "text/html", "utf-8", null)
//            .loadUrl("javascript:myfunc2('${filterText}')")
    }
}