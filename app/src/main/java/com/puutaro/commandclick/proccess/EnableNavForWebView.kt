package com.puutaro.commandclick.proccess

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object EnableNavForWebView {

    fun checkForGoBack(
        fragment: androidx.fragment.app.Fragment
    ): Boolean {
        val activity = fragment.activity
        val indexTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.index_terminal_fragment)
        )
        val editExecuteTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.edit_terminal_fragment)
        )
        if(
            indexTerminalFragment == null
            && editExecuteTerminalFragment == null
        ) return false
        if(
            indexTerminalFragment?.isVisible != true
            && editExecuteTerminalFragment?.isVisible != true
        ) return false
        if(indexTerminalFragment?.isVisible == true){
            return judgeCanGoBack(
                indexTerminalFragment
            )
        }
        if(editExecuteTerminalFragment?.isVisible == true){
            return judgeCanGoBack(
                editExecuteTerminalFragment
            )
        }
        return false
    }

    fun checkForReload(
        fragment: androidx.fragment.app.Fragment
    ): Boolean {
        val activity = fragment.activity
        val indexTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.index_terminal_fragment)
        )
        val editExecuteTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.edit_terminal_fragment)
        )
        if(
            indexTerminalFragment == null
            && editExecuteTerminalFragment == null
        ) return false
        if(
            indexTerminalFragment?.isVisible != true
            && editExecuteTerminalFragment?.isVisible != true
        ) return false
        if(indexTerminalFragment?.isVisible == true){
            return judgeCanReload(
                indexTerminalFragment
            )
        }
        if(editExecuteTerminalFragment?.isVisible == true){
            return judgeCanReload(
                editExecuteTerminalFragment
            )
        }
        return false
    }

    fun checkForGoForward(
        fragment: androidx.fragment.app.Fragment
    ): Boolean {
        val activity = fragment.activity
        val indexTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.index_terminal_fragment)
        )
        val editExecuteTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.edit_terminal_fragment)
        )
        if(
            indexTerminalFragment == null
            && editExecuteTerminalFragment == null
        ) return false
        if(
            indexTerminalFragment?.isVisible != true
            && editExecuteTerminalFragment?.isVisible != true
        ) return false
        if(indexTerminalFragment?.isVisible == true){
            return judgeCanGoForward(
                indexTerminalFragment
            )
        }
        if(editExecuteTerminalFragment?.isVisible == true){
            return judgeCanGoForward(
                editExecuteTerminalFragment
            )
        }
        return false
    }

}


private fun judgeCanGoForward(
    targetTerminalFragment: TerminalFragment
): Boolean {
    val webView = targetTerminalFragment.binding.terminalWebView
    if (!webView.isVisible) return false
    return webView.canGoForward()
}

private fun judgeCanGoBack(
    targetTerminalFragment: TerminalFragment
): Boolean {
    val webView = targetTerminalFragment.binding.terminalWebView
    if (!webView.isVisible) return false
    return webView.canGoBack()
}

private fun judgeCanReload(
    targetTerminalFragment: TerminalFragment
): Boolean {
    val webView = targetTerminalFragment.binding.terminalWebView
    if (!webView.isVisible) return false
    val reloadUrl = webView.url
        ?: return false
    val isReloadUrl = reloadUrl.startsWith(
        WebUrlVariables.filePrefix)
            || reloadUrl.startsWith(
        WebUrlVariables.slashPrefix)
            || reloadUrl.startsWith(
        WebUrlVariables.httpsPrefix)
            || reloadUrl.startsWith(
        WebUrlVariables.httpPrefix)
    if(!isReloadUrl) return false
    return true
}