package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FirstUrlHistoryFile
import com.puutaro.commandclick.proccess.filer.StartFileMaker
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.state.SharePreferenceMethod


object ConfigFromStartUpFileSetterForTerm {

    fun set(
        terminalFragment: TerminalFragment,
    ){
        val activity = terminalFragment.activity
        val sharePref = activity?.getPreferences(Context.MODE_PRIVATE)
        CommandClickScriptVariable.makeConfigJsFile(
            UsePath.cmdclickSystemAppDirPath,
            UsePath.cmdclickConfigFileName
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
            CommandClickVariables.makeScriptContentsList(
                UsePath.cmdclickSystemAppDirPath,
                UsePath.cmdclickConfigFileName
            ),
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

        terminalFragment.onTermBackendWhenStart = SettingVariableReader.getCbValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE,
            SettingVariableSelects.OnTermBackendWhenStartSelects.INHERIT.name,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name,
                SettingVariableSelects.OnTermBackendWhenStartSelects.OFF.name,
            ),
        )

        terminalFragment.onTermShortWhenLoad = SettingVariableReader.getCbValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE,
            SettingVariableSelects.OnTermShortWhenLoadSelects.INHERIT.name,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.OnTermShortWhenLoadSelects.ON.name,
                SettingVariableSelects.OnTermShortWhenLoadSelects.OFF.name,
            ),
        )

        terminalFragment.disableShowToolbarWhenHighlight = SettingVariableReader.getCbValue(
            settingVariableListFromConfig,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE,
            SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.INHERIT.name,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE,
            listOf(
                SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.ON.name,
                SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.OFF.name,
            ),
        )

        terminalFragment.fontZoomPercent = SettingVariableReader.getNumValue(
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

        val currentScriptFileNameSource = SharePreferenceMethod.getStringFromSharePreference(
            sharePref,
            SharePrefferenceSetting.current_fannel_name
        )

        StartFileMaker.makeForStartupScript(
            terminalFragment,
            terminalFragment.currentAppDirPath
        )
        val cmdclickStartupJsName = UsePath.cmdclickStartupJsName
        val currentScriptFileName = if (
            terminalFragment.tag ==
            terminalFragment.context?.getString(
                R.string.index_terminal_fragment
            )
        ) cmdclickStartupJsName
        else currentScriptFileNameSource

        if(
            currentScriptFileName != cmdclickStartupJsName
        ){
            val settingVariableListFromStartup = CommandClickVariables.substituteVariableListFromHolder(
                CommandClickVariables.makeScriptContentsList(
                    terminalFragment.currentAppDirPath,
                    cmdclickStartupJsName
                ),
                settingSectionStart,
                settingSectionEnd
            )
            terminalFragment.srcImageAnchorLongPressMenuFilePath = ScriptPreWordReplacer.replace(
                UsePath.srcImageAnchorLongPressMenuFilePath,
                terminalFragment.currentAppDirPath,
                cmdclickStartupJsName,
            )

            terminalFragment.srcAnchorLongPressMenuFilePath = ScriptPreWordReplacer.replace(
                UsePath.srcAnchorLongPressMenuFilePath,
                terminalFragment.currentAppDirPath,
                cmdclickStartupJsName,
            )

            terminalFragment.imageLongPressMenuFilePath = ScriptPreWordReplacer.replace(
                UsePath.imageLongPressMenuFilePath,
                terminalFragment.currentAppDirPath,
                cmdclickStartupJsName,
            )

            terminalFragment.noScrollSaveUrls = SettingVariableReader.setListFromPath(
                ScriptPreWordReplacer.replace(
                    UsePath.noScrollSaveUrlsFilePath,
                    terminalFragment.currentAppDirPath,
                    cmdclickStartupJsName,
                )
            )

            terminalFragment.defaultMonitorFile = SettingVariableReader.getCbValue(
                settingVariableListFromStartup,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
                listOf(
                    UsePath.cmdClickMonitorFileName_2,
                    UsePath.cmdClickMonitorFileName_3,
                    UsePath.cmdClickMonitorFileName_4,
                ),
            )
        }
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            CommandClickVariables.makeScriptContentsList(
                terminalFragment.currentAppDirPath,
                currentScriptFileName
            ),
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

        terminalFragment.onTermBackendWhenStart = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START,
            terminalFragment.onTermBackendWhenStart,
            SettingVariableSelects.OnTermBackendWhenStartSelects.INHERIT.name,
            terminalFragment.onTermBackendWhenStart,
            listOf(
                SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name,
                SettingVariableSelects.OnTermBackendWhenStartSelects.OFF.name,
            ),
        )

        terminalFragment.onTermShortWhenLoad = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD,
            terminalFragment.onTermShortWhenLoad,
            SettingVariableSelects.OnTermShortWhenLoadSelects.INHERIT.name,
            terminalFragment.onTermShortWhenLoad,
            listOf(
                SettingVariableSelects.OnTermShortWhenLoadSelects.ON.name,
                SettingVariableSelects.OnTermShortWhenLoadSelects.OFF.name,
            ),
        )

        terminalFragment.disableShowToolbarWhenHighlight = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT,
            terminalFragment.disableShowToolbarWhenHighlight,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE,
            terminalFragment.disableShowToolbarWhenHighlight,
            listOf(
                SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.ON.name,
                SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.OFF.name,
            ),
        )

        terminalFragment.defaultMonitorFile = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
            listOf(
                UsePath.cmdClickMonitorFileName_2,
                UsePath.cmdClickMonitorFileName_3,
                UsePath.cmdClickMonitorFileName_4,
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
        val isSrcImageAnchorLongPressMenuFilePathVal = SettingVariableReader.isExist(
            settingVariableList,
            CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        )
        if(
            isSrcImageAnchorLongPressMenuFilePathVal
        ) terminalFragment.srcImageAnchorLongPressMenuFilePath =
            ScriptPreWordReplacer.replace(
                UsePath.srcImageAnchorLongPressMenuFilePath,
                terminalFragment.currentAppDirPath,
                currentScriptFileName,
            )

        val isSrcAnchorLongPressMenuFilePathVal = SettingVariableReader.isExist(
            settingVariableList,
            CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        )
        if(
            isSrcAnchorLongPressMenuFilePathVal
        ) terminalFragment.srcAnchorLongPressMenuFilePath =
            ScriptPreWordReplacer.replace(
                UsePath.srcAnchorLongPressMenuFilePath,
                terminalFragment.currentAppDirPath,
                currentScriptFileName,
            )


        val isImageLongPressMenuFilePathVal = SettingVariableReader.isExist(
            settingVariableList,
            CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
        )
        if(
            isImageLongPressMenuFilePathVal
        ) terminalFragment.imageLongPressMenuFilePath =
                ScriptPreWordReplacer.replace(
                    UsePath.imageLongPressMenuFilePath,
                    terminalFragment.currentAppDirPath,
                    currentScriptFileName,
                )

        val noScrollSaveUrls = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.noScrollSaveUrlsFilePath,
                terminalFragment.currentAppDirPath,
                currentScriptFileName,
            )
        )
        if(
            noScrollSaveUrls.isNotEmpty()
        ) terminalFragment.noScrollSaveUrls =
            noScrollSaveUrls
    }
}