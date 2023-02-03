package com.puutaro.commandclick.util

class MakeVariableStringValue {
    companion object {
        fun make(
            cmdVariableList: List<String>?,
            variableName: String,
            variableDefaultStrValue: String,
        ): String {
            val runShellSource =  CommandClickVariables.substituteCmdClickVariable(
                cmdVariableList,
                variableName
            ) ?: variableDefaultStrValue
           return if(
                runShellSource == String()
            ) variableDefaultStrValue
            else runShellSource
        }
    }
}