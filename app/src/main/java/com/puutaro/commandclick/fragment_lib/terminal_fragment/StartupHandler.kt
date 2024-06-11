package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.AutoExecFireManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object StartupHandler {

    fun invoke(
        terminalFragment: TerminalFragment
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            execInvoke(
                terminalFragment,
            )
        }
    }
    private fun execInvoke(
        terminalFragment: TerminalFragment,
    ) {
        val currentAppDirPath = terminalFragment.currentAppDirPath
        val fannelName = terminalFragment.currentFannelName
        WebUrlVariables.makeUrlHistoryFile(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        )
        val launchFannelName = if(
            fannelName == CommandClickScriptVariable.EMPTY_STRING
            || fannelName.isEmpty()
        ) UsePath.cmdclickPreferenceJsName
        else fannelName
        AutoExecFireManager.fire(
            terminalFragment,
            launchFannelName,
        )

    }
}