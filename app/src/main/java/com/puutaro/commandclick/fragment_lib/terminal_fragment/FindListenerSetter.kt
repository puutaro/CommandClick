package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object FindListenerSetter {
    fun set(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
        val binding = terminalFragment.binding
        binding.terminalWebView.setFindListener {
                activeMatchOrdinal, numberOfMatches, isDoneCounting ->
            if(!isDoneCounting) return@setFindListener
            val fannelInfoMap = terminalFragment.fannelInfoMap
//            val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//                fannelInfoMap
//            )
            val currentFannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val fannelState = FannelInfoTool.getCurrentStateName(
                fannelInfoMap
            )
            val cmdEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
//                currentAppDirPath,
                currentFannelName,
                fannelState
            )
            val commandIndexFragment =
                TargetFragmentInstance.getFromFragment<CommandIndexFragment>(
                    terminalFragment.activity,
                    context?.getString(R.string.command_index_fragment)
                )
            val cmdEditFragment =
                TargetFragmentInstance.getFromFragment<EditFragment>(
                    terminalFragment.activity,
                    cmdEditFragmentTag
                )
            val isNotVisibleCommandIndexFragment = commandIndexFragment?.isVisible != true
            val isNotVisibleCmdEditFragment = cmdEditFragment?.isVisible != true
            if(
                isNotVisibleCommandIndexFragment
                && isNotVisibleCmdEditFragment
            ) return@setFindListener
            val listener =
                context as? TerminalFragment.OnFindPageSearchResultListener
            listener?.onFindPageSearchResultListner(
                activeMatchOrdinal,
                numberOfMatches,
            )
        }
    }
}