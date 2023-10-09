package com.puutaro.commandclick.proccess.intent

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent
import com.puutaro.commandclick.proccess.lib.MakeExecCmdForTermux
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


object ExecShellScript {

    fun execShellScript(
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

        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(selectedShellFileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val shellContentsList = if(shellContentsListSource.isNullOrEmpty()) {
            ReadText(
                recentAppDirPath,
                selectedShellFileName
            ).textToList()
        } else shellContentsListSource
        val substituteSettingVariableList =
            CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
            currentFragment,
            substituteSettingVariableList,
        )

        val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
        ) ?: CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE

        val onUrlLaunchMacro = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.ON_URL_LAUNCH_MACRO
        ) ?: CommandClickScriptVariable.ON_URL_LAUNCH_MACRO_DEFAULT_VALUE

        UrlLaunchMacro.launch(
            terminalViewModel,
            recentAppDirPath,
            onUrlLaunchMacro,
        )

        JavascriptExecuter.exec(
            currentFragment,
            terminalViewModel,
            substituteSettingVariableList,
            onUrlLaunchMacro,
        )

        val shellExecEnv = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.SHELL_EXEC_ENV
        ) ?: CommandClickScriptVariable.SHELL_EXEC_ENV_DEFAULT_VALUE

        when(shellExecEnv){
            SettingVariableSelects.ShellExecEnvSelects.UBUNTU.name
            -> ubuntuExecHandler(
                currentFragment,
                recentAppDirPath,
                selectedShellFileName,
                substituteSettingVariableList,
            )
            SettingVariableSelects.ShellExecEnvSelects.TERMUX.name
            -> termuxExecer(
                currentFragment,
                recentAppDirPath,
                selectedShellFileName,
                substituteSettingVariableList,
            )
        }

        ShellFilePathToHistory.insert(
            recentAppDirPath,
            selectedShellFileName,
        )

        if(
            onUpdateLastModify
            == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
        ) return
        FileSystems.updateLastModified(
            recentAppDirPath,
            selectedShellFileName
        )
    }

    private fun ubuntuExecHandler(
        currentFragment: Fragment,
        recentAppDirPath: String,
        selectedShellFileName: String,
        substituteSettingVariableList: List<String>?,
    ){
        val context = currentFragment.context
            ?: return
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuLaunchCompFile.isFile
        ) {
            Toast.makeText(
                context,
                "Launch ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val ubuntuExecMode = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.UBUNTU_EXEC_MODE
        ) ?: CommandClickScriptVariable.UBUNTU_EXEC_MODE_DEFAULT_VALUE
        val ubuntuOutputFile = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.UBUNTU_OUTPUT_FILE
        ) ?: CommandClickScriptVariable.UBUNTU_OUTPUT_FILE_DEFAULT_VALUE
        when(ubuntuExecMode){
            SettingVariableSelects.UbuntuExecModeSelects.background.name -> {
                val backgroundCmdIntent = Intent()
                backgroundCmdIntent.action = BroadCastIntentScheme.BACKGROUND_CMD_START.action
                backgroundCmdIntent.putExtra(
                    UbuntuServerIntentExtra.backgroundShellPath.schema,
                    "${recentAppDirPath}/${selectedShellFileName}"
                )
                backgroundCmdIntent.putExtra(
                    UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema,
                    String()
                )
                backgroundCmdIntent.putExtra(
                    UbuntuServerIntentExtra.backgroundMonitorFileName.schema,
                    ubuntuOutputFile
                )
                currentFragment.activity?.sendBroadcast(backgroundCmdIntent)
            }
            SettingVariableSelects.UbuntuExecModeSelects.foreground.name -> {
                val foregroundCmdIntent = Intent()
                foregroundCmdIntent.action = BroadCastIntentScheme.FOREGROUND_CMD_START.action
                foregroundCmdIntent.putExtra(
                    UbuntuServerIntentExtra.foregroundShellPath.schema,
                    "${recentAppDirPath}/${selectedShellFileName}"
                )
                foregroundCmdIntent.putExtra(
                    UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema,
                    String()
                )
                foregroundCmdIntent.putExtra(
                    UbuntuServerIntentExtra.foregroundTimeout.schema,
                    "2000"
                )
                currentFragment.activity?.sendBroadcast(foregroundCmdIntent)
            }
        }
    }

    private fun termuxExecer(
        currentFragment: Fragment,
        recentAppDirPath: String,
        selectedShellFileName: String,
        substituteSettingVariableList: List<String>?,
    ){
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
        val context = currentFragment.context
            ?: return
        val runShell = when(currentFragment){
            is com.puutaro.commandclick.fragment.CommandIndexFragment -> currentFragment.runShell
            is EditFragment -> currentFragment.runShell
            else -> CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
        }
        val terminalDo = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.TERMINAL_DO
        ) ?: String()
        val backgroundExec =
            terminalDo != SettingVariableSelects.TerminalDoSelects.TERMUX.name

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
    }
}


private object ShellFilePathToHistory {
    fun insert(
        recentAppDirPath: String,
        shellFileName: String,
    ) {
        if(
            shellFileName == UsePath.cmdclickStartupJsName
        ) return
        val appUrlSystemPath = "${recentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val shellFullPath = "${recentAppDirPath}/${shellFileName}"
        if(
            !File(shellFullPath).isFile
        ) return
        val insertedHistoryContentsList = listOf("${shellFullPath}\t${shellFullPath}") + ReadText(
            appUrlSystemPath,
            cmdclickUrlHistoryFileName
        ).textToList()
        FileSystems.writeFile(
            appUrlSystemPath,
            cmdclickUrlHistoryFileName,
            insertedHistoryContentsList.joinToString("\n")
        )
    }
}