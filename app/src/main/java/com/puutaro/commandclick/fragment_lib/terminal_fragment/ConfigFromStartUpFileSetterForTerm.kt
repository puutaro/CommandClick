package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FirstUrlHistoryFile
import com.puutaro.commandclick.util.*


class ConfigFromStartUpFileSetterForTerm {
    companion object {


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
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                    languageType
                )
            val settingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
            ) as String

            val settingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
            ) as String

            val settingVariableListFromConfig = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    UsePath.cmdclickConfigDirPath,
                    UsePath.cmdclickConfigFileName
                ).textToList(),
                settingSectionStart,
                settingSectionEnd
            )

            terminalFragment.onAdBlock = MakeVariableCbValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.ON_ADBLOCK,
                CommandClickShellScript.ON_ADBLOCK_DEFAULT_VALUE,
                SettingVariableSelects.Companion.OnAdblockSelects.INHERIT.name,
                CommandClickShellScript.ON_ADBLOCK_DEFAULT_VALUE,
                listOf(
                    SettingVariableSelects.Companion.OnAdblockSelects.ON.name,
                    SettingVariableSelects.Companion.OnAdblockSelects.OFF.name,
                ),
            )

            terminalFragment.fontZoomPercent =  MakeVariableNumValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE,
                "1"
            )

            terminalFragment.runShell =  MakeVariableStringValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.CMDCLICK_RUN_SHELL,
                CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
            )

            terminalFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.TERMINAL_COLOR,
                CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
            )
            terminalFragment.terminalFontColor = MakeVariableStringValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.TERMINAL_FONT_COLOR,
                CommandClickShellScript.TERMINAL_FONT_COLOR_DEFAULT_VALUE
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

            terminalFragment.onAdBlock = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.ON_ADBLOCK,
                terminalFragment.onAdBlock,
                SettingVariableSelects.Companion.OnAdblockSelects.INHERIT.name,
                terminalFragment.onAdBlock,
                listOf(
                    SettingVariableSelects.Companion.OnAdblockSelects.ON.name,
                    SettingVariableSelects.Companion.OnAdblockSelects.OFF.name,
                ),
            )

            terminalFragment.fontZoomPercent =  MakeVariableNumValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM,
                terminalFragment.fontZoomPercent,
                "1"
            )

            terminalFragment.runShell =  MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_RUN_SHELL,
                terminalFragment.runShell
            )

            terminalFragment.binding.terminalWebView.settings.textZoom =
                terminalFragment.fontZoomPercent

            terminalFragment.onHistoryUrlTitle = CommandClickVariables.substituteCmdClickVariable(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_ON_HISTORY_URL_TITLE
            ) ?: terminalFragment.onHistoryUrlTitle
            FirstUrlHistoryFile.delete(
                terminalFragment,
                terminalFragment.currentAppDirPath
            )

            terminalFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_COLOR,
                terminalFragment.terminalColor
            )

            terminalFragment.terminalFontColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_FONT_COLOR,
                terminalFragment.terminalFontColor
            )
        }
    }
}