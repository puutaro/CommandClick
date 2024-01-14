package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.AutoShellExecManager
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object StartupOrEditExecuteOnceShell {

    fun invoke(
        terminalFragment: TerminalFragment
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            execInvoke(
                terminalFragment,
            )
        }
    }
    suspend fun execInvoke(
        terminalFragment: TerminalFragment,
    ) {
        val context = terminalFragment.context
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val activity = withContext(Dispatchers.IO) {
            while(true){
                if(terminalFragment.isAdded) break
                delay(100)
            }
            terminalFragment.activity
        }

        val currentAppDirPath = terminalFragment.currentAppDirPath
        val fannelName = terminalFragment.currentFannelName


        val editExecuteOnceCurrentShellFileName =
            terminalViewModel.editExecuteOnceCurrentShellFileName
        if (
            !editExecuteOnceCurrentShellFileName.isNullOrEmpty()
        ) {
            TargetFragmentInstance()
                .getFromFragment<TerminalFragment>(
                    activity,
                    activity?.getString(R.string.index_terminal_fragment)
                ) ?: return
            ExecJsOrSellHandler.handle(
                terminalFragment,
                currentAppDirPath,
                editExecuteOnceCurrentShellFileName,
            )
            terminalViewModel.editExecuteOnceCurrentShellFileName = null
            return
        }
        WebUrlVariables.makeUrlHistoryFile(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        )
        val launchFannelName = if(
            fannelName == CommandClickScriptVariable.EMPTY_STRING
            || fannelName.isEmpty()
        ) UsePath.cmdclickStartupJsName
        else fannelName
        AutoShellExecManager.fire(
            terminalFragment,
            launchFannelName,
        )

    }
}