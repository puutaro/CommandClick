package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FirstUrlHistoryFile
import com.puutaro.commandclick.util.*


object ConfigFromStartUpFileSetterForTerm {

    fun set(
        terminalFragment: TerminalFragment,
    ){
        val activity = terminalFragment.activity
        val sharePref = activity?.getPreferences(Context.MODE_PRIVATE)
        terminalFragment.currentAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.current_app_dir
        )

        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_END
        ) as String

        val settingVariableListFromConfig = CommandClickVariables.substituteVariableListFromHolder(
            ReadText(
                UsePath.cmdclickConfigDirPath,
                UsePath.cmdclickConfigFileName
            ).textToList(),
            settingSectionStart,
            settingSectionEnd
        )

        terminalFragment.onAdBlock = SettingVariableReader.getCbValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.ON_ADBLOCK,
            CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE,
            SettingVariableSelects.Companion.OnAdblockSelects.INHERIT.name,
            CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.Companion.OnAdblockSelects.ON.name,
                SettingVariableSelects.Companion.OnAdblockSelects.OFF.name,
            ),
        )

        terminalFragment.fontZoomPercent =  SettingVariableReader.getNumValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM,
            CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE,
            "1"
        )

        terminalFragment.runShell =  SettingVariableReader.getStrValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
        )

        terminalFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.TERMINAL_COLOR,
            CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
        )
        terminalFragment.terminalFontColor = SettingVariableReader.getStrValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.TERMINAL_FONT_COLOR,
            CommandClickScriptVariable.TERMINAL_FONT_COLOR_DEFAULT_VALUE
        )

        val currentShellFileNameSource = SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.current_script_file_name
        )

        val currentShellFileName = if (
            terminalFragment.tag ==
            terminalFragment.context?.getString(
                R.string.index_terminal_fragment
            )
        ) UsePath.cmdclickStartupJsName
        else currentShellFileNameSource


        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            ReadText(
                terminalFragment.currentAppDirPath,
                currentShellFileName
            ).textToList(),
            settingSectionStart,
            settingSectionEnd
        )

        terminalFragment.ignoreHistoryPathList = SettingVariableReader.getStrListByReplace(
            settingVariableList,
            CommandClickScriptVariable.IGNORE_HISTORY_PATHS,
            currentShellFileName,
            terminalFragment.currentAppDirPath,
        )

        
        terminalFragment.onAdBlock = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_ADBLOCK,
            terminalFragment.onAdBlock,
            SettingVariableSelects.Companion.OnAdblockSelects.INHERIT.name,
            terminalFragment.onAdBlock,
            listOf(
                SettingVariableSelects.Companion.OnAdblockSelects.ON.name,
                SettingVariableSelects.Companion.OnAdblockSelects.OFF.name,
            ),
        )

        terminalFragment.onUrlHistoryRegister = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.Companion.OnUrlHistoryRegisterSelects.ON.name,
                SettingVariableSelects.Companion.OnUrlHistoryRegisterSelects.OFF.name,
            ),
        )

        terminalFragment.fontZoomPercent =  SettingVariableReader.getNumValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM,
            terminalFragment.fontZoomPercent,
            "1"
        )

        terminalFragment.runShell =  SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            terminalFragment.runShell
        )

        terminalFragment.binding.terminalWebView.settings.textZoom =
            terminalFragment.fontZoomPercent

        terminalFragment.onHistoryUrlTitle = CommandClickVariables.substituteCmdClickVariable(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_ON_HISTORY_URL_TITLE
        ) ?: terminalFragment.onHistoryUrlTitle
        FirstUrlHistoryFile.delete(
            terminalFragment,
            terminalFragment.currentAppDirPath
        )

        terminalFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            terminalFragment.terminalColor
        )

        terminalFragment.terminalFontColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_FONT_COLOR,
            terminalFragment.terminalFontColor
        )
    }
}