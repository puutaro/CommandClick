package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript

class MakeVariableNumValue {
    companion object {
        fun make(
            cmdVariableList: List<String>?,
            numVariableName: String,
            defaultNum: Int,
            throwNumStr: String
        ): Int {
            return try {
                val fontZoomShellValueSource = CommandClickVariables.substituteCmdClickVariable(
                    cmdVariableList,
                    numVariableName,
                )
                val fontZoomShellValue = if(
                    fontZoomShellValueSource == throwNumStr
                    || fontZoomShellValueSource.isNullOrEmpty()
                    || fontZoomShellValueSource == "0"
                ){
                    defaultNum.toString()
                } else fontZoomShellValueSource
                fontZoomShellValue.toInt() ?: defaultNum
            } catch (e: Exception) {
                defaultNum
            }
        }
    }
}