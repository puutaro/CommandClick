package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.content.Context
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.*

object AutoShellExecManager {

    private val onAutoExecArg = "onAutoExec"

    fun fire(
        terminalFrgment: TerminalFragment,
        cmdclickStartupOrEndShellName: String,
    ){
        if(
            terminalFrgment.onUrlLaunchIntent
        ) return
        val activity = terminalFrgment.activity
            ?: return
        val startUpPref = activity.getPreferences(Context.MODE_PRIVATE)
        val currentAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )

        val jsContentsList = ReadText(
            currentAppDirPath,
            cmdclickStartupOrEndShellName
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