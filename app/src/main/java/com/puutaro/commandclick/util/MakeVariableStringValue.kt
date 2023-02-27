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
            val runShellSourceTrim = BothEdgeQuote.trim(runShellSource)
            return if(
                runShellSourceTrim == String()
            ) variableDefaultStrValue
            else runShellSourceTrim
        }
    }
}