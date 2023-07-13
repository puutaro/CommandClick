package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod

class ScriptFileSaver(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    private val readSharePreffernceMap: Map<String, String>,
    private val onButton: Boolean = false,
) {
    private val context = editFragment.context
    private val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_script_file_name
    )
    fun save(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>? = null,
    ){

        val editedTextContents = EditedTextContents(
            binding,
            editFragment,
            readSharePreffernceMap
        )

        val editedShellContentsList = if(
            editFragment.passCmdVariableEdit
            == CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
            || editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.settingEditPrefix.str
            ) == true
        ){
            if(onButton) return
            editedTextContents.updateBySettingVariables(
                shellContentsList,
                recordNumToMapNameValueInSettingHolder,
            )
        } else {
            val shellContentsListUpdatedCmdVariable = editedTextContents.updateByCommandVariables(
                shellContentsList,
                recordNumToMapNameValueInCommandHolder,
            )
            updateShellScriptNameForEditCmdVriable(
                shellContentsListUpdatedCmdVariable
            )
        }
        editedTextContents.save(
            editedShellContentsList,
        )
    }

    private fun updateShellScriptNameForEditCmdVriable(
        shellContentsListUpdatedCmdVariable: List<String>
    ): List<String> {
        val shellScriptNameVariableName = CommandClickScriptVariable.SCRIPT_FILE_NAME
        var countSettingHolderStart = 0
        var countSettingHolderEnd = 0
        return shellContentsListUpdatedCmdVariable.map {
            if (
                it.startsWith(editFragment.settingSectionStart)
                && it.endsWith(editFragment.settingSectionStart)
            ) countSettingHolderStart++
            if (
                it.startsWith(editFragment.settingSectionEnd)
                && it.endsWith(editFragment.settingSectionEnd)
            ) countSettingHolderEnd++
            if (
                countSettingHolderStart == 0
                || countSettingHolderEnd > 0
            ) return@map it
            if (
                it.startsWith(shellScriptNameVariableName)
            ) "${shellScriptNameVariableName}=\"${currentShellFileName}\""
            else it
        }
    }
}