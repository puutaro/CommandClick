package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
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
            val readSharedPreferences = terminalFragment.readSharedPreferences
            val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharedPreferences,
                SharePrefferenceSetting.current_app_dir
            )
            val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharedPreferences,
                SharePrefferenceSetting.current_fannel_name
            )
            val fannelState = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharedPreferences,
                SharePrefferenceSetting.current_fannel_state
            )
            val cmdEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
                currentAppDirPath,
                currentFannelName,
                fannelState
            )
            val commandIndexFragment =
                TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                    terminalFragment.activity,
                    context?.getString(R.string.command_index_fragment)
                )
            val cmdEditFragment =
                TargetFragmentInstance().getFromFragment<EditFragment>(
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