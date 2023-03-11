package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common

import android.content.Context
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JsOrShellFromSuffix

class DecideEditTag(
    private val shellContentsList: List<String>,
    private val selectedShellFileName: String,
) {

    fun decide(
        context: Context?,
        cmdSettingEditFragmentTag: String? = null
    ): String? {
        val languageType =
            JsOrShellFromSuffix.judge(selectedShellFileName)

        val languageTypeToSectionHolderMap =
            CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
        ) as String
        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_END
        ) as String
        val enableCommandHolderVariablesEdit = howEnableVariableHolder(
            commandSectionStart,
            commandSectionEnd
        )

        val enableSettingHolderVariablesEdit = howEnableVariableHolder(
            settingSectionStart,
            settingSectionEnd
        )
        if(!enableCommandHolderVariablesEdit
            && !enableSettingHolderVariablesEdit
        ){
            return null
        }
        return if(enableCommandHolderVariablesEdit) {
            context?.getString(com.puutaro.commandclick.R.string.cmd_variable_edit_fragment)
        } else {
            cmdSettingEditFragmentTag
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