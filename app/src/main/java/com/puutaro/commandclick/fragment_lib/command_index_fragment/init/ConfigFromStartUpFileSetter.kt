package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*


class ConfigFromStartUpFileSetter {
    companion object {
        fun set(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
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
                    currentAppDirPath,
                    UsePath.cmdclickStartupJsName
                ).textToList(),
                settingSectionStart,
                settingSectionEnd
            )

            cmdIndexFragment.historySwitch = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_HISTORY_SWITCH,
                cmdIndexFragment.historySwitch,
                SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
                cmdIndexFragment.historySwitch,
                listOf(
                    SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                    SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
                ),
            )

            cmdIndexFragment.urlHistoryOrButtonExec = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
                cmdIndexFragment.urlHistoryOrButtonExec,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
                cmdIndexFragment.urlHistoryOrButtonExec,
                listOf(
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
                ),
            )

            cmdIndexFragment.statusBarIconColorMode = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE,
                cmdIndexFragment.statusBarIconColorMode,
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
                cmdIndexFragment.urlHistoryOrButtonExec,
                listOf(
                    SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
                ),
            )

                cmdIndexFragment.runShell = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_RUN_SHELL,
                cmdIndexFragment.runShell
            )

            cmdIndexFragment.shiban = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_SHIBAN,
                cmdIndexFragment.shiban
            )

            cmdIndexFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_COLOR,
                cmdIndexFragment.terminalColor
            )
        }
    }
}