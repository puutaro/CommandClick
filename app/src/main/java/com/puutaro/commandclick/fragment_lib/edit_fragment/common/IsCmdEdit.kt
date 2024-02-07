package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagManager

object IsCmdEdit {
    fun judge(
        editFragment: EditFragment,
    ): Boolean {
        val onPassCmdVariableEdit =
            editFragment.passCmdVariableEdit ==
                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val currentEditFragmentTag = editFragment.tag
        return currentEditFragmentTag?.startsWith(
            FragmentTagManager.Prefix.SETTING_EDIT_PREFIX.str
        ) != true
                && !onPassCmdVariableEdit
    }
}