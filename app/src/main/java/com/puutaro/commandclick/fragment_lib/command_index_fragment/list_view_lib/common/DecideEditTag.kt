package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.str.AltRegexTool

class DecideEditTag(
    private val shellContentsList: List<String>,
//    private val currentAppDirPath: String,
    private val selectedScriptFileName: String,
    private val fannelState: String,
) {
//    private val languageType =
//        CommandClickVariables.judgeJsOrShellFromSuffix(selectedScriptFileName)

//    private val languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String
//    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//    ) as String
//    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//    ) as String
    private val enableCommandHolderVariablesEdit = howEnableVariableHolder(
        CommandClickScriptVariable.CMD_SEC_START,
        CommandClickScriptVariable.CMD_SEC_END
//        commandSectionStart,
//        commandSectionEnd
    )

    private val enableSettingHolderVariablesEdit = howEnableVariableHolder(
        CommandClickScriptVariable.SETTING_SEC_START,
        CommandClickScriptVariable.SETTING_SEC_END,
//        settingSectionStart,
//        settingSectionEnd
    )

    fun decide(): String? {
        if(!enableCommandHolderVariablesEdit
            && !enableSettingHolderVariablesEdit
        ){
            return null
        }
        return if(enableCommandHolderVariablesEdit) {
            FragmentTagManager.makeCmdValEditTag(
//                currentAppDirPath,
                selectedScriptFileName,
                fannelState
            )
        } else {
            FragmentTagManager.makeSettingValEditTag(
//                currentAppDirPath,
                selectedScriptFileName,
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
            FragmentTagManager.makeCmdValEditTag(
//                currentAppDirPath,
                selectedScriptFileName,
                fannelState
            )
        } else {
            FragmentTagManager.makeSettingValEditTag(
//                currentAppDirPath,
                selectedScriptFileName,
            )
        }
    }



    private fun howEnableVariableHolder(
        startHolderName: String,
        endHolderName: String,
    ): Boolean {
//        val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
        return CommandClickVariables.extractValListFromHolder(
            shellContentsList,
            startHolderName,
            endHolderName
        )?.filter {
            AltRegexTool.containsAlphaNumUnderscoreHyphenEquals(
                it,
            )
//            cmdclickVariableRegex.containsMatchIn(
//                it
//            )
        }?.size?.let {
            it > 0
        } ?: false
    }
}