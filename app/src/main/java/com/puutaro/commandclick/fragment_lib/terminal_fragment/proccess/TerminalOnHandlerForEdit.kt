package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment

object TerminalOnHandlerForEdit {
    fun handle(
        terminalFragment: TerminalFragment
    ){
        if(
            terminalFragment.terminalOn
            != SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
        ) return
        val context = terminalFragment.context
        val listener = context as? TerminalFragment.OnTermSizeMinimumListenerForTerm
        listener?.onTermSizeMinimumForTerm()
    }
}