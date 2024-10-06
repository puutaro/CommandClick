package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
//import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*
import java.io.File


object ConfigFromPreferenceFileSetter {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
//        currentAppDirPath: String,
    ){

//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val cmdclickPreferenceJsName = SystemFannel.preference
//        val languageType = LanguageTypeSelects.JAVA_SCRIPT
//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//                languageType
//            )
//        val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//
//        val settingSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String

        val preferenceConList = when(
            File(UsePath.cmdclickDefaultAppDirPath, cmdclickPreferenceJsName).isFile
        ) {
            false ->
                CommandClickVariables.makeMainFannelConListFromUrl(
                    cmdIndexFragment.context,
                    cmdclickPreferenceJsName
                )
            else ->
                CommandClickVariables.makeMainFannelConList(
//                currentAppDirPath,
                    cmdclickPreferenceJsName
                )

        }

        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            preferenceConList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
//            settingSectionStart,
//            settingSectionEnd
        )

        cmdIndexFragment.onTermVisibleWhenKeyboard = SettingVariableReader.getCbValue(
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

//        cmdIndexFragment.historySwitch =  SettingVariableReader.getCbValue(
//            settingVariableList,
//            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
//            CommandClickScriptVariable.HISTORY_SWITCH_DEFAULT_VALUE,
//            SettingVariableSelects.HistorySwitchSelects.INHERIT.name,
//            CommandClickScriptVariable.HISTORY_SWITCH_DEFAULT_VALUE,
//            listOf(
//                SettingVariableSelects.HistorySwitchSelects.OFF.name,
//                SettingVariableSelects.HistorySwitchSelects.ON.name
//            ),
//        )

//        cmdIndexFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
//            settingVariableList,
//            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
//            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
//            SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name,
//            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE,
//            listOf(
//                SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
//                SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
//            ),
//        )

        cmdIndexFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
        )
//        val bottomScriptUrlList = SettingVariableReader.setListFromPath(
//            ScriptPreWordReplacer.replace(
//                UsePath.homeScriptUrlsFilePath,
////                currentAppDirPath,
//                cmdclickPreferenceJsName,
//            )
//        )
//        if(
//            bottomScriptUrlList.isNotEmpty()
//        ) cmdIndexFragment.bottomScriptUrlList = bottomScriptUrlList

//        val homeFannelHistoryNameList = SettingVariableReader.setListFromPath(
//            ScriptPreWordReplacer.replace(
//                UsePath.homeFannelsFilePath,
////                currentAppDirPath,
//                cmdclickPreferenceJsName,
//            )
//        )
//        if(
//            homeFannelHistoryNameList.isNotEmpty()
//        ) cmdIndexFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

    }
}