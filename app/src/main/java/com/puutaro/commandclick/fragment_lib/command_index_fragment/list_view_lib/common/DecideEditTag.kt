package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.JsOrShellFromSuffix

class DecideEditTag(
    private val shellContentsList: List<String>,
    private val currentAppDirPath: String,
    private val selectedScriptFileName: String,
) {

    private val languageType =
        JsOrShellFromSuffix.judge(selectedScriptFileName)

    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String
    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String
    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String
    private val enableCommandHolderVariablesEdit = howEnableVariableHolder(
        commandSectionStart,
        commandSectionEnd
    )

    private val enableSettingHolderVariablesEdit = howEnableVariableHolder(
        settingSectionStart,
        settingSectionEnd
    )

    fun decide(): String? {
        if(!enableCommandHolderVariablesEdit
            && !enableSettingHolderVariablesEdit
        ){
            return null
        }
        return if(enableCommandHolderVariablesEdit) {
            FragmentTagManager.makeTag(
                FragmentTagManager.Prefix.cmdEditPrefix.str,
                currentAppDirPath,
                selectedScriptFileName,
                FragmentTagManager.Suffix.ON.str
            )
        } else {
            FragmentTagManager.makeTag(
                FragmentTagManager.Prefix.settingEditPrefix.str,
                currentAppDirPath,
                selectedScriptFileName,
                String()
            )
        }
    }

    fun decideForEdit(): String? {
        if(!enableCommandHolderVariablesEdit
            && !enableSettingHolderVariablesEdit
        ){
            return null
        }
        return if(enableCommandHolderVariablesEdit) {
            FragmentTagManager.makeTag(
                FragmentTagManager.Prefix.cmdEditPrefix.str,
                currentAppDirPath,
                selectedScriptFileName,
                String()
            )
        } else {
            FragmentTagManager.makeTag(
                FragmentTagManager.Prefix.settingEditPrefix.str,
                currentAppDirPath,
                selectedScriptFileName,
                String()
            )
        }
    }



    private fun howEnableVariableHolder(
        startHolderName: String,
        endHolderName: String,
    ): Boolean {
        val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
        return CommandClickVariables.substituteVariableListFromHolder(
            shellContentsList,
            startHolderName,
            endHolderName
        )?.filter {
            cmdclickVariableRegex.containsMatchIn(
                it
            )
        }?.size?.let {
            it > 0
        } ?: false
    }
}