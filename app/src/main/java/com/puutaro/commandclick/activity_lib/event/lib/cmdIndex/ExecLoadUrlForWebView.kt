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
            try {
                val indexTerminalFragment = activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.index_terminal_fragment)
                ) as TerminalFragment
                if(indexTerminalFragment.isVisible) {
                    val webView = indexTerminalFragment.binding.terminalWebView
                    webView.loadUrl(searchUrl)
                    return
                }
            } catch (e: Exception){
                println("pass")
            }
            try {
                val editExecuteTerminalFragment = activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.edit_execute_terminal_fragment)
                ) as TerminalFragment
                if(editExecuteTerminalFragment.isVisible) {
                    val webView = editExecuteTerminalFragment.binding.terminalWebView
                    webView.loadUrl(searchUrl)
                    return
                }
            } catch (e: Exception){
                return
            }
        }
    }
}