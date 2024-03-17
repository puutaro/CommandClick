package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.RecordNumToMapNameValueInHolderMaker
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.AlterConfig
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonIconSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonVisibleSettingForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object ConfigFromScriptFileSetter {

    fun set(
        editFragment: EditFragment,
        mainFannelConList: List<String>
    ){
        val context = editFragment.context
            ?: return
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context)
        )
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val onShortcut = SharePrefTool.getOnShortcut(
            readSharePreferenceMap
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val settingVariableList = FannelStateRooterManager.makeSettingVariableList(
            readSharePreferenceMap,
            setReplaceVariableMap,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
            editFragment.settingFannelPath
        )
        editFragment.settingFannelConList = settingVariableList

        editFragment.setVariableTypeList =
            SetVariableTypesSetterForEdit.set(
                editFragment,
                readSharePreferenceMap,
                settingVariableList,
            )
        editFragment.hideSettingVariableList =
            ListSettingVariableListMaker.makeFromSettingVariableList(
                CommandClickScriptVariable.HIDE_SETTING_VARIABLES,
                readSharePreferenceMap,
                editFragment.setReplaceVariableMap,
                settingVariableList
            )
        editFragment.recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolderMaker.makeForSetting(editFragment)
        editFragment.recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolderMaker.makeForCmdHolder(
                editFragment,
                mainFannelConList
            )
        editFragment.existIndexList =
            judgeExistListIndex(
                editFragment,
            )

        editFragment.editBoxTitleConfig = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.EDIT_TITLE_CONFIG,
            settingVariableList,
            readSharePreferenceMap,
            setReplaceVariableMap,
            String(),
        ).let {
            AlterToolForSetValType.updateConfigMapByAlter(
                context,
                it,
                busyboxExecutor,
                setReplaceVariableMap
            )
        }
        TitleImageAndViewSetter.set(
            editFragment
        )
        setToolbarButtonConfigMapFromSettingValList(
            editFragment,
            busyboxExecutor,
            settingVariableList,
            onShortcut,
        )

        editFragment.editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        editFragment.listIndexConfigMap = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.LIST_INDEX_CONFIG,
            settingVariableList,
            readSharePreferenceMap,
            setReplaceVariableMap,
            String(),
        ).let {
            AlterToolForSetValType.updateConfigMapByAlter(
                context,
                it,
                busyboxExecutor,
                setReplaceVariableMap
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "getfile_index_list_config_in_init.txt").absolutePath,
//            listOf(
//                "",
//                "listIndexConfigMap: ${editFragment.listIndexConfigMap}",
//            ).joinToString("\n\n\n")
//        )
        editFragment.qrDialogConfig = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.QR_DIALOG_CONFIG,
            settingVariableList,
            readSharePreferenceMap,
            setReplaceVariableMap,
            String(),
        ).let {
            AlterToolForSetValType.updateConfigMapByAlter(
                context,
                it,
                busyboxExecutor,
                setReplaceVariableMap
            )
        }


        if (
            !onShortcut
        ) {
            setButtonVisible(
                editFragment,
                onShortcut
            )
            return
        }

        editFragment.onTermVisibleWhenKeyboard = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD,
            editFragment.onTermVisibleWhenKeyboard,
            SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.INHERIT.name,
            editFragment.onTermVisibleWhenKeyboard,
            listOf(
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name,
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
            ),
        )

        editFragment.historySwitch = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HISTORY_SWITCH,
            editFragment.historySwitch,
            SettingVariableSelects.HistorySwitchSelects.INHERIT.name,
            editFragment.historySwitch,
            listOf(
                SettingVariableSelects.HistorySwitchSelects.OFF.name,
                SettingVariableSelects.HistorySwitchSelects.ON.name
            ),
        )

        editFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            editFragment.urlHistoryOrButtonExec,
            SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name,
            editFragment.urlHistoryOrButtonExec,
            listOf(
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
            ),
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

        editFragment.passCmdVariableEdit = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT,
            String(),
        )

        setButtonVisible(
            editFragment,
            onShortcut
        )
        setEditToolBarButtonIcon(
            editFragment,
            onShortcut
        )

        val bottomScriptUrlList = makeListFromSettingPath(
            editFragment,
            settingVariableList,
            CommandClickScriptVariable.HOME_SCRIPT_URLS_PATH,
            UsePath.homeScriptUrlsFilePath,
        )
        if(
            bottomScriptUrlList.isNotEmpty()
        ) editFragment.bottomScriptUrlList = bottomScriptUrlList


        val homeFannelHistoryNameList = makeListFromSettingPath(
            editFragment,
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_HOME_FANNELS_PATH,
            UsePath.homeFannelsFilePath,
        )
        if(
            homeFannelHistoryNameList.isNotEmpty()
        ) editFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

        if(
            editFragment.tag?.startsWith(
                FragmentTagPrefix.Prefix.SETTING_VAL_EDIT_PREFIX.str
            ) == true
        ) return
        editFragment.terminalOn = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_DO,
            CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE

        )

        editFragment.onNoUrlSaveMenu = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.NO_SCROLL_SAVE_URLS,
            String()
        ).isNotEmpty()

        editFragment.onUpdateLastModify = !(
                SettingVariableReader.getStrValue(
                    settingVariableList,
                    CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY,
                    String()
                ) == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
                )
    }

    private fun judgeExistListIndex(
        editFragment: EditFragment,
    ): Boolean {
        val isSetting = !IsCmdEdit.judge(editFragment)
//            editFragment.editTypeSettingKey ==
//                    EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT

        if(
            isSetting
        ) return false
//        val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
//            editFragment.recordNumToMapNameValueInSettingHolder,
//            currentAppDirPath,
//            currentScriptFileName,
//            editFragment.setReplaceVariableMap
//        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setVal.txt").absolutePath,
//           listOf(
//               "recordNumToMapNameValueInSettingHolder: ${editFragment.recordNumToMapNameValueInSettingHolder}",
//               "setVariableTypeList: ${editFragment.setVariableTypeList?.joinToString("\n")}"
//           ).joinToString("\n\n")
//        )
//        val listIndexHeadRegex = Regex(
//            "^[a-zA-Z0-9]+:${EditTextSupportViewName.LIST_INDEX.str}="
//        )

        val setVariableTypeList =
            editFragment.setVariableTypeList
                ?: return false
        val setVariableTypeListCon =
            "\n" + setVariableTypeList.joinToString("\n")
        val listIndexNewlineRegex = Regex(
            "\n[a-zA-Z0-9]{1,}:${EditTextSupportViewName.LIST_INDEX.str}="
        )
        return listIndexNewlineRegex.containsMatchIn(setVariableTypeListCon)
    }

    private fun setButtonVisible(
        editFragment: EditFragment,
        onShortcut: Boolean
    ){
        editFragment.enableEditExecute =
            (editFragment.editExecuteValue ==
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    ) && onShortcut

        val enableCmdEdit = editFragment.enableCmdEdit

        val isSettingEdit = !enableCmdEdit
                || editFragment.passCmdVariableEdit ==
                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val isOnlyCmdEdit = enableCmdEdit
                && !editFragment.enableEditExecute
        val isCmdEditExecute = enableCmdEdit
                && editFragment.enableEditExecute
        editFragment.toolBarButtonVisibleMap.put(
            ToolbarButtonBariantForEdit.HISTORY,
            isCmdEditExecute
        )
        val buttonVisibleOn =
            ButtonVisibleSettingForToolbarButton.ButtonVisibleValue.ON.name
        val settingButtonVariant = ToolbarButtonBariantForEdit.SETTING
        editFragment.toolBarButtonVisibleMap.put(
            settingButtonVariant,
            when(true) {
                isSettingEdit,
                isOnlyCmdEdit -> false
                else -> {
                    val settingButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(
                            settingButtonVariant
                        )
                    settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.key
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let true
                        it == buttonVisibleOn
                    }
                }
            }
        )
        val editButtonVariant = ToolbarButtonBariantForEdit.EDIT
        editFragment.toolBarButtonVisibleMap.put(
            editButtonVariant,
            when (true) {
                isSettingEdit -> false
                isOnlyCmdEdit -> true
//                    !FDialogTempFile.howFDialogFile(currentFannelName)
                else -> {
                    val editButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(
                            editButtonVariant
                        )
                    editButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.key
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let true
                        it == buttonVisibleOn
                    }
                }
            }
        )
        val okButtonVariant = ToolbarButtonBariantForEdit.OK
        editFragment.toolBarButtonVisibleMap.put(
            okButtonVariant,
            when(true) {
                isSettingEdit -> true
                isOnlyCmdEdit -> true
                else -> {
                    val okButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(
                            okButtonVariant
                        )
                    okButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.key
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let true
                        it == buttonVisibleOn
                    }
                }
            }
        )
        val extraButtonVariant = ToolbarButtonBariantForEdit.EXTRA
        editFragment.toolBarButtonVisibleMap.put(
            extraButtonVariant,
            when(true) {
                isSettingEdit,
                isOnlyCmdEdit -> false
                else -> {
                    val settingButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(
                            extraButtonVariant
                        )
                    settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.key
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let false
                        it == buttonVisibleOn
                    }
                }
            }
        )
    }

    private fun setToolbarButtonConfigMapFromSettingValList(
        editFragment: EditFragment,
        busyboxExecutor: BusyboxExecutor?,
        settingVariableList: List<String>?,
        onShortcut: Boolean,
    ){
        val context = editFragment.context ?: return
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        editFragment.toolbarButtonConfigMap =
            mapOf(
                ToolbarButtonBariantForEdit.SETTING to execMakeToolbarButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.SETTING_BUTTON_CONFIG,
                    String(),
                    onShortcut,
                ).let {
                    AlterToolForSetValType.updateConfigMapByAlter(
                        context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                },
                ToolbarButtonBariantForEdit.EXTRA to execMakeToolbarButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.EXTRA_BUTTON_CONFIG,
                    String(),
                    onShortcut,
                ).let {
                    AlterToolForSetValType.updateConfigMapByAlter(
                        context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                },
                ToolbarButtonBariantForEdit.EDIT to execMakeToolbarButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.EDIT_BUTTON_CONFIG,
                    String(),
                    onShortcut,
                ).let {
                    AlterToolForSetValType.updateConfigMapByAlter(
                        context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                },
                ToolbarButtonBariantForEdit.OK to makePlayButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    onShortcut,
                ).let {
                    AlterToolForSetValType.updateConfigMapByAlter(
                        context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                }
            )
    }

    private fun setEditToolBarButtonIcon(
        editFragment: EditFragment,
        onShortcut: Boolean
    ){
        editFragment.enableEditExecute =
            (editFragment.editExecuteValue ==
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    ) && onShortcut

        val enableCmdEdit = editFragment.enableCmdEdit

        val isSettingEdit = !enableCmdEdit
                || editFragment.passCmdVariableEdit ==
                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val isOnlyCmdEdit = enableCmdEdit
                && !editFragment.enableEditExecute
        val iconNameIdPairList = CmdClickIcons.values()
        val defaultSettingButtonIconId =
            R.drawable.icons8_setting
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.SETTING,
            when(true) {
                isSettingEdit,
                isOnlyCmdEdit ->
                    defaultSettingButtonIconId
                else -> {
                    val settingButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.SETTING)
                    val selectedIconName = settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.SETTING.str
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultSettingButtonIconId
                }
            }
        )
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.EXTRA,
            let {
                    val settingButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.EXTRA)
                    val selectedIconName = settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.EXTRA.str
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultSettingButtonIconId
                }
        )
        val defaultOkButtonIconId =
            R.drawable.icons8_check_ok
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.OK,
            when(true) {
                isSettingEdit ->
                    defaultOkButtonIconId
                isOnlyCmdEdit ->
                    defaultOkButtonIconId
                else -> {
                    val okButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.OK)
                    val selectedIconName = okButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.PLAY.str
                    val defaultIconId = R.drawable.icons_play
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultIconId
                }
            }
        )
        val defaultEditButtonIconId =
            R.drawable.icons8_edit
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.EDIT,
            when(true) {
                isSettingEdit ->
                    defaultEditButtonIconId
                isOnlyCmdEdit ->
                    defaultEditButtonIconId
                else -> {
                    val editButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.EDIT)
                    val selectedIconName = editButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.EDIT.str
                    val defaultIconId = R.drawable.icons8_check_ok
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultIconId
                }
            }
        )
    }

    private fun execMakeToolbarButtonConfigMap(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        targetSettingConfigValName: String,
        defaultButtonConfigCon: String,
        onShortcut: Boolean,
    ): Map<String, String> {
        if(
            !onShortcut
        ) return mapOf()
        return ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            targetSettingConfigValName,
            settingVariableList,
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
            defaultButtonConfigCon,
        )
    }

    private fun makePlayButtonConfigMap(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        onShortcut: Boolean,
    ): Map<String, String> {
        if(!onShortcut){
            return mapOf()
        }
        val isSettingEdit = !editFragment.enableCmdEdit
                || editFragment.passCmdVariableEdit ==
                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        return when(isSettingEdit) {
            true -> mapOf(
                SettingButtonConfigMapKey.CLICK.key to
                        listOf(
                            JsActionKeyManager.JsActionsKey.JS_PATH.key,
                            MacroForToolbarButton.Macro.OK.name
                        ).joinToString("=")
            )
            else -> ListSettingVariableListMaker.makeConfigMapFromSettingValList(
                CommandClickScriptVariable.PLAY_BUTTON_CONFIG,
                settingVariableList,
                editFragment.readSharePreferenceMap,
                editFragment.setReplaceVariableMap,
                String(),
            )
        }
    }

    private fun makeListFromSettingPath(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        settingValName: String,
        defaultPath: String,
    ): List<String> {
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val bottomScriptUrlPath =  SettingVariableReader.getStrValue(
            settingVariableList,
            settingValName,
            defaultPath,
        ).let {
            val repPath = when(it.isEmpty()){
                true -> defaultPath
                else -> it
            }
            SetReplaceVariabler.execReplaceByReplaceVariables(
                repPath,
                editFragment.setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        }
        return SettingVariableReader.setListFromPath(
            bottomScriptUrlPath
        )
    }
}

