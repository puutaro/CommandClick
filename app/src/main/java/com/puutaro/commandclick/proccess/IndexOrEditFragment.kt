package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

class IndexOrEditFragment(
    private val currentFragment: Fragment
) {
    fun select(
    ): ChangeTargetFragment? {
        val activity = currentFragment.activity
        val context = currentFragment.context
        val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
        val cmdVariableEditFragmentTag = context?.getString(R.string.cmd_variable_edit_fragment)
        val commandIndexFragment =
            TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                activity,
                cmdIndexFragmentTag
            )
        if(commandIndexFragment != null
            && commandIndexFragment.isVisible
        ) return ChangeTargetFragment.CMD_INDEX_FRAGMENT

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