package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader

object SetConfigInfo {
    fun set(
        editFragment: EditFragment
    ){

        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            ReadText(
                UsePath.cmdclickConfigDirPath,
                UsePath.cmdclickConfigFileName
            ).textToList(),
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )


        editFragment.historySwitch =  SettingVariableReader.getCbValue(
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

        editFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
            SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
            ),
        )

        editFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
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

        editFragment.homeFannelHistoryNameList = CommandClickVariables.substituteCmdClickVariableList(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HOME_FANNEL
        )

        editFragment.runShell =  SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
        )

        editFragment.shiban = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_SHIBAN,
            CommandClickScriptVariable.CMDCLICK_SHIBAN_DEFAULT_VALUE
        )

        editFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
        )

    }
}