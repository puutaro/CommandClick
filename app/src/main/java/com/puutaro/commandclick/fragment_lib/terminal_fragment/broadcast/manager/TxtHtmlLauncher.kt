package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.manager

import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.TxtHtmlDescriber
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.IndexOrEditFragment

object TxtHtmlLauncher {
    fun launch(
        terminalFragment: TerminalFragment,
        urlStr: String,
    ){
        val context = terminalFragment.context
        val binding = terminalFragment.binding
        binding.terminalWebView.loadDataWithBaseURL(
            "",
            TxtHtmlDescriber.make(
                urlStr,
                terminalFragment
            ),
            "text/html",
            "utf-8",
            null
        )
        val changeTargetFragment =
            IndexOrEditFragment(terminalFragment).select()
        val listener =
            context as? TerminalFragment.OnTermLongChangeListenerForTerminalFragment
        listener?.onTermLongChangeForTerminalFragment(
            changeTargetFragment
        )
    }
}