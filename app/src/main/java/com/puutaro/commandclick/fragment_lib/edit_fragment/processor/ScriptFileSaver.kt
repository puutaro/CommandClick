package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.state.FragmentTagManager

class ScriptFileSaver(
    private val editFragment: EditFragment,
) {
    fun save(
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?
    ){
        val editedTextContents = EditedTextContents(
            editFragment,
        )

        val editedShellContentsList = if(
            editFragment.passCmdVariableEdit
            == CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
            || editFragment.tag?.startsWith(FragmentTagManager.Prefix.SETTING_EDIT_PREFIX.str) == true
        ){
            editedTextContents.updateBySettingVariables(
                editFragment.currentScriptContentsList,
                recordNumToMapNameValueInSettingHolder,
            )
        } else {
            editedTextContents.updateByCommandVariables(
                editFragment.currentScriptContentsList,
                recordNumToMapNameValueInCommandHolder,
            )
        }
        editedTextContents.save(
            editedShellContentsList,
        )

    }
}