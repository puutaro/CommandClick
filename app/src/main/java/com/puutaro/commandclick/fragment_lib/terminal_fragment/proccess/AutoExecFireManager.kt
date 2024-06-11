package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variant.ScriptArgsMapList
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import java.io.File

object AutoExecFireManager {

    private val onAutoExecArg =
        ScriptArgsMapList.ScriptArgsName.ON_AUTO_EXEC.str

    fun fire(
        terminalFragment: TerminalFragment,
        cmdclickPreferenceJsName: String,
    ){
        if(
            terminalFragment.onUrlLaunchIntent
        ) return
        val context = terminalFragment.context
        val isCmdIndexTerminalFrag = howCmdIndexTerminalFrag(
            terminalFragment
        )

        val currentAppDirPath = terminalFragment.currentAppDirPath
        val currentSettingFannelPath = makeSettingFannelPath(
            terminalFragment,
            currentAppDirPath,
            cmdclickPreferenceJsName,
            isCmdIndexTerminalFrag
        )
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            currentSettingFannelPath
        )
        val jsContentsList = ReadText(
            currentSettingFannelPath
        ).readText().let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                terminalFragment.currentFannelName
            )
        }.split("\n")

        val substituteSettingVariableList =
            makeSettingValList(
                terminalFragment,
                jsContentsList,
                isCmdIndexTerminalFrag
            )
        val onAutoShell = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.CMDCLICK_ON_AUTO_EXEC
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "args_autoexec.txt").absolutePath,
//            listOf(
//                "currentSettingFannelPath: ${currentSettingFannelPath}",
//                "jsContentsList: ${jsContentsList}",
//                "onAutoShell: ${onAutoShell}",
//                "on: ${onAutoShell != SettingVariableSelects.AutoExecSelects.ON.name}",
//            ).joinToString("\n\n\n")
//        )
        if(
            onAutoShell !=
            SettingVariableSelects.AutoExecSelects.ON.name
        ) return
        ExecJsLoad.execJsLoad(
            terminalFragment,
            currentAppDirPath,
            cmdclickPreferenceJsName,
            jsContentsList,
            onAutoExecArg
        )
    }

    private fun howCmdIndexTerminalFrag(
        terminalFragment: TerminalFragment
    ): Boolean {
        val currentTerminalFragTag = terminalFragment.tag
        return currentTerminalFragTag == terminalFragment.context?.getString(
                R.string.index_terminal_fragment
            ) && !currentTerminalFragTag.isNullOrEmpty()
    }

    private fun makeSettingFannelPath(
        terminalFragment: TerminalFragment,
        currentAppDirPath: String,
        cmdclickPreferenceJsName: String,
        isCmdIndexTerminalFrag: Boolean
    ): String {
        return when(isCmdIndexTerminalFrag) {
            true -> File(
                currentAppDirPath,
                cmdclickPreferenceJsName
            ).absolutePath
            else -> FannelStateRooterManager.getSettingFannelPath(
                terminalFragment.readSharePreferenceMap,
                terminalFragment.setReplaceVariableMap
            )
        }
    }

    private fun makeSettingValList(
        terminalFragment: TerminalFragment,
        jsContentsList: List<String>,
        isCmdIndexTerminalFrag: Boolean
    ): List<String>? {
        return when(isCmdIndexTerminalFrag) {
            true ->
                CommandClickVariables.extractValListFromHolder(
                    jsContentsList,
                    terminalFragment.settingSectionStart,
                    terminalFragment.settingSectionEnd,
                )
            else ->
                FannelStateRooterManager.makeSettingVariableList(
                    terminalFragment.readSharePreferenceMap,
                    terminalFragment.setReplaceVariableMap,
                    terminalFragment.settingSectionStart,
                    terminalFragment.settingSectionEnd,
                    terminalFragment.settingFannelPath,
                )
        }
    }
}