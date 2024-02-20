package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variant.ScriptArgsMapList
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import java.io.File

object AutoExecFireManager {

    private val onAutoExecArg =
        ScriptArgsMapList.ScriptArgsName.ON_AUTO_EXEC.str

    fun fire(
        terminalFragment: TerminalFragment,
        cmdclickStartupOrEndShellName: String,
    ){
        if(
            terminalFragment.onUrlLaunchIntent
        ) return
        val isCmdIndexTerminalFrag = howCmdIndexTerminalFrag(
            terminalFragment
        )

        val currentAppDirPath = terminalFragment.currentAppDirPath
        val currentSettingFannelPath = makeSettingFannelPath(
            terminalFragment,
            currentAppDirPath,
            cmdclickStartupOrEndShellName,
            isCmdIndexTerminalFrag
        )
        val jsContentsList = ReadText(
            currentSettingFannelPath
        ).textToList()

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
        if(
            onAutoShell !=
            SettingVariableSelects.AutoExecSelects.ON.name
        ) return
        ExecJsLoad.execJsLoad(
            terminalFragment,
            currentAppDirPath,
            cmdclickStartupOrEndShellName,
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
        cmdclickStartupOrEndShellName: String,
        isCmdIndexTerminalFrag: Boolean
    ): String {
        return when(isCmdIndexTerminalFrag) {
            true -> File(
                currentAppDirPath,
                cmdclickStartupOrEndShellName
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
                CommandClickVariables.substituteVariableListFromHolder(
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