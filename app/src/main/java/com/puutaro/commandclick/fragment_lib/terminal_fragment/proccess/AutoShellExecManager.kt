package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variant.ScriptArgs
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object AutoShellExecManager {

    private val onAutoExecArg = ScriptArgs.ON_AUTO_EXEC.str

    fun fire(
        terminalFrgment: TerminalFragment,
        cmdclickStartupOrEndShellName: String,
    ){
        if(
            terminalFrgment.onUrlLaunchIntent
        ) return
        val currentAppDirPath = terminalFrgment.currentAppDirPath

        val jsContentsList = ReadText(
            File(
                currentAppDirPath,
                cmdclickStartupOrEndShellName
            ).absolutePath
        ).textToList()
        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val substituteSettingVariableList =
            CommandClickVariables.substituteVariableListFromHolder(
                jsContentsList,
                settingSectionStart,
                settingSectionEnd,
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
            terminalFrgment,
            currentAppDirPath,
            cmdclickStartupOrEndShellName,
            jsContentsList,
            onAutoExecArg
        )
    }
}