package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.SharePreffrenceMethod

class ShellScriptSaver(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    private val readSharePreffernceMap: Map<String, String>,
    private val onButton: Boolean = false,
) {
    private val context = editFragment.context
    private val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_shell_file_name
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

        val settingVariableEditFragment = context?.getString(
            R.string.setting_variable_edit_fragment
        )
        val cmdConfigVariableEditFragment = context?.getString(
            R.string.cmd_config_variable_edit_fragment
        )

        val editedShellContentsList = when(
            editFragment.tag
        ) {
            settingVariableEditFragment,
            cmdConfigVariableEditFragment -> {
                if(onButton) return
                editedTextContents.updateBySettingVariables(
                    shellContentsList,
                    recordNumToMapNameValueInSettingHolder,
                )
            }
            else -> {
                val shellContentsListUpdatedCmdVariable = editedTextContents.updateByCommandVariables(
                    shellContentsList,
                    recordNumToMapNameValueInCommandHolder,
                )
                updateShellScriptNameForEditCmdVriable(
                    shellContentsListUpdatedCmdVariable
                )
            }
        }
        editedTextContents.save(
            editedShellContentsList,
        )
    }

    private fun updateShellScriptNameForEditCmdVriable(
        shellContentsListUpdatedCmdVariable: List<String>
    ): List<String> {
        val shellScriptNameVariableName = CommandClickShellScript.SHELL_FILE_NAME
        var countSettingHolderStart = 0
        var countSettingHolderEnd = 0
        return shellContentsListUpdatedCmdVariable.map {
            if (
                it.startsWith(CommandClickShellScript.SETTING_SECTION_START)
                && it.endsWith(CommandClickShellScript.SETTING_SECTION_START)
            ) countSettingHolderStart++
            if (
                it.startsWith(CommandClickShellScript.SETTING_SECTION_END)
                && it.endsWith(CommandClickShellScript.SETTING_SECTION_END)
            ) countSettingHolderEnd++
            if (
                countSettingHolderStart == 0
                || countSettingHolderEnd > 0
            ) return@map it
            if (
                it.startsWith(shellScriptNameVariableName)
            ) "${shellScriptNameVariableName}=${currentShellFileName}"
            else it
        }
    }
}