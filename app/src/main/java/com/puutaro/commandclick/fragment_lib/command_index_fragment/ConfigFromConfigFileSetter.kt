package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*

class ConfigFromConfigFileSetter {
    companion object {
        fun set(
            cmdIndexFragment: CommandIndexFragment
        ){

            val languageType = LanguageTypeSelects.JAVA_SCRIPT
            val languageTypeToSectionHolderMap =
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                    languageType
                )
            val settingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
            ) as String

            val settingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
            ) as String
            val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    UsePath.cmdclickConfigDirPath,
                    UsePath.cmdclickConfigFileName
                ).textToList(),
                settingSectionStart,
                settingSectionEnd
            )

            cmdIndexFragment.historySwitch = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_HISTORY_SWITCH,
                CommandClickShellScript.HISTORY_SWITCH_DEFAULT_VALUE,
                SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
                CommandClickShellScript.HISTORY_SWITCH_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                    SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
                ),
            )

            cmdIndexFragment.urlHistoryOrButtonExec = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
                CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
                CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
                ),
            )

            cmdIndexFragment.statusBarIconColorMode = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
                ),
            )

            cmdIndexFragment.runShell = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_RUN_SHELL,
                CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
            )

            cmdIndexFragment.shiban = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_SHIBAN,
                CommandClickShellScript.CMDCLICK_SHIBAN_DEFAULT_VALUE
            )

            cmdIndexFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_COLOR,
                CommandClickShellScript.TERMINAL_DO_DEFAULT_VALUE
            )
        }
    }
}