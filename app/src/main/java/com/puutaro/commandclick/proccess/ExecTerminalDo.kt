package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent.Companion.execSetTermSizeForIntent
import com.puutaro.commandclick.proccess.lib.MakeExecCmdForTermux
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


class ExecTerminalDo {

    companion object {
        fun execTerminalDo(
            currentFragment: Fragment,
            recentAppdirPath: String,
            selectedShellFileName: String,
            shellContentsListSource: List<String>? = null
        ){
            if(
                !File(
                    recentAppdirPath,
                    selectedShellFileName
                ).isFile
            ) return
            val runShell = when(currentFragment){
                is CommandIndexFragment -> currentFragment.runShell
                is EditFragment -> currentFragment.runShell
                else -> CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
            }
            val context = currentFragment.context
            val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()

            val shellContentsList = if(shellContentsListSource.isNullOrEmpty()) {
                ReadText(
                    recentAppdirPath,
                    selectedShellFileName
                ).txetToList()
            } else shellContentsListSource
            val substituteSettingVariableList =
                CommandClickVariables.substituteVariableListFromHolder(
                    shellContentsList,
                    CommandClickShellScript.SETTING_SECTION_START,
                    CommandClickShellScript.SETTING_SECTION_END,
                )

            execSetTermSizeForIntent(
                currentFragment,
                recentAppdirPath,
                substituteSettingVariableList,
            )

            val terminalDo = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.TERMINAL_DO
            ) ?: String()
            val backgroundExec =
                terminalDo != SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name

            val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.ON_UPDATE_LAST_MODIFY
            ) ?: CommandClickShellScript.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE

            val execCmd = MakeExecCmdForTermux.make(
                    currentFragment,
                    terminalDo,
                    substituteSettingVariableList,
                    recentAppdirPath,
                    selectedShellFileName,
                    runShell,
            )

            terminalViewModel.onDisplayUpdate = true

            ExecBashScriptIntent.ToTermux(
                runShell,
                context,
                execCmd,
                backgroundExec
            )
            if(
                onUpdateLastModify
                == SettingVariableSelects.Companion.OnUpdateLastModifySelects.OFF.name
            ) return
            FileSystems.updateLastModified(
                recentAppdirPath,
                selectedShellFileName
            )
        }
    }
}

