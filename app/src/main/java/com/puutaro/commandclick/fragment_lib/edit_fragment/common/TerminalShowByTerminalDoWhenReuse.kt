package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object TerminalShowByTerminalDoWhenReuse {
    fun show(
        editFragment: EditFragment,
        shellContentsList: List<String>?
    ){
        TargetFragmentInstance().getFromFragment<TerminalFragment>(
            editFragment.activity,
            editFragment.context?.getString(R.string.edit_terminal_fragment)
        ) ?: return
        if(
            editFragment.tag?.startsWith(
                FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str
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
