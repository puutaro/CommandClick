package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

class FindListenerSetter {
    companion object {
        fun set(
            terminalFragment: TerminalFragment
        ){
            val context = terminalFragment.context
            val binding = terminalFragment.binding
            binding.terminalWebView.setFindListener {
                    activeMatchOrdinal, numberOfMatches, isDoneCounting ->
                if(!isDoneCounting) return@setFindListener
                val commandIndexFragment =
                    TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                        terminalFragment.activity,
                        context?.getString(R.string.command_index_fragment)
                    )
                if(commandIndexFragment?.isVisible != true) return@setFindListener
                val listener =
                    context as? TerminalFragment.OnFindPageSearchResultListener
                listener?.onFindPageSearchResultListner(
                    activeMatchOrdinal,
                    numberOfMatches,
                )
            }
        }
    }
}