package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader

object SetConfigInfo {
    fun set(
        editFragment: EditFragment
    ){
        val configConList = CommandClickVariables.makeScriptContentsList(
            UsePath.cmdclickSystemAppDirPath,
            UsePath.cmdclickConfigFileName
        )
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            configConList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )

        editFragment.onTermVisibleWhenKeyboard = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE,
            SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.INHERIT.name,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name,
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
            ),
        )

        editFragment.historySwitch =  SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            CommandClickScriptVariable.HISTORY_SWITCH_DEFAULT_VALUE,
            SettingVariableSelects.HistorySwitchSelects.INHERIT.name,
            CommandClickScriptVariable.HISTORY_SWITCH_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.HistorySwitchSelects.ON.name
            ),
        )

        editFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
            SettingVariableSelects.StatusBarIconColorModeSelects.INHERIT.name,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.StatusBarIconColorModeSelects.BLACK.name
            ),
        )

        editFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
            SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
        )

        val homeFannelHistoryNameList = SettingVariableReader.setListFromPath(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HOME_FANNELS_PATH
        )
        if(
            homeFannelHistoryNameList.isNotEmpty()
        ) editFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

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