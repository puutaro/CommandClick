package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.TargetFragmentInstance

object TerminalShowByTerminalDoWhenReuse {
    fun show(
        editFragment: EditFragment,
        shellContentsList: List<String>?
    ){
        TargetFragmentInstance().getFromFragment<TerminalFragment>(
            editFragment.activity,
            editFragment.context?.getString(R.string.edit_execute_terminal_fragment)
        ) ?: return
        if(
            editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.cmdEditPrefix.str
            ) != true
        ) return
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
                terminalDo == SettingVariableSelects.TerminalDoSelects.OFF.name
                        || terminalDo == SettingVariableSelects.TerminalDoSelects.TERMUX.name
                )
        if(
            editFragment.languageType != LanguageTypeSelects.JAVA_SCRIPT
            && onTerminalDoOffAndTermux
        ) return
        val listener = editFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
        listener?.onKeyBoardVisibleChangeForEditFragment(
            false,
            true
        )
    }
}
