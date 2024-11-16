package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.manager

import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.TxtHtmlDescriber
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object TxtHtmlLauncher {
    fun launch(
        terminalFragment: TerminalFragment,
        urlStr: String,
    ){
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        binding.terminalWebView.loadDataWithBaseURL(
            "",
            TxtHtmlDescriber.make(
                terminalFragment,
                urlStr,
            ),
            "text/html",
            "utf-8",
            null
        )
        val cmdEditFragmentTag = TargetFragmentInstance.getCmdEditFragmentTag(activity)
        val bottomFragment = TargetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdEditFragmentTag,
        )
        val listener =
            context as? TerminalFragment.OnTermLongChangeListenerForTerminalFragment
        listener?.onTermLongChangeForTerminalFragment(
            bottomFragment
        )
    }
}