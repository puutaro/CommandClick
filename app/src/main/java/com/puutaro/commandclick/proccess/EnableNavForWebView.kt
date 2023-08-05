package com.puutaro.commandclick.proccess

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

object EnableNavForWebVeiw {
    fun checkForGoForward(
        fragment: Fragment
    ): Boolean {
        val activity = fragment.activity
        val context = fragment.context
        val targetFragmentInstance = TargetFragmentInstance()
        val indexTerminalFragment = targetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.index_terminal_fragment)
        )
        val editExecuteTerminalFragment = targetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.edit_execute_terminal_fragment)
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

    fun checkForGoBack(
        fragment: Fragment
    ): Boolean {
        val activity = fragment.activity
        val context = fragment.context
        val targetFragmentInstance = TargetFragmentInstance()
        val indexTerminalFragment = targetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.index_terminal_fragment)
        )
        val editExecuteTerminalFragment = targetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.edit_execute_terminal_fragment)
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