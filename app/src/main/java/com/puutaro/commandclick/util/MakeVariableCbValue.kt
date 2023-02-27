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
            val historySwitchSourceTrim = BothEdgeQuote.trim(historySwitchSource)

            return if(
                noDefaultValueList.contains(historySwitchSourceTrim)
            ) historySwitchSourceTrim
            else if(
                historySwitchSourceTrim == inheritVariableValue
            ) inheritVariableReturnValue
            else defaultVariableStrValue
        }
    }
}