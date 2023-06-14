package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.TargetFragmentInstance

class IndexOrEditFragment(
    private val terminalFragment: TerminalFragment
) {
    fun select(
    ): ChangeTargetFragment? {
        val activity = terminalFragment.activity
        val context = terminalFragment.context
        val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
        val commandIndexFragment =
            TargetFragmentInstance().getFromFragment<com.puutaro.commandclick.fragment.CommandIndexFragment>(
                activity,
                cmdIndexFragmentTag
            )
        if(commandIndexFragment != null
            && commandIndexFragment.isVisible
        ) return ChangeTargetFragment.CMD_INDEX_FRAGMENT
        val cmdVariableEditFragmentTag =
            FragmentTagManager.makeTag(
                FragmentTagManager.Prefix.cmdEditPrefix.str,
                terminalFragment.currentAppDirPath,
                terminalFragment.currentScriptName,
                FragmentTagManager.Suffix.ON.str
            )
        val cmdVariableEditFragment =
            TargetFragmentInstance().getFromFragment<EditFragment>(
                activity,
                cmdVariableEditFragmentTag
            )
        if(cmdVariableEditFragment != null
            && cmdVariableEditFragment.isVisible
        ) return ChangeTargetFragment.CMD_VARIABLES_EDIT_FRAGMENT
        return null
    }

}