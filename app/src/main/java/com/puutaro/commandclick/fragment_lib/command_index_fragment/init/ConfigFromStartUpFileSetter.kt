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

        val cmdclickStartupJsName = UsePath.cmdclickStartupJsName
        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            CommandClickVariables.makeScriptContentsList(
                currentAppDirPath,
                cmdclickStartupJsName
            ),
            settingSectionStart,
            settingSectionEnd
        )

        cmdIndexCommandIndexFragment.historySwitch = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            cmdIndexCommandIndexFragment.historySwitch,
            SettingVariableSelects.HistorySwitchSelects.INHERIT.name,
            cmdIndexCommandIndexFragment.historySwitch,
            listOf(
                SettingVariableSelects.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.HistorySwitchSelects.ON.name
            ),
        )

        cmdIndexCommandIndexFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec,
            SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name,
            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
        )

        cmdIndexCommandIndexFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            cmdIndexCommandIndexFragment.statusBarIconColorMode,
            SettingVariableSelects.StatusBarIconColorModeSelects.INHERIT.name,
            cmdIndexCommandIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.StatusBarIconColorModeSelects.BLACK.name
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
        val bottomScriptUrlList = SettingVariableReader.setListFromPath(
            settingVariableList,
            CommandClickScriptVariable.HOME_SCRIPT_URLS_PATH
        )
        if(
            bottomScriptUrlList.isNotEmpty()
        ) cmdIndexCommandIndexFragment.bottomScriptUrlList = bottomScriptUrlList

        val homeFannelHistoryNameList = SettingVariableReader.setListFromPath(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HOME_FANNELS_PATH
        )
        if(
            homeFannelHistoryNameList.isNotEmpty()
        ) cmdIndexCommandIndexFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

    }
}