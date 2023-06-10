package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*


object ConfigFromStartUpFileSetter {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
    ){

        val cmdclickStartupJsName = UsePath.cmdclickStartupJsName
        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_END
        ) as String

        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            ReadText(
                currentAppDirPath,
                UsePath.cmdclickStartupJsName
            ).textToList(),
            settingSectionStart,
            settingSectionEnd
        )

        cmdIndexFragment.historySwitch = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            cmdIndexFragment.historySwitch,
            SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
            cmdIndexFragment.historySwitch,
            listOf(
                SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
            ),
        )

        cmdIndexFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            cmdIndexFragment.urlHistoryOrButtonExec,
            SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
            cmdIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
        )

        cmdIndexFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            cmdIndexFragment.statusBarIconColorMode,
            SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
            cmdIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
            ),
        )

            cmdIndexFragment.runShell = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            cmdIndexFragment.runShell
        )

        cmdIndexFragment.shiban = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_SHIBAN,
            cmdIndexFragment.shiban
        )

        cmdIndexFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            cmdIndexFragment.terminalColor
        )

        val bottomScriptUrlListSource = SettingVariableReader.getStrListByReplace(
            settingVariableList,
            CommandClickScriptVariable.HOME_SCRIPT_URL,
            cmdclickStartupJsName,
            currentAppDirPath,
        )
        cmdIndexFragment.bottomScriptUrlList = bottomScriptUrlListSource.filter {
            val enableSuffix = it.endsWith(CommandClickScriptVariable.JS_FILE_SUFFIX)
                    || it.endsWith(CommandClickScriptVariable.SHELL_FILE_SUFFIX)
                    || it.endsWith(CommandClickScriptVariable.HTML_FILE_SUFFIX)
            it.isNotEmpty()
                    && enableSuffix
        }

        val homeFannelHistoryNameListSource = CommandClickVariables.substituteCmdClickVariableList(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HOME_FANNEL
        )
        if(
            !homeFannelHistoryNameListSource
                ?.joinToString("")
                .isNullOrEmpty()
        ) cmdIndexFragment.homeFannelHistoryNameList = homeFannelHistoryNameListSource
    }
}