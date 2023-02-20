package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent.Companion.execSetTermSizeForIntent
import com.puutaro.commandclick.proccess.lib.MakeExecCmdForTermux
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
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
                ).textToList()
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



            val onUrlLaunchMacro = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.ON_URL_LAUNCH_MACRO
            ) ?: CommandClickShellScript.ON_URL_LAUNCH_MACRO_DEFAULT_VALUE

            urlLaunchMacroProcessor(
                terminalViewModel,
                recentAppdirPath,
                onUrlLaunchMacro,
            )

            jsExecuteProcessor(
                terminalViewModel,
                substituteSettingVariableList,
                onUrlLaunchMacro,
            )



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


private fun urlLaunchMacroProcessor(
    terminalViewModel: TerminalViewModel,
    recentAppdirPath: String,
    onUrlLaunchMacro: String,
) {
    when(onUrlLaunchMacro){
        SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.RECENT.name -> {
            terminalViewModel.launchUrl = ReadText(
                recentAppdirPath,
                UsePath.cmdclickUrlHistoryFileName
            ).textToList()
                .firstOrNull()
                ?.split("\t")?.lastOrNull()
        }
        SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.FREQUENCY.name -> {
            terminalViewModel.launchUrl = ReadText(
                recentAppdirPath,
                UsePath.cmdclickUrlHistoryFileName
            ).textToList().groupBy { it }
                .mapValues { it.value.size }
                .maxBy { it.value }
                .key
                .split("\t")
                .lastOrNull()
        }
        else -> {

        }
    }
}


private fun jsExecuteProcessor(
    terminalViewModel: TerminalViewModel,
    substituteSettingVariableList: List<String>?,
    onUrlLaunchMacro: String,
) {
    if(
      onUrlLaunchMacro
      != SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.OFF.name
    ) return
    if(substituteSettingVariableList.isNullOrEmpty()) return
    val execJsPath = CommandClickVariables.substituteCmdClickVariable(
        substituteSettingVariableList,
        CommandClickShellScript.EXEC_JS_PATH
    ) ?: return
    terminalViewModel.launchUrl = JavaScriptLoadUrl.make(
        execJsPath,
    )
}
