package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable


object MakeSettingVariableNameOrValue {

    fun returnValidVariableName (
        substituteCmdStartEndContentStr: String,
        equalIndex: Int,
        substituteCmdStartEndContentList: List<String>,
    ): String? {
        val variableNameSource = substituteCmdStartEndContentStr.substring(
            0, equalIndex
        )
        val variableNameChecked =
            if (
                CommandClickScriptVariable.SETTING_VARIABLE_NAMES_LIST.contains(variableNameSource)
            ) {
                variableNameSource
            } else {
                return null
            }
        val variableNameFirstElement = substituteCmdStartEndContentList.filter {
            it.startsWith("${variableNameChecked}=")
        }.firstOrNull()
        return if(
            (
                    variableNameFirstElement != null
                            && variableNameFirstElement == substituteCmdStartEndContentStr
                    )
            || substituteCmdStartEndContentStr.startsWith(
                "${CommandClickScriptVariable.SET_VARIABLE_TYPE}="
            )
            || substituteCmdStartEndContentStr.startsWith(
                "${CommandClickScriptVariable.SET_REPLACE_VARIABLE}="
            )
            || substituteCmdStartEndContentStr.startsWith(
                "${CommandClickScriptVariable.HOME_SCRIPT_URL}="
            )
        ) {
            variableNameChecked
        } else {
            null
        }
    }
}