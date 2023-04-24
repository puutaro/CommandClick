package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.util.CommandClickVariables


class JudgeOpenTerminal {
    companion object {
        fun judge(
            settingSectionVariableList: List<String>?,
            editExecuteValue: String
        ): Boolean {
            if(settingSectionVariableList == null) return false
            if(
                editExecuteValue !=
                SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
            ) return false
            val terminalDo = CommandClickVariables.substituteCmdClickVariable(
                settingSectionVariableList,
                CommandClickScriptVariable.TERMINAL_DO
            ) ?: SettingVariableSelects.Companion.TerminalDoSelects.ON.name
            if(terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.ON.name){
                return true
            }
            return false
        }
    }
}