package com.puutaro.commandclick.activity_lib.event.lib.terminal

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecReload {
    fun execReload(
        activity: MainActivity
    ){

        val indexTerminalFragment =
            TargetFragmentInstance.getFromActivity<TerminalFragment>(
                activity,
                activity.getString(R.string.index_terminal_fragment)
            )
        val editExecuteTerminalFragment =
            TargetFragmentInstance.getFromActivity<TerminalFragment>(
                activity,
                activity.getString(R.string.edit_terminal_fragment)
            )
        if(
            indexTerminalFragment == null
            && editExecuteTerminalFragment == null
        ) return
        if(
            indexTerminalFragment?.isVisible != true
            && editExecuteTerminalFragment?.isVisible != true
        ) return
        if(indexTerminalFragment?.isVisible == true){
            execReload(
                indexTerminalFragment
            )
            return
        }
        if(editExecuteTerminalFragment?.isVisible == true){
            execReload(
                editExecuteTerminalFragment
            )
            return
        }

    }
}


private fun execReload(
    targetTerminalFragment: TerminalFragment
){
    val webView = targetTerminalFragment.binding.terminalWebView
    if (!webView.isVisible) return
    val reloadUrl = webView.url
        ?: return
    val isReloadUrl = reloadUrl.startsWith(
        WebUrlVariables.filePrefix)
            || reloadUrl.startsWith(
        WebUrlVariables.slashPrefix)
            || reloadUrl.startsWith(
        WebUrlVariables.httpsPrefix)
            || reloadUrl.startsWith(
        WebUrlVariables.httpPrefix)
    if(!isReloadUrl) return
    webView.loadUrl(reloadUrl)
}
