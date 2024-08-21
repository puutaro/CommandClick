package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CommandClickVariables

object ValidFannelNameGetterForTerm {

    fun get(
        terminalFragment: TerminalFragment
    ): String {
        val context = terminalFragment.context
        return when (
            terminalFragment.tag == context?.getString(
                R.string.index_terminal_fragment
            )
        ) {
            true -> UsePath.cmdclickPreferenceJsName
            else -> terminalFragment.currentFannelName
        }
    }

    fun make(
        terminalFragment: TerminalFragment,
        currentValidFannelName: String,
    ): List<String> {
        return CommandClickVariables.makeMainFannelConList(
//            terminalFragment.currentAppDirPath,
            currentValidFannelName
        )
    }
}