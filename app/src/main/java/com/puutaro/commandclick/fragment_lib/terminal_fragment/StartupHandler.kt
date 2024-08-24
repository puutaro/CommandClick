package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.AutoExecFireManager
import com.puutaro.commandclick.proccess.UrlLaunchIntentAction
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

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
        val tag = terminalFragment.tag
        val isIndexTerminal = !tag.isNullOrEmpty()
                && tag == terminalFragment.context?.getString(R.string.index_terminal_fragment)
        if(isIndexTerminal){
            UrlLaunchMacro.launchForIndex(
                terminalFragment.context,
            )
            return
        }
        val launchFannelName = when(
            FannelInfoTool.isEmptyFannelName(fannelName)
        ) {
            true -> SystemFannel.preference
            else -> fannelName
        }
        AutoExecFireManager.fire(
            terminalFragment,
            launchFannelName,
        )

    }
}