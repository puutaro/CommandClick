package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*


object ConfigFromStartUpFileSetter {
    fun set(
        cmdIndexCommandIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
    ){
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

        cmdIndexCommandIndexFragment.historySwitch = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            cmdIndexCommandIndexFragment.historySwitch,
            SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
            cmdIndexCommandIndexFragment.historySwitch,
            listOf(
                SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
            ),
        )

        cmdIndexCommandIndexFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec,
            SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
        )

        cmdIndexCommandIndexFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            cmdIndexCommandIndexFragment.statusBarIconColorMode,
            SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
            ),
        )

            cmdIndexCommandIndexFragment.runShell = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            cmdIndexCommandIndexFragment.runShell
        )

        cmdIndexCommandIndexFragment.shiban = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_SHIBAN,
            cmdIndexCommandIndexFragment.shiban
        )

        cmdIndexCommandIndexFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            cmdIndexCommandIndexFragment.terminalColor
        )

        cmdIndexCommandIndexFragment.bottomScriptUrlList = CommandClickScriptVariable.BOTTOM_SCRIPT_URL_LIST.map {
            SettingVariableReader.getStrValue(
                settingVariableList,
                it,
                String()
            )
        }.filter {
            val enableSuffix = it.endsWith(CommandClickScriptVariable.JS_FILE_SUFFIX)
                    || it.endsWith(CommandClickScriptVariable.SHELL_FILE_SUFFIX)
                    || it.endsWith(CommandClickScriptVariable.HTML_FILE_SUFFIX)
            it.isNotEmpty()
                    && enableSuffix
        }
    }
}