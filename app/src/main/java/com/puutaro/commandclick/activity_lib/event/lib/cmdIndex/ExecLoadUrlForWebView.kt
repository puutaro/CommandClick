package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File
import java.io.UnsupportedEncodingException

import java.net.URLDecoder




object ExecLoadUrlForWebView {
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
                activity.getString(R.string.edit_terminal_fragment)
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

    private fun convertIntoUth8Format(url: String): String? {
        var newStr: String? = ""
        try {
            newStr = URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return newStr
    }
}