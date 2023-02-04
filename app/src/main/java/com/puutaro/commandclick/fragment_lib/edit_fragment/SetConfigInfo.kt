package com.puutaro.commandclick.fragment_lib.edit_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.MakeVariableCbValue
import com.puutaro.commandclick.util.MakeVariableStringValue
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class SetConfigInfo {
    companion object {
        fun set(
            editFragment: EditFragment
        ){

            val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

            val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    UsePath.cmdclickConfigDirPath,
                    UsePath.cmdclickConfigFileName
                ).txetToList(),
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END
            )


            editFragment.historySwitch =  MakeVariableCbValue.make(
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

            terminalViewModel.onBackStackWhenSizeLong =  MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.ON_BACKSTACK_WHEN_SIZE_LONG,
                CommandClickShellScript.ON_BACKSTACK_WHEN_SIZE_LONG_DEFAULT_VALUE,
                SettingVariableSelects.Companion.OnBackstackWhenSizeLongSelects.INHERIT.name,
                CommandClickShellScript.ON_BACKSTACK_WHEN_SIZE_LONG_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.OnBackstackWhenSizeLongSelects.ON.name,
                    SettingVariableSelects.Companion.OnBackstackWhenSizeLongSelects.OFF.name
                ),
            )

            editFragment.statusBarIconColorMode = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
                ),
            )

            editFragment.urlHistoryOrButtonExec = MakeVariableCbValue.make(
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


            editFragment.runShell =  MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_RUN_SHELL,
                CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
            )

            editFragment.shiban = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_SHIBAN,
                CommandClickShellScript.CMDCLICK_SHIBAN_DEFAULT_VALUE
            )

            editFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_COLOR,
                CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
            )

        }
    }
}