private object AlterToolForSetValType {

    private val alterKeyName = AlterConfig.alterKeyName
    private const val mainSeparator = '|'
    private const val ifArgsSeparator = '!'

    fun updateConfigMapByAlter(
        context: Context,
        configMap: Map<String, String>,
        busyboxExecutor: BusyboxExecutor?,
        replaceVariableMap: Map<String, String>?
    ): Map<String, String> {
        if(
            busyboxExecutor == null
        ) return configMap
        val alterKeyEqualStr = "${alterKeyName}="
        return configMap.map {
                configMapEntry ->
            val currentConfigKey = configMapEntry.key
            val currentConfigValue = configMapEntry.value
            val defaultConfigPair =
                currentConfigKey to currentConfigValue
            val currentConfigValueList = QuoteTool.splitBySurroundedIgnore(
                currentConfigValue,
                mainSeparator
            )
            val alterTypeValue = currentConfigValueList.firstOrNull {
                it.startsWith(alterKeyEqualStr)
            }
            if(
                alterTypeValue.isNullOrEmpty()
            ) return@map defaultConfigPair
            val alterValue = QuoteTool.trimBothEdgeQuote(
                alterTypeValue.removePrefix(
                    alterKeyEqualStr
                ).trim()
            )
            val alterMap = makeAlterMap(
                alterValue,
                replaceVariableMap
            )
            val shellIfOutput = getShellIfOutput(
                context,
                alterMap,
                replaceVariableMap,
                busyboxExecutor,
            )
            val disableAlter = shellIfOutput.isEmpty()
            if(
                disableAlter
            ) return@map currentConfigKey to QuoteTool.splitBySurroundedIgnore(
                currentConfigValue,
                mainSeparator
            ).filter {
                !it.startsWith(alterKeyEqualStr)
            }.joinToString(mainSeparator.toString())
            val updateConfigValue = execAlter(
                currentConfigValueList,
                alterMap,
                alterValue,
                shellIfOutput,
            )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "setValMap_makeVariableTypeValueByalter.txt").absolutePath,
//                    listOf(
//                        "alterTypeValue: ${alterTypeValue}",
//                        "alterValue: ${alterValue}",
//                        "alterMap: ${alterMap}",
//                        "updateTypeValue: ${updateTypeValue}",
//                    ).joinToString("\n\n-------\n")
//                )
            currentConfigKey to updateConfigValue
        }.toMap()
    }

    private fun execAlter(
        currentConfigValueList: List<String>,
        alterMapSrc: Map<String, String>,
        alterValue: String,
        shellIfOutput: String,
    ): String {
        val alterIfKeyList =
            AlterConfig.IfKey.values().map{ it.key }
        val alterMap =
            alterMapSrc +
                    CmdClickMap.createMap(
                        shellIfOutput,
                        mainSeparator
                    )
        val alterMapKeyList = alterMap.keys
        val currentConfigValueListWithAlterKeyRemove =
            currentConfigValueList.map {
                    configValue ->
                val alterKey = alterMapKeyList.firstOrNull {
                    configValue.startsWith("${it}=")
                }
                if(
                    alterKey.isNullOrEmpty()
                ) return@map configValue
    //                FileSystems.updateFile(
    //                    File(UsePath.cmdclickDefaultAppDirPath, "setValMap_exec_alter.txt").absolutePath,
    //                    listOf(
    //                        "alterMapKeyList: ${alterMapKeyList}",
    //                        "typeValue: ${typeValue}",
    //                        "alterKey: ${alterKey}",
    //                    ).joinToString("\n\n-------\n")
    //                )
                String()
            }.joinToString(mainSeparator.toString())
        val alterValueExcludeIfKey = QuoteTool.splitBySurroundedIgnore(
            alterValue,
            mainSeparator
        ).filter {
            !alterIfKeyList.contains(it)
        }.joinToString(mainSeparator.toString())
        return listOf(
            currentConfigValueListWithAlterKeyRemove,
            alterValueExcludeIfKey,
            shellIfOutput,
        ).joinToString(mainSeparator.toString())
    }

    private fun getShellIfOutput(
        context: Context,
        alterMap: Map<String, String>,
        replaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor,
    ): String {
        val shellIfCon = makeShellIfCon(
            context,
            alterMap,
            replaceVariableMap,
        )
//            val repValHashMap = replaceVariableMap?.let {
//                HashMap(it)
//            }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_shellIf.txt").absolutePath,
//            listOf(
//                "alterMap: ${alterMap}",
//                "shellIfCon: ${shellIfCon}",
//                "alterMap: ${alterMap}",
//                "shellIfCon: ${shellIfCon}",
//                "val: ${busyboxExecutor.getCmdOutput(
//                    shellIfCon,
//                )}"
//            ).joinToString("\n\n\n")
//        )
        return busyboxExecutor.getCmdOutput(
            shellIfCon,
        ).trim()
    }

    private fun makeAlterMap(
        alterValue: String,
        replaceVariableMap: Map<String, String>?,
    ): Map<String, String> {
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            alterValue,
            replaceVariableMap,
            String(),
            String()
        ).let {
            CmdClickMap.createMap(
                it,
                mainSeparator,
            ).toMap()
        }
    }

    private fun makeShellIfCon(
        context: Context,
        alterMap: Map<String, String>?,
        replaceVariableMap: Map<String, String>?,
    ): String {
        if(
            alterMap.isNullOrEmpty()
        ) return String()
        val shellIfCon = alterMap.get(
            AlterConfig.IfKey.SHELL_IF_CON.key
        )
        if(
            !shellIfCon.isNullOrEmpty()
        ) return SetReplaceVariabler.execReplaceByReplaceVariables(
            shellIfCon,
            replaceVariableMap,
            String(),
            String(),
        )
        val shellPath = alterMap.get(
            AlterConfig.IfKey.SHELL_IF_PATH.key
        ) ?: return String()
        val extraRepValMap = CmdClickMap.createMap(
            alterMap.get(
                AlterConfig.IfKey.IF_ARGS.key
            ),
            ifArgsSeparator
        ).toMap()
        return ShellMacroHandler.makeShellCon(
            context,
            shellPath,
            replaceVariableMap,
            extraRepValMap,
        )
    }
}
