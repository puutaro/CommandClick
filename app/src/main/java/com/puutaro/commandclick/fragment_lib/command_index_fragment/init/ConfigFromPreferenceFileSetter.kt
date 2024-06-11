package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer


object ConfigFromPreferenceFileSetter {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
    ){

        val cmdclickPreferenceJsName = UsePath.cmdclickPreferenceJsName
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

        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            CommandClickVariables.makeMainFannelConList(
                currentAppDirPath,
                cmdclickPreferenceJsName
            ),
            settingSectionStart,
            settingSectionEnd
        )

        cmdIndexFragment.onTermVisibleWhenKeyboard = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD,
            cmdIndexFragment.onTermVisibleWhenKeyboard,
            SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.INHERIT.name,
            cmdIndexFragment.onTermVisibleWhenKeyboard,
            listOf(
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name,
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
            ),
        )

        cmdIndexFragment.historySwitch = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            cmdIndexFragment.historySwitch,
            SettingVariableSelects.HistorySwitchSelects.INHERIT.name,
            cmdIndexFragment.historySwitch,
            listOf(
                SettingVariableSelects.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.HistorySwitchSelects.ON.name
            ),
        )

        cmdIndexFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            cmdIndexFragment.urlHistoryOrButtonExec,
            SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name,
            cmdIndexFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
        )

        cmdIndexFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            cmdIndexFragment.terminalColor
        )
        val bottomScriptUrlList = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.homeScriptUrlsFilePath,
                currentAppDirPath,
                cmdclickPreferenceJsName,
            )
        )
        if(
            bottomScriptUrlList.isNotEmpty()
        ) cmdIndexFragment.bottomScriptUrlList = bottomScriptUrlList

        val homeFannelHistoryNameList = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.homeFannelsFilePath,
                currentAppDirPath,
                cmdclickPreferenceJsName,
            )
        )
        if(
            homeFannelHistoryNameList.isNotEmpty()
        ) cmdIndexFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

    }
}