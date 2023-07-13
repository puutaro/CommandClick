package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FirstUrlHistoryFile
import com.puutaro.commandclick.proccess.StartUpScriptMaker
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
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
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String


        val settingVariableListFromConfig = CommandClickVariables.substituteVariableListFromHolder(
            ReadText(
                UsePath.cmdclickSystemAppDirPath,
                UsePath.cmdclickConfigFileName
            ).textToList(),
            settingSectionStart,
            settingSectionEnd
        )

        terminalFragment.onAdBlock = SettingVariableReader.getCbValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.ON_ADBLOCK,
            CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE,
            SettingVariableSelects.OnAdblockSelects.INHERIT.name,
            CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.OnAdblockSelects.ON.name,
                SettingVariableSelects.OnAdblockSelects.OFF.name,
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

        terminalFragment.srcImageAnchorLongPressMenuFilePath = SettingVariableReader.getStrValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
            String()
        )

        val currentScriptFileNameSource = SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.current_script_file_name
        )

        terminalFragment.currentScriptName = currentScriptFileNameSource

        StartUpScriptMaker.make(
            terminalFragment,
            terminalFragment.currentAppDirPath
        )
        val currentScriptFileName = if (
            terminalFragment.tag ==
            terminalFragment.context?.getString(
                R.string.index_terminal_fragment
            )
        ) UsePath.cmdclickStartupJsName
        else currentScriptFileNameSource


        val fannelDirName = currentScriptFileName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            ReadText(
                terminalFragment.currentAppDirPath,
                currentScriptFileName
            ).readText().let {
                ScriptPreWordReplacer.replace(
                    it,
                    terminalFragment.currentAppDirPath,
                    fannelDirName,
                    terminalFragment.currentScriptName,
                )
            }.split("\n"),
            settingSectionStart,
            settingSectionEnd
        )

        terminalFragment.terminalOn = CommandClickVariables.substituteCmdClickVariable(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_DO
        ) ?: CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE


        terminalFragment.ignoreHistoryPathList = ListSettingVariableListMaker.makeFromSettingVariableList(
            CommandClickScriptVariable.IGNORE_HISTORY_PATHS,
            terminalFragment.currentAppDirPath,
            currentScriptFileName,
            fannelDirName,
            settingVariableList ?: emptyList(),
        )
        terminalFragment.onAdBlock = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_ADBLOCK,
            terminalFragment.onAdBlock,
            SettingVariableSelects.OnAdblockSelects.INHERIT.name,
            terminalFragment.onAdBlock,
            listOf(
                SettingVariableSelects.OnAdblockSelects.ON.name,
                SettingVariableSelects.OnAdblockSelects.OFF.name,
            ),
        )

        terminalFragment.onUrlHistoryRegister = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.OnUrlHistoryRegisterSelects.ON.name,
                SettingVariableSelects.OnUrlHistoryRegisterSelects.OFF.name,
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

        terminalFragment.srcImageAnchorLongPressMenuFilePath = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
            String()
        )

        terminalFragment.srcAnchorLongPressMenuFilePath = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
            String()
        )

        terminalFragment.imageLongPressMenuFilePath = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
            String()
        )
    }
}