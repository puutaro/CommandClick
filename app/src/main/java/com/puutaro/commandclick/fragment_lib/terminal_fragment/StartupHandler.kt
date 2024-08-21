package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.AutoExecFireManager
import com.puutaro.commandclick.proccess.UrlLaunchIntentAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object StartupHandler {

    fun invoke(
        terminalFragment: TerminalFragment
    ) {
//        val isUrlIntent = UrlLaunchIntentAction.judge(terminalFragment.activity)
//        if(isUrlIntent) return
        CoroutineScope(Dispatchers.Main).launch {
            execInvoke(
                terminalFragment,
            )
        }
    }
    private fun execInvoke(
        terminalFragment: TerminalFragment,
    ) {
//        val currentAppDirPath = terminalFragment.currentAppDirPath
        val fannelName = terminalFragment.currentFannelName
        WebUrlVariables.makeUrlHistoryFile(
            "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
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