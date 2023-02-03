package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript


class MakeSettingVariableNameOrValue {
    companion object {

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
                    CommandClickShellScript.SETTING_VARIABLE_NAMES_LIST.contains(variableNameSource)
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
                || substituteCmdStartEndContentStr.startsWith("${CommandClickShellScript.SET_VARIABLE_TYPE}=")
            ) {
                variableNameChecked
            } else {
                null
            }
        }
    }
}