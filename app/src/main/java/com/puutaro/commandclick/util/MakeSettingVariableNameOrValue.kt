package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable


    object MakeSettingVariableNameOrValue {

        fun returnValidVariableName (
            substituteCmdStartEndContentStr: String,
            equalIndex: Int,
            substituteCmdStartEndContentList: List<String>,
        ): String? {
            val variableNameSource = substituteCmdStartEndContentStr.substring(
                0, equalIndex
            )
            val factSettingVariableNamesList =
                CommandClickScriptVariable.SETTING_VARIABLE_NAMES_LIST
            return if (
                    factSettingVariableNamesList.contains(variableNameSource)
                ) {
                    variableNameSource
                } else {
                    null
                }
//            val variableNameFirstElement = substituteCmdStartEndContentList.lastOrNull {
//                it.startsWith("${variableNameChecked}=")
//            }
//            return if(
//                (
//                        variableNameFirstElement != null
////                                && variableNameFirstElement == substituteCmdStartEndContentStr
//                        )
////                || substituteCmdStartEndContentStr.startsWith(
////                    "${CommandClickScriptVariable.SET_VARIABLE_TYPE}="
////                )
////                || substituteCmdStartEndContentStr.startsWith(
////                    "${CommandClickScriptVariable.SET_REPLACE_VARIABLE}="
////                )
//            ) {
//                variableNameChecked
//            } else {
//                null
//            }
        }
}