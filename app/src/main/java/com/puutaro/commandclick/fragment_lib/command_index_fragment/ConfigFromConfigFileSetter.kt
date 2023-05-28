package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*

class ConfigFromConfigFileSetter {
    companion object {
        fun set(
            cmdIndexCommandIndexFragment: CommandIndexFragment
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
                    UsePath.cmdclickConfigDirPath,
                    UsePath.cmdclickConfigFileName
                ).textToList(),
                settingSectionStart,
                settingSectionEnd
            )

            cmdIndexCommandIndexFragment.historySwitch =  SettingVariableReader.getCbValue(
                settingVariableList,
                CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
                CommandClickScriptVariable.HISTORY_SWITCH_DEFAULT_VALUE,
                SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
                CommandClickScriptVariable.HISTORY_SWITCH_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                    SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
                ),
            )

            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
                settingVariableList,
                CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
                CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
                CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
                ),
            )

            cmdIndexCommandIndexFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
                settingVariableList,
                CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
                CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
                CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
                ),
            )

            cmdIndexCommandIndexFragment.homeFannelHistoryName =  SettingVariableReader.getStrValue(
                settingVariableList,
                CommandClickScriptVariable.CMDCLICK_HOME_FANNEL,
                String()
            )

            cmdIndexCommandIndexFragment.runShell = SettingVariableReader.getStrValue(
                settingVariableList,
                CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
                CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
            )

            cmdIndexCommandIndexFragment.shiban = SettingVariableReader.getStrValue(
                settingVariableList,
                CommandClickScriptVariable.CMDCLICK_SHIBAN,
                CommandClickScriptVariable.CMDCLICK_SHIBAN_DEFAULT_VALUE
            )

            cmdIndexCommandIndexFragment.terminalColor = SettingVariableReader.getStrValue(
                settingVariableList,
                CommandClickScriptVariable.TERMINAL_COLOR,
                CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
            )
        }
    }
}