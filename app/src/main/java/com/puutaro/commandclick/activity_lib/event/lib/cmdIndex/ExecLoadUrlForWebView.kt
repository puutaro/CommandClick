package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment

class ExecLoadUrlForWebView {
    companion object {
        fun execLoadUrlForWebView (
            activity: MainActivity,
            searchUrl: String,

        ) {
            val terminalFragment = try {
                activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.index_terminal_fragment)
                ) as TerminalFragment
            } catch (e: Exception){
                return
            }
            val webView = terminalFragment.binding.terminalWebView
            webView.loadUrl(searchUrl)
        }
    }
}