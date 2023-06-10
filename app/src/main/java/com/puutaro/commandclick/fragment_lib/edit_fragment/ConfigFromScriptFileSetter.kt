package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.*

object ConfigFromScriptFileSetter {

    fun set(
        editFragment: EditFragment,
    ){
        val readSharePreffernceMap = editFragment.readSharePreffernceMap
        val onShortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.on_shortcut
        )
        if (
            onShortcut != ShortcutOnValueStr.ON.name
        ) return

        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )

        val context = editFragment.context

        val currentShellContentsList = ReadText(
            currentAppDirPath,
            currentShellFileName
        ).textToList()

        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            currentShellContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )

        editFragment.historySwitch = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            editFragment.historySwitch,
            SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
            editFragment.historySwitch,
            listOf(
                SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
            ),
        )

        editFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            editFragment.urlHistoryOrButtonExec,
            SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
            editFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
        )

        editFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            editFragment.statusBarIconColorMode,
            SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
            editFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
            ),
        )

        editFragment.runShell = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            editFragment.runShell
        )

        editFragment.shiban = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_SHIBAN,
            editFragment.shiban
        )

        editFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            editFragment.terminalColor
        )

        editFragment.fontZoomPercent = SettingVariableReader.getNumValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM,
            editFragment.fontZoomPercent,
            "1"
        )

        editFragment.execPlayBtnLongPress = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EXEC_PLAY_BTN_LONG_PRESS,
            String()
        )

        editFragment.overrideItemClickExec = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.OVERRIDE_ITEM_CLICK_EXEC,
            CommandClickScriptVariable.OVERRIDE_ITEM_CLICK_EXEC_DEFAULT_VALUE,
        )

        editFragment.execEditBtnLongPress = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EXEC_EDIT_BTN_LONG_PRESS,
            String()
        )

        val bottomScriptUrlListSource = SettingVariableReader.getStrListByReplace(
            settingVariableList,
            CommandClickScriptVariable.HOME_SCRIPT_URL,
            currentShellFileName,
            currentAppDirPath,
        )

        editFragment.bottomScriptUrlList = bottomScriptUrlListSource.filter {
            val enableSuffix = it.endsWith(
                    CommandClickScriptVariable.JS_FILE_SUFFIX
                )
                    || it.endsWith(
                        CommandClickScriptVariable.SHELL_FILE_SUFFIX
                    )
                    || it.endsWith(
                        CommandClickScriptVariable.HTML_FILE_SUFFIX
                    )
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
        ) editFragment.homeFannelHistoryNameList = homeFannelHistoryNameListSource

        if(
            editFragment.tag ==
            editFragment.context?.getString(R.string.setting_variable_edit_fragment)
        ) return
        editFragment.terminalOn = CommandClickVariables.substituteCmdClickVariable(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_DO
        ) ?: CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
    }
}