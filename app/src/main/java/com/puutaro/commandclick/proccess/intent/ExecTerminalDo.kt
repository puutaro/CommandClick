package com.puutaro.commandclick.proccess.intent

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent
import com.puutaro.commandclick.proccess.lib.MakeExecCmdForTermux
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


object ExecTerminalDo {

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
            is com.puutaro.commandclick.fragment.CommandIndexFragment -> currentFragment.runShell
            is EditFragment -> currentFragment.runShell
            else -> CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
        }
        val context = currentFragment.context
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
        val languageType =
            JsOrShellFromSuffix.judge(selectedShellFileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_END
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

        val terminalDo = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.TERMINAL_DO
        ) ?: String()
        val backgroundExec =
            terminalDo != SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name

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
            context,
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