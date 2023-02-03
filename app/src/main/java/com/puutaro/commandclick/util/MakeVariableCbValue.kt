package com.puutaro.commandclick.util


class MakeVariableCbValue {
    companion object {
        fun make(
            cmdVariableList: List<String>?,
            variableName: String,
            defaultVariableStrValue: String,
            inheritVariableValue: String,
            inheritVariableReturnValue: String,
            noDefaultValueList: List<String>,
        ): String {
            val historySwitchSource = CommandClickVariables.substituteCmdClickVariable(
                cmdVariableList,
                variableName
            ) ?: defaultVariableStrValue

            return if(
                noDefaultValueList.contains(historySwitchSource)
            ) historySwitchSource
            else if(
                historySwitchSource == inheritVariableValue
            ) inheritVariableReturnValue
            else defaultVariableStrValue
        }
    }
}