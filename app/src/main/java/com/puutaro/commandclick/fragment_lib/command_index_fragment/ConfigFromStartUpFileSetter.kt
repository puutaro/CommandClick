package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ConfigFromStartUpFileSetter {
    companion object {
        fun set(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
        ){

            val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

            val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    currentAppDirPath,
                    UsePath.cmdclickStartupShellName
                ).txetToList(),
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END
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