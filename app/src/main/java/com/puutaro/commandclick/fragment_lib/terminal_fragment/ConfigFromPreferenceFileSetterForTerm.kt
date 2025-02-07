package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressPathDecider
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.sd.SdCardTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File


object ConfigFromPreferenceFileSetterForTerm {

    fun set(
        terminalFragment: TerminalFragment,
    ){
        val context = terminalFragment.context
        val cmdclickPreferenceJsName = SystemFannel.preference
//        CmdClickSystemFannelManager.createConfigFannel(
//            context
//        )
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
//        runBlocking {
//            CmdClickSystemFannelManager.createPreferenceFannel(
//                context,
//            )
//        }
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val preferenceConList = when(
            File(UsePath.cmdclickDefaultAppDirPath, cmdclickPreferenceJsName).isFile
        ) {
            false ->
                CommandClickVariables.makeMainFannelConListFromUrl(
                    context,
                    cmdclickPreferenceJsName
                )
            else ->
                CommandClickVariables.makeMainFannelConList(
//                currentAppDirPath,
                    cmdclickPreferenceJsName
                )

        }
        val settingVariableListFromPreference = CommandClickVariables.extractValListFromHolder(
            preferenceConList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
//            settingSectionStart,
//            settingSectionEnd
        )

        terminalFragment.onAdBlock = SettingVariableReader.getCbValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.ON_ADBLOCK,
            CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE,
            SettingVariableSelects.OnAdblockSelects.INHERIT.name,
            CommandClickScriptVariable.ON_ADBLOCK_DEFAULT_VALUE,
            sequenceOf(
                SettingVariableSelects.OnAdblockSelects.ON.name,
                SettingVariableSelects.OnAdblockSelects.OFF.name,
            ),
        )

        terminalFragment.onTermBackendWhenStart = SettingVariableReader.getCbValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE,
            SettingVariableSelects.OnTermBackendWhenStartSelects.INHERIT.name,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE,
            sequenceOf(
                SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name,
                SettingVariableSelects.OnTermBackendWhenStartSelects.OFF.name,
            ),
        )

        terminalFragment.onTermShortWhenLoad = SettingVariableReader.getCbValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE,
            SettingVariableSelects.OnTermShortWhenLoadSelects.INHERIT.name,
            CommandClickScriptVariable.ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE,
            sequenceOf(
                SettingVariableSelects.OnTermShortWhenLoadSelects.ON.name,
                SettingVariableSelects.OnTermShortWhenLoadSelects.OFF.name,
            ),
        )

        terminalFragment.disableShowToolbarWhenHighlight = SettingVariableReader.getCbValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE,
            SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.INHERIT.name,
            CommandClickScriptVariable.DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE,
            sequenceOf(
                SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.ON.name,
                SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.OFF.name,
            ),
        )

        terminalFragment.fontZoomPercent = SettingVariableReader.getNumValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM,
            CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE,
            "1"
        )

        terminalFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.TERMINAL_COLOR,
            CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
        )
        val onRootfsSdCardSaveSelectsOn = SettingVariableSelects.OnRootfsSdCardSaveSelects.ON.name
        SettingVariableReader.getCbValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.ON_ROOTFS_SDCARD_SAVE,
            String(),
            String(),
            String(),
            sequenceOf(onRootfsSdCardSaveSelectsOn),
        ).let {
            val isRootfsSdCardSave =
                it == onRootfsSdCardSaveSelectsOn
            val isCreate =
                isRootfsSdCardSave
                        && SdCardTool.isAvailable(context)
            val listener =
                context as? TerminalFragment.GetSdcardDirListenerForTerm
            listener?.getSdcardDirForTerm(isCreate)
        }

        terminalFragment.terminalFontColor = SettingVariableReader.getStrValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.TERMINAL_FONT_COLOR,
            CommandClickScriptVariable.TERMINAL_FONT_COLOR_DEFAULT_VALUE
        )

        terminalFragment.srcImageAnchorLongPressMenuFilePath =
            LongPressPathDecider.decide(
//                    currentAppDirPath,
                cmdclickPreferenceJsName,
                settingVariableListFromPreference,
                CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
                terminalFragment.setReplaceVariableMap,
            )

        terminalFragment.srcAnchorLongPressMenuFilePath =
            LongPressPathDecider.decide(
//                    currentAppDirPath,
                cmdclickPreferenceJsName,
                settingVariableListFromPreference,
                CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
                terminalFragment.setReplaceVariableMap,
            )
        terminalFragment.imageLongPressMenuFilePath =
            LongPressPathDecider.decide(
//                    currentAppDirPath,
                cmdclickPreferenceJsName,
                settingVariableListFromPreference,
                CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
                terminalFragment.setReplaceVariableMap,
            )

        terminalFragment.noScrollSaveUrls = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.noScrollSaveUrlsFilePath,
//                    terminalFragment.currentAppDirPath,
                cmdclickPreferenceJsName,
            )
        )

        terminalFragment.defaultMonitorFile = SettingVariableReader.getCbValue(
            settingVariableListFromPreference,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
            CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
            sequenceOf(
                UsePath.cmdClickMonitorFileName_2,
                UsePath.cmdClickMonitorFileName_3,
                UsePath.cmdClickMonitorFileName_4,
            ),
        ).let {
            if(
                MonitorFileManager.monitorFileList.contains(it)
            ) return@let it
            UsePath.cmdClickMonitorFileName_1
        }
        val currentFannelName = terminalFragment.currentFannelName
        if(
           FannelInfoTool.isEmptyFannelName(currentFannelName)
           || currentFannelName == cmdclickPreferenceJsName
        ) {
            terminalFragment.terminalOn = SettingVariableReader.getStrValue(
                settingVariableListFromPreference,
                CommandClickScriptVariable.TERMINAL_DO,
                CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
            )
            terminalFragment.ignoreHistoryPathList = ListSettingVariableListMaker.makeFromSettingVariableList(
                context,
                CommandClickScriptVariable.IGNORE_HISTORY_PATHS,
                terminalFragment.fannelInfoMap,
                terminalFragment.setReplaceVariableMap,
                settingVariableListFromPreference,
            )
            terminalFragment.onLaunchUrlHistoryByBackstack = SettingVariableReader.getCbValue(
                settingVariableListFromPreference,
                CommandClickScriptVariable.ON_LAUNCH_URL_HISTORY_BY_BACKSTACK,
                terminalFragment.onLaunchUrlHistoryByBackstack,
                terminalFragment.onLaunchUrlHistoryByBackstack,
                terminalFragment.onLaunchUrlHistoryByBackstack,
                sequenceOf(
                    SettingVariableSelects.OnLaunchUrlHistoryByBackstack.ON.name,
                    SettingVariableSelects.OnLaunchUrlHistoryByBackstack.OFF.name,
                ),
            )
            terminalFragment.defaultMonitorFile = SettingVariableReader.getCbValue(
                settingVariableListFromPreference,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
                sequenceOf(
                    UsePath.cmdClickMonitorFileName_2,
                    UsePath.cmdClickMonitorFileName_3,
                    UsePath.cmdClickMonitorFileName_4,
                ),
            ).let {
                if(
                    MonitorFileManager.monitorFileList.contains(it)
                ) return@let it
                UsePath.cmdClickMonitorFileName_1
            }
            terminalFragment.onUrlHistoryRegister = SettingVariableReader.getCbValue(
                settingVariableListFromPreference,
                CommandClickScriptVariable.ON_URL_HISTORY_REGISTER,
                CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
                CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
                CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
                sequenceOf(
                    SettingVariableSelects.OnUrlHistoryRegisterSelects.ON.name,
                    SettingVariableSelects.OnUrlHistoryRegisterSelects.OFF.name,
                ),
            )
            return
        }
//        val currentScriptFileName = ValidFannelNameGetterForTerm.get(
//            terminalFragment
//        )

//        if(
//            currentScriptFileName != cmdclickPreferenceJsName
//        ){
//            val settingVariableListFromPreference = CommandClickVariables.extractValListFromHolder(
//                CommandClickVariables.makeMainFannelConList(
////                    terminalFragment.currentAppDirPath,
//                    cmdclickPreferenceJsName
//                ),
//                settingSectionStart,
//                settingSectionEnd
//            )
//            terminalFragment.srcImageAnchorLongPressMenuFilePath =
//                LongPressPathDecider.decide(
//                    terminalFragment,
////                    currentAppDirPath,
//                    cmdclickPreferenceJsName,
//                    settingVariableListFromPreference,
//                    CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
//                )
//
//            terminalFragment.srcAnchorLongPressMenuFilePath =
//                LongPressPathDecider.decide(
//                    terminalFragment,
////                    currentAppDirPath,
//                    cmdclickPreferenceJsName,
//                    settingVariableListFromPreference,
//                    CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
//                )
//            terminalFragment.imageLongPressMenuFilePath =
//                LongPressPathDecider.decide(
//                    terminalFragment,
////                    currentAppDirPath,
//                    cmdclickPreferenceJsName,
//                    settingVariableListFromPreference,
//                    CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
//                )

//            terminalFragment.noScrollSaveUrls = SettingVariableReader.setListFromPath(
//                ScriptPreWordReplacer.replace(
//                    UsePath.noScrollSaveUrlsFilePath,
////                    terminalFragment.currentAppDirPath,
//                    cmdclickPreferenceJsName,
//                )
//            )

//            terminalFragment.defaultMonitorFile = SettingVariableReader.getCbValue(
//                settingVariableListFromPreference,
//                CommandClickScriptVariable.DEFAULT_MONITOR_FILE,
//                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
//                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
//                CommandClickScriptVariable.DEFAULT_MONITOR_FILE_DEFAULT_VALUE,
//                listOf(
//                    UsePath.cmdClickMonitorFileName_2,
//                    UsePath.cmdClickMonitorFileName_3,
//                    UsePath.cmdClickMonitorFileName_4,
//                ),
//            ).let {
//                if(
//                    MonitorFileManager.monitorFileList.contains(it)
//                ) return@let it
//                UsePath.cmdClickMonitorFileName_1
//            }
//        }
        val fannelContentsList = CommandClickVariables.makeMainFannelConList(
//            terminalFragment.currentAppDirPath,
            currentFannelName
        )
        val settingVariableList = makeSettingVariableListForTerm(
            terminalFragment,
            fannelContentsList,
            currentFannelName,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
//            settingSectionStart,
//            settingSectionEnd,
        )
        terminalFragment.terminalOn = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_DO,
            CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
        )
        terminalFragment.ignoreHistoryPathList = ListSettingVariableListMaker.makeFromSettingVariableList(
            context,
            CommandClickScriptVariable.IGNORE_HISTORY_PATHS,
            terminalFragment.fannelInfoMap,
            terminalFragment.setReplaceVariableMap,
            settingVariableList ?: emptyList(),
        )
        terminalFragment.onAdBlock = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_ADBLOCK,
            terminalFragment.onAdBlock,
            SettingVariableSelects.OnAdblockSelects.INHERIT.name,
            terminalFragment.onAdBlock,
            sequenceOf(
                SettingVariableSelects.OnAdblockSelects.ON.name,
                SettingVariableSelects.OnAdblockSelects.OFF.name,
            ),
        )
        terminalFragment.onLaunchUrlHistoryByBackstack = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_LAUNCH_URL_HISTORY_BY_BACKSTACK,
            terminalFragment.onLaunchUrlHistoryByBackstack,
            terminalFragment.onLaunchUrlHistoryByBackstack,
            terminalFragment.onLaunchUrlHistoryByBackstack,
            sequenceOf(
                SettingVariableSelects.OnLaunchUrlHistoryByBackstack.ON.name,
                SettingVariableSelects.OnLaunchUrlHistoryByBackstack.OFF.name,
            ),
        )

        terminalFragment.onTermBackendWhenStart = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_BACKEND_WHEN_START,
            terminalFragment.onTermBackendWhenStart,
            SettingVariableSelects.OnTermBackendWhenStartSelects.INHERIT.name,
            terminalFragment.onTermBackendWhenStart,
            sequenceOf(
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
            sequenceOf(
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
            sequenceOf(
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
            sequenceOf(
                UsePath.cmdClickMonitorFileName_2,
                UsePath.cmdClickMonitorFileName_3,
                UsePath.cmdClickMonitorFileName_4,
            ),
        ).let {
            if(
                MonitorFileManager.monitorFileList.contains(it)
            ) return@let it
            UsePath.cmdClickMonitorFileName_1
        }

        terminalFragment.onUrlHistoryRegister = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE,
            sequenceOf(
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

        terminalFragment.binding.terminalWebView.settings.textZoom =
            terminalFragment.fontZoomPercent

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
                LongPressPathDecider.decide(
//                    currentAppDirPath,
                    currentFannelName,
                    settingVariableList,
                    CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
                    terminalFragment.setReplaceVariableMap,
                )

        val isSrcAnchorLongPressMenuFilePathVal = SettingVariableReader.isExist(
            settingVariableList,
            CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        )
        if(
            isSrcAnchorLongPressMenuFilePathVal
        ) terminalFragment.srcAnchorLongPressMenuFilePath =
            LongPressPathDecider.decide(
//                currentAppDirPath,
                currentFannelName,
                settingVariableList,
                CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
                terminalFragment.setReplaceVariableMap,
            )

        val isImageLongPressMenuFilePathVal = SettingVariableReader.isExist(
            settingVariableList,
            CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
        )
        if(
            isImageLongPressMenuFilePathVal
        ) terminalFragment.imageLongPressMenuFilePath =
            LongPressPathDecider.decide(
//                currentAppDirPath,
                currentFannelName,
                settingVariableList,
                CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
                terminalFragment.setReplaceVariableMap,
            )

        val noScrollSaveUrls = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.noScrollSaveUrlsFilePath,
//                terminalFragment.currentAppDirPath,
                currentFannelName,
            )
        )
        if(
            noScrollSaveUrls.isNotEmpty()
        ) terminalFragment.noScrollSaveUrls =
            noScrollSaveUrls
    }
}

private fun makeSettingVariableListForTerm(
    terminalFragment: TerminalFragment,
    fannelContentsList: List<String>,
    currentScriptFileName: String,
    settingSectionStart: String,
    settingSectionEnd: String,
): List<String>? {
    val isIndexTerminal =
        terminalFragment.tag == terminalFragment.context?.getString(
        R.string.index_terminal_fragment
    )
    val isPreferenceScript =
        currentScriptFileName == SystemFannel.preference
    if(
        isIndexTerminal
        || isPreferenceScript
    ) return CommandClickVariables.extractValListFromHolder(
        fannelContentsList,
        settingSectionStart,
        settingSectionEnd
    )
    return FannelStateRooterManager.makeSettingVariableList(
        terminalFragment.context,
        terminalFragment.fannelInfoMap,
        terminalFragment.setReplaceVariableMap,
        settingSectionStart,
        settingSectionEnd,
        terminalFragment.settingFannelPath
    )
}
