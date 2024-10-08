package com.puutaro.commandclick.proccess.intent

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.intent.lib.UrlLaunchMacro
import com.puutaro.commandclick.proccess.lib.MakeExecCmdForTermux
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


//object ExecShellScript {
//
//    fun execShellScript(
//        currentFragment: Fragment,
////        recentAppDirPath: String,
//        selectedShellFileName: String,
//        shellContentsListSource: List<String>? = null
//    ){
//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        if(
//            !File(
//                cmdclickDefaultAppDirPath,
//                selectedShellFileName
//            ).isFile
//        ) return
//
//        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
//        val languageType =
//            CommandClickVariables.judgeJsOrShellFromSuffix(selectedShellFileName)
//
//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//        val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//        val settingSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String
//
//        val shellContentsList = if(shellContentsListSource.isNullOrEmpty()) {
//            ReadText(
//                File(
//                    cmdclickDefaultAppDirPath,
//                    selectedShellFileName
//                ).absolutePath
//            ).textToList()
//        } else shellContentsListSource
//        val substituteSettingVariableList =
//            CommandClickVariables.extractValListFromHolder(
//                shellContentsList,
//                settingSectionStart,
//                settingSectionEnd,
//            )
//
////        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
////            currentFragment,
////            substituteSettingVariableList,
////        )
//
//        val onUpdateLastModify = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
//        ) ?: CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE
//
//        val onUrlLaunchMacro = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.ON_URL_LAUNCH_MACRO
//        ) ?: CommandClickScriptVariable.ON_URL_LAUNCH_MACRO_DEFAULT_VALUE
//
//        val curUrl =
//            TargetFragmentInstance().getCurrentTerminalFragmentFromFrag(
//                currentFragment.activity,
//            )?.binding?.terminalWebView?.url
//        UrlLaunchMacro.launch(
//            terminalViewModel,
////            recentAppDirPath,
//            onUrlLaunchMacro,
//            curUrl,
//        )
//
//        JavascriptExecuter.exec(
//            currentFragment,
//            terminalViewModel,
//            substituteSettingVariableList,
//            onUrlLaunchMacro,
//        )
//
//        val shellExecEnv = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.SHELL_EXEC_ENV
//        ) ?: CommandClickScriptVariable.SHELL_EXEC_ENV_DEFAULT_VALUE
//        when(shellExecEnv){
//            SettingVariableSelects.ShellExecEnvSelects.UBUNTU.name
//            -> ubuntuExecHandler(
//                currentFragment,
////                recentAppDirPath,
//                selectedShellFileName,
//                substituteSettingVariableList,
//            )
//            SettingVariableSelects.ShellExecEnvSelects.TERMUX.name
//            -> termuxExecer(
//                currentFragment,
////                recentAppDirPath,
//                selectedShellFileName,
//                substituteSettingVariableList,
//            )
//        }
//
//        ShellFilePathToHistory.insert(
////            recentAppDirPath,
//            selectedShellFileName,
//        )
//
//        if(
//            onUpdateLastModify
//            == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
//        ) return
//        FileSystems.updateLastModified(
//            File(
//                cmdclickDefaultAppDirPath,
//                selectedShellFileName
//            ).absolutePath
//        )
//    }
//
//    private fun ubuntuExecHandler(
//        currentFragment: Fragment,
////        recentAppDirPath: String,
//        selectedShellFileName: String,
//        substituteSettingVariableList: List<String>?,
//    ){
//        val context = currentFragment.context
//            ?: return
//        val ubuntuFiles = UbuntuFiles(context)
//        if(
//            !ubuntuFiles.ubuntuLaunchCompFile.isFile
//        ) {
//            ToastUtils.showShort("Launch ubuntu")
//            return
//        }
//        val ubuntuExecMode = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.UBUNTU_EXEC_MODE
//        ) ?: CommandClickScriptVariable.UBUNTU_EXEC_MODE_DEFAULT_VALUE
//        val ubuntuOutputFile = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.UBUNTU_OUTPUT_FILE
//        ) ?: CommandClickScriptVariable.UBUNTU_OUTPUT_FILE_DEFAULT_VALUE
//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        when(ubuntuExecMode){
//            SettingVariableSelects.UbuntuExecModeSelects.background.name -> {
//                val backgroundCmdIntent = Intent()
//                backgroundCmdIntent.action = BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START.action
//                backgroundCmdIntent.putExtra(
//                    UbuntuServerIntentExtra.backgroundShellPath.schema,
//                    "${cmdclickDefaultAppDirPath}/${selectedShellFileName}"
//                )
//                backgroundCmdIntent.putExtra(
//                    UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema,
//                    String()
//                )
//                backgroundCmdIntent.putExtra(
//                    UbuntuServerIntentExtra.backgroundMonitorFileName.schema,
//                    ubuntuOutputFile
//                )
//                currentFragment.activity?.sendBroadcast(backgroundCmdIntent)
//            }
//            SettingVariableSelects.UbuntuExecModeSelects.foreground.name -> {
//                val foregroundCmdIntent = Intent()
//                foregroundCmdIntent.action = BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START.action
//                foregroundCmdIntent.putExtra(
//                    UbuntuServerIntentExtra.foregroundShellPath.schema,
//                    "${cmdclickDefaultAppDirPath}/${selectedShellFileName}"
//                )
//                foregroundCmdIntent.putExtra(
//                    UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema,
//                    String()
//                )
//                foregroundCmdIntent.putExtra(
//                    UbuntuServerIntentExtra.foregroundTimeout.schema,
//                    "2000"
//                )
//                currentFragment.activity?.sendBroadcast(foregroundCmdIntent)
//            }
//        }
//    }
//
//    private fun termuxExecer(
//        currentFragment: Fragment,
////        recentAppDirPath: String,
//        selectedShellFileName: String,
//        substituteSettingVariableList: List<String>?,
//    ){
//        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
//        val context = currentFragment.context
//            ?: return
//        val runShell = CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
//        val terminalDo = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.TERMINAL_DO
//        ) ?: String()
//        val backgroundExec =
//            terminalDo != SettingVariableSelects.TerminalDoSelects.TERMUX.name
//
//        val execCmd = MakeExecCmdForTermux.make(
//            currentFragment,
//            terminalDo,
//            substituteSettingVariableList,
////            recentAppDirPath,
//            selectedShellFileName,
//            runShell,
//        )
//
//        terminalViewModel.onDisplayUpdate = true
//
//        ExecBashScriptIntent.ToTermux(
//            context,
//            execCmd,
//            backgroundExec
//        )
//    }
//}
//
//
//private object ShellFilePathToHistory {
//    fun insert(
////        recentAppDirPath: String,
//        shellFileName: String,
//    ) {
//        if(
//            shellFileName == SystemFannel.preference
//        ) return
//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        val appUrlSystemPath = "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
//        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
//        val shellFullPath = "${cmdclickDefaultAppDirPath}/${shellFileName}"
//        if(
//            !File(shellFullPath).isFile
//        ) return
//        val cmdclickUrlHistoryFilePath = File(
//            appUrlSystemPath,
//            cmdclickUrlHistoryFileName
//        ).absolutePath
//        val insertedHistoryContentsList = listOf("${shellFullPath}\t${shellFullPath}") + ReadText(
//            cmdclickUrlHistoryFilePath
//        ).textToList()
//        FileSystems.writeFile(
//            cmdclickUrlHistoryFilePath,
//            insertedHistoryContentsList.joinToString("\n")
//        )
//    }
//}