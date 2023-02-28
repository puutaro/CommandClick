package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.CommandClickShellScript
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
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END
            )
            val terminalDo = CommandClickVariables.substituteCmdClickVariable(
                variablesSettingHolderList,
                CommandClickShellScript.TERMINAL_DO
            )
            if(
                terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
                || terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name
            ) return
            val listener = editFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
            listener?.onKeyBoardVisibleChangeForEditFragment(
                false,
                true
            )
        }
    }
}