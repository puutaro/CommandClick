package com.puutaro.commandclick.activity_lib.event.lib.terminal

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

object ExecGoBack {

    fun execGoBack(
        activity: MainActivity
    ){

        val targetFragmentInstance = TargetFragmentInstance()
        val indexTerminalFragment =
            targetFragmentInstance.getFromActivity<TerminalFragment>(
                activity,
                activity.getString(R.string.index_terminal_fragment)
            )
        val editExecuteTerminalFragment =
            targetFragmentInstance.getFromActivity<TerminalFragment>(
                activity,
                activity.getString(R.string.edit_execute_terminal_fragment)
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
            execBack(
                indexTerminalFragment
            )
            return
        }
        if(editExecuteTerminalFragment?.isVisible == true){
            execBack(
                editExecuteTerminalFragment
            )
            return
        }

    }
}


private fun execBack(
    targetFragment: TerminalFragment
){
    val webView = targetFragment.binding.terminalWebView
    if (!webView.isVisible) return
    if(webView.canGoBack()) webView.goBack()
}