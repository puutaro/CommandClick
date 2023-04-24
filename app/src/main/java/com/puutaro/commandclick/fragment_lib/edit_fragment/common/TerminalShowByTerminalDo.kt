package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables

class TerminalShowByTerminalDo {
    companion object {
        fun show(
            editFragment: EditFragment,
            shellContentsList: List<String>?
        ){
            if(
                shellContentsList.isNullOrEmpty()
            ) return
            val variablesSettingHolderList = CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                editFragment.settingSectionStart,
                editFragment.settingSectionEnd
            )
            val terminalDo = CommandClickVariables.substituteCmdClickVariable(
                variablesSettingHolderList,
                CommandClickScriptVariable.TERMINAL_DO
            )
            val onTerminalDoOffAndTermux = (
                    terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
                            || terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name
                    )
            if(
                onTerminalDoOffAndTermux
            ) return
            val listener = editFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
            listener?.onKeyBoardVisibleChangeForEditFragment(
                false,
                true
            )
        }
    }
}