package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagPrefix

object IsCmdEdit {
    fun judge(
        editFragment: EditFragment,
    ): Boolean {
//        val onPassCmdVariableEdit =
//            editFragment.passCmdVariableEdit ==
//                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val currentEditFragmentTag = editFragment.tag
        return currentEditFragmentTag?.startsWith(
            FragmentTagPrefix.Prefix.SETTING_VAL_EDIT_PREFIX.str
        ) != true
//                && !onPassCmdVariableEdit
    }
}