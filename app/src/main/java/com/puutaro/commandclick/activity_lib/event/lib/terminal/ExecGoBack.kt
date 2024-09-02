package com.puutaro.commandclick.activity_lib.event.lib.terminal

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecGoBack {

    fun execGoBack(
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
    targetTerminalFragment: TerminalFragment
){
    val webView = targetTerminalFragment.binding.terminalWebView
    if (!webView.isVisible) return
    if(webView.canGoBack()) webView.goBack()
}
