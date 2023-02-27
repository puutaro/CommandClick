package com.puutaro.commandclick.proccess

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
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
            recentAppDirPath: String,
            selectedShellFileName: String,
            shellContentsListSource: List<String>? = null
        ){
            if(
                !File(
                    recentAppDirPath,
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
                    recentAppDirPath,
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
                recentAppDirPath,
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
                recentAppDirPath,
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
                    recentAppDirPath,
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

            val editExecute = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.EDIT_EXECUTE
            ) ?: CommandClickShellScript.EDIT_EXECUTE_DEFAULT_VALUE

            ShellFilePathToHistory.insert(
                recentAppDirPath,
                selectedShellFileName,
            )

            if(
                onUpdateLastModify
                == SettingVariableSelects.Companion.OnUpdateLastModifySelects.OFF.name
            ) return
            FileSystems.updateLastModified(
                recentAppDirPath,
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
                .filter {
                    EnableUrlPrefix.check(
                        it.split("\t").lastOrNull()
                    )
                }
                .firstOrNull()
                ?.split("\t")?.lastOrNull()
        }
        SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.FREQUENCY.name -> {
            terminalViewModel.launchUrl = ReadText(
                recentAppdirPath,
                UsePath.cmdclickUrlHistoryFileName
            ).textToList()
                .filter {
                    EnableUrlPrefix.check(
                        it.split("\t").lastOrNull()
                    )
                }
                .groupBy { it }
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
    if(
        substituteSettingVariableList.isNullOrEmpty()
    ) return
    val execJsOrHtmlPath = CommandClickVariables.substituteCmdClickVariable(
        substituteSettingVariableList,
        CommandClickShellScript.EXEC_JS_OR_HTML_PATH
    ) ?: return
    if(
        execJsOrHtmlPath.endsWith(
            CommandClickShellScript.JS_FILE_SUFFIX
        )
        || execJsOrHtmlPath.endsWith(
            CommandClickShellScript.JSX_FILE_SUFFIX
        )
    ) {
        terminalViewModel.launchUrl = JavaScriptLoadUrl.make(
            execJsOrHtmlPath,
        )
        return
    }
    val enableHtmlSuffix = execJsOrHtmlPath.endsWith(
        CommandClickShellScript.HTML_FILE_SUFFIX
    )
            || execJsOrHtmlPath.endsWith(
        CommandClickShellScript.HTM_FILE_SUFFIX
    )
    val enableHtml =
        execJsOrHtmlPath.startsWith(
            WebUrlVariables.slashPrefix
        ) && enableHtmlSuffix
    if(!enableHtml) return
    val jsOrHtmlFileObj = File(execJsOrHtmlPath)
    if(!jsOrHtmlFileObj.isFile) return
    val currentAppDir = jsOrHtmlFileObj.parent
    if(
        currentAppDir.isNullOrEmpty()
    ) return
    terminalViewModel.launchUrl = "${currentAppDir}/${jsOrHtmlFileObj.name}"
}


private class ShellFilePathToHistory {
    companion object {
        fun insert(
            recentAppDirPath: String,
            shellFileName: String,
        ) {
            if(
                shellFileName == UsePath.cmdclickStartupShellName
                || shellFileName == UsePath.cmdclickEndShellName
            ) return
            val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
            val shellFullPath = "${recentAppDirPath}/${shellFileName}"
            if(
                !File(shellFullPath).isFile
            ) return
            val insertedHistoryContentsList = listOf("${shellFullPath}\t${shellFullPath}") + ReadText(
                recentAppDirPath,
                cmdclickUrlHistoryFileName
            ).textToList()
            FileSystems.writeFile(
                recentAppDirPath,
                cmdclickUrlHistoryFileName,
                insertedHistoryContentsList.joinToString("\n")
            )
        }
    }
}