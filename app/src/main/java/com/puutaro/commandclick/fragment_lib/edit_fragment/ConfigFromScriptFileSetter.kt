package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
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
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.JsAcAlterIfTool
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonIconSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonVisibleSettingForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object ConfigFromScriptFileSetter {

    fun set(
        editFragment: EditFragment,
        mainFannelConList: List<String>
    ){
        val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
        val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
        val fannelInfoMap = editFragment.fannelInfoMap
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val onShortcut = FannelInfoTool.getOnShortcut(
            fannelInfoMap
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val settingVariableList = FannelStateRooterManager.makeSettingVariableList(
            fannelInfoMap,
            setReplaceVariableMap,
            settingSectionStart,
            settingSectionEnd,
            editFragment.settingFannelPath
        )
        editFragment.settingFannelConList = settingVariableList

//        editFragment.setVariableTypeList =
//            SetVariableTypesSetterForEdit.set(
//                editFragment,
//                fannelInfoMap,
//                settingVariableList,
//            )
//        editFragment.hideSettingVariableList =
//            ListSettingVariableListMaker.makeFromSettingVariableList(
//                CommandClickScriptVariable.HIDE_SETTING_VARIABLES,
//                fannelInfoMap,
//                editFragment.setReplaceVariableMap,
//                settingVariableList
//            )
//        editFragment.recordNumToMapNameValueInSettingHolder =
//            RecordNumToMapNameValueInHolderMaker.makeForSetting(editFragment)
//        editFragment.recordNumToMapNameValueInCommandHolder =
//            RecordNumToMapNameValueInHolderMaker.makeForCmdHolder(
//                editFragment,
//                mainFannelConList
//            )
//        editFragment.existIndexList =
//            judgeExistListIndex(
//                editFragment,
//            )

        editFragment.editBoxTitleConfig = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.EDIT_BOX_TITLE_CONFIG,
            settingVariableList,
            fannelInfoMap,
            setReplaceVariableMap,
            String(),
        ).let {
            AlterToolForSetValType.updateConfigMapByAlter(
                editFragment.context,
                it,
                editFragment.busyboxExecutor,
                setReplaceVariableMap
            )
        }
//        val binding = editFragment.binding
//        val editTextView = binding.editTextView
//        TitleImageAndViewSetter.set(
//            editFragment,
//            editTextView,
//            fannelInfoMap,
//            setReplaceVariableMap,
//            editFragment.busyboxExecutor,
//            binding.editTitleImage,
//            editFragment.editBoxTitleConfig
//        )

//        val isSettingEdit = !editFragment.enableCmdEdit
//                || editFragment.passCmdVariableEdit ==
//                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE


        setToolbarButtonConfigMapFromSettingValList(
            editFragment,
            editFragment.busyboxExecutor,
            settingVariableList,
            onShortcut,
//            isSettingEdit,
        )

        editFragment.editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        editFragment.listIndexConfigMap = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.LIST_INDEX_CONFIG,
            settingVariableList,
            fannelInfoMap,
            setReplaceVariableMap,
            String(),
        ).let {
            AlterToolForSetValType.updateConfigMapByAlter(
                editFragment.context,
                it,
                editFragment.busyboxExecutor,
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
//        editFragment.qrDialogConfig = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
//            CommandClickScriptVariable.QR_DIALOG_CONFIG,
//            settingVariableList,
//            fannelInfoMap,
//            setReplaceVariableMap,
//            String(),
//        ).let {
//            AlterToolForSetValType.updateConfigMapByAlter(
//                editFragment.context,
//                it,
//                editFragment.busyboxExecutor,
//                setReplaceVariableMap
//            )
//        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcAlter.txt").absolutePath,
//            listOf(
//                "qrDialogConfig: ${editFragment.qrDialogConfig}"
//            ).joinToString("\n")
//        )


        editFragment.enableEditExecute =
            (editFragment.editExecuteValue ==
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    ) && onShortcut
        val isOnlyCmdEdit = !editFragment.enableEditExecute
//        editFragment.enableCmdEdit
//                && !editFragment.enableEditExecute

        if (
            !onShortcut
        ) {
            setButtonVisible(
                editFragment,
                editFragment.enableEditExecute,
//                editFragment.enableCmdEdit,
//                isSettingEdit,
                isOnlyCmdEdit,
            )
            return
        }

        editFragment.onTermVisibleWhenKeyboard = SettingVariableReader.getCbValue(
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

//        editFragment.historySwitch = SettingVariableReader.getCbValue(
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
//
//        editFragment.urlHistoryOrButtonExec = SettingVariableReader.getCbValue(
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

//        editFragment.passCmdVariableEdit = SettingVariableReader.getStrValue(
//            settingVariableList,
//            CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT,
//            String(),
//        )

        editFragment.enableEditExecute =
            (editFragment.editExecuteValue ==
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    ) && onShortcut
//        val isSettingEditForOnShortcut = !editFragment.enableCmdEdit
//                || editFragment.passCmdVariableEdit ==
//                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val isOnlyCmdEditForShortcut = !editFragment.enableEditExecute
//        editFragment.enableCmdEdit
//                && !editFragment.enableEditExecute

        setButtonVisible(
            editFragment,
            editFragment.enableEditExecute,
//            editFragment.enableCmdEdit,
//            isSettingEditForOnShortcut,
            isOnlyCmdEditForShortcut,
        )
        setEditToolBarButtonIcon(
            editFragment,
//            isSettingEditForOnShortcut,
            isOnlyCmdEditForShortcut,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "state.txt").absolutePath,
//            listOf(
//                "readSharePreferenceMap: ${readSharePreferenceMap}",
//                "setReplaceVariableMap: ${setReplaceVariableMap}",
//                "settingVariableList: ${settingVariableList}",
//                "setVariableTypeList: ${SetVariableTypesSetterForEdit.set(
//                    editFragment,
//                    readSharePreferenceMap,
//                    settingVariableList,
//                )}",
//                "hideSettingVariableList: ${editFragment.hideSettingVariableList}",
//                "onShortcut: ${onShortcut}",
//                "isToolbarBtnCustomInSettingSelects: ${editFragment.isToolbarBtnCustomInSettingSelects}",
//                "enableEditExecute: ${editFragment.enableEditExecute}",
//                "editFragment.enableCmdEdit: ${editFragment.enableCmdEdit}",
//                "isSettingEditForOnShortcut: ${isSettingEditForOnShortcut}",
//                "isOnlyCmdEditForShortcut: ${isOnlyCmdEditForShortcut}",
//                "toolBarButtonVisibleMap: ${editFragment.toolBarButtonVisibleMap}",
//                "toolBarButtonIconMap: ${editFragment.toolBarButtonIconMap}",
//                "toolbarButtonConfigMap: ${editFragment.toolbarButtonConfigMap}",
//            ).joinToString("\n\n")
//        )

//        val bottomScriptUrlList = makeListFromSettingPath(
//            editFragment,
//            settingVariableList,
//            CommandClickScriptVariable.HOME_SCRIPT_URLS_PATH,
//            UsePath.homeScriptUrlsFilePath,
//        )
//        if(
//            bottomScriptUrlList.isNotEmpty()
//        ) editFragment.bottomScriptUrlList = bottomScriptUrlList

//
//        val homeFannelHistoryNameList = makeListFromSettingPath(
//            editFragment,
//            settingVariableList,
//            CommandClickScriptVariable.CMDCLICK_HOME_FANNELS_PATH,
//            UsePath.homeFannelsFilePath,
//        )
//        if(
//            homeFannelHistoryNameList.isNotEmpty()
//        ) editFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

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

//        editFragment.onNoUrlSaveMenu = SettingVariableReader.getStrValue(
//            settingVariableList,
//            CommandClickScriptVariable.NO_SCROLL_SAVE_URLS,
//            String()
//        ).isNotEmpty()

        editFragment.onUpdateLastModify = !(
                SettingVariableReader.getStrValue(
                    settingVariableList,
                    CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY,
                    String()
                ) == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
                )
    }

//    private fun judgeExistListIndex(
//        editFragment: EditFragment,
//    ): Boolean {
//        val isSetting = !IsCmdEdit.judge(editFragment)
//
//        if(
//            isSetting
//        ) return false
////        FileSystems.writeFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "setVal.txt").absolutePath,
////           listOf(
////               "recordNumToMapNameValueInSettingHolder: ${editFragment.recordNumToMapNameValueInSettingHolder}",
////               "setVariableTypeList: ${editFragment.setVariableTypeList?.joinToString("\n")}"
////           ).joinToString("\n\n")
////        )
//
////        val setVariableTypeList =
////            editFragment.setVariableTypeList
////                ?: return false
////        val setVariableTypeListCon =
////            "\n" + setVariableTypeList.joinToString("\n")
//        val listIndexNewlineRegex = Regex(
//            "\n[a-zA-Z0-9]{1,}:${EditTextSupportViewName.LIST_INDEX.str}="
//        )
//        return listIndexNewlineRegex.containsMatchIn(setVariableTypeListCon)
//    }

    private fun setButtonVisible(
        editFragment: EditFragment,
        enableEditExecute: Boolean,
//        enableCmdEdit: Boolean,
//        isSettingEdit: Boolean,
        isOnlyCmdEdit: Boolean,
    ){
//        editFragment.enableEditExecute =
//            (editFragment.editExecuteValue ==
//                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
//                    ) && onShortcut

//        val enableCmdEdit = editFragment.enableCmdEdit

//        val isSettingEdit = !enableCmdEdit
//                || editFragment.passCmdVariableEdit ==
//                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
//        val isOnlyCmdEdit = enableCmdEdit
//                && !enableEditExecute
//        val isCmdEditExecute = enableEditExecute
//        enableCmdEdit
//                && enableEditExecute
        editFragment.toolBarButtonVisibleMap.put(
            ToolbarButtonBariantForEdit.HISTORY,
            enableEditExecute
        )
        val buttonVisibleOn =
            ButtonVisibleSettingForToolbarButton.ButtonVisibleValue.ON.name
        val settingButtonVariant = ToolbarButtonBariantForEdit.SETTING
        editFragment.toolBarButtonVisibleMap.put(
            settingButtonVariant,
            when(true) {
//                isSettingEdit -> false
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
//                isSettingEdit -> false
                isOnlyCmdEdit -> false
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
                        ) return@let false
                        it == buttonVisibleOn
                    }
                }
            }
        )
        val okButtonVariant = ToolbarButtonBariantForEdit.OK
        editFragment.toolBarButtonVisibleMap.put(
            okButtonVariant,
            when(true) {
//                isSettingEdit -> true
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
//                isSettingEdit -> false
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
//        isSettingEdit: Boolean,
    ){
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
                        editFragment.context,
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
                        editFragment.context,
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
                        editFragment.context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                },
                ToolbarButtonBariantForEdit.OK to makePlayButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    onShortcut,
//                    isSettingEdit,
                ).let {
                    AlterToolForSetValType.updateConfigMapByAlter(
                        editFragment.context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                }
            )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "toolbarInInitMap.txt").absolutePath,
//            listOf(
//                "settingVariableList ${settingVariableList}",
//                "${CommandClickScriptVariable.SETTING_BUTTON_CONFIG}: ${execMakeToolbarButtonConfigMap(
//                    editFragment,
//                    settingVariableList,
//                    CommandClickScriptVariable.SETTING_BUTTON_CONFIG,
//                    String(),
//                    onShortcut,
//                )}",
//                "toolbarButtonConfigMap: ${editFragment.toolbarButtonConfigMap}",
//            ).joinToString("\n")
//        )
    }

    private fun setEditToolBarButtonIcon(
        editFragment: EditFragment,
//        isSettingEdit: Boolean,
        isOnlyCmdEdit: Boolean,
    ){
//        editFragment.enableEditExecute =
//            (editFragment.editExecuteValue ==
//                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
//                    ) && onShortcut

//        val enableCmdEdit = editFragment.enableCmdEdit

//        val isSettingEdit = !enableCmdEdit
//                || editFragment.passCmdVariableEdit ==
//                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
//        val isOnlyCmdEdit = enableCmdEdit
//                && !enableEditExecute
        val iconNameIdPairList = CmdClickIcons.values()
        val defaultIconId = R.drawable.icons8_setting
        val defaultSettingButtonIconIdToCaption =
            Pair(defaultIconId, "setting")
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.SETTING,
            when(true) {
//                isSettingEdit,
                isOnlyCmdEdit ->
                    defaultSettingButtonIconIdToCaption
                else -> {
                    val settingButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.SETTING)
                    val selectedIconName = settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.SETTING.str
                    val selectedCaption = settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.CAPTTION.key
                    ) ?: String()
                    val iconId = iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultIconId
                    Pair(iconId, selectedCaption)
                }
            }
        )
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.EXTRA,
            let {
                val settingButtonConfigMap =
                    editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.EXTRA)
                val selectedCaption = settingButtonConfigMap?.get(
                    SettingButtonConfigMapKey.CAPTTION.key
                ) ?: String()
                val selectedIconName = settingButtonConfigMap?.get(
                    SettingButtonConfigMapKey.ICON.key
                ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.EXTRA.str
                val iconId = iconNameIdPairList.find {
                    it.str == selectedIconName
                }?.id ?: defaultIconId
                Pair(iconId, selectedCaption)
            }
        )
        val defaultOkButtonIconIdToCaption =
            Pair(R.drawable.icons8_check_ok, "ok")
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.OK,
            when(true) {
//                isSettingEdit ->
//                    defaultOkButtonIconIdToCaption
                isOnlyCmdEdit ->
                    defaultOkButtonIconIdToCaption
                else -> {
                    val okButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.OK)
                    val selectedCaption = okButtonConfigMap?.get(
                        SettingButtonConfigMapKey.CAPTTION.key
                    ) ?: String()
                    val selectedIconName = okButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.PLAY.str
                    val defaultOkIconId = R.drawable.icons_play
                    val iconId = iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultOkIconId
                    Pair(iconId, selectedCaption)
                }
            }
        )
        val defaultEditButtonIconId =
            R.drawable.icons8_edit
        val defaultEditButtonIconIdToCaption =
            Pair(defaultEditButtonIconId, "edit")
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.EDIT,
            when(true) {
//                isSettingEdit ->
//                    defaultEditButtonIconIdToCaption
                isOnlyCmdEdit ->
                    defaultEditButtonIconIdToCaption
                else -> {
                    val editButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.EDIT)
                    val selectedCaption = editButtonConfigMap?.get(
                        SettingButtonConfigMapKey.CAPTTION.key
                    ) ?: String()
                    val selectedIconName = editButtonConfigMap?.get(
                        SettingButtonConfigMapKey.ICON.key
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.EDIT.str
                    val defaultEditIconId = R.drawable.icons8_check_ok
                    val iconId = iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultEditIconId
                    Pair(iconId, selectedCaption)

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
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            defaultButtonConfigCon,
        )
    }

    private fun makePlayButtonConfigMap(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        onShortcut: Boolean,
//        isSettingEdit: Boolean,
    ): Map<String, String> {
        if(!onShortcut){
            return mapOf()
        }
//        val isSettingEdit = !editFragment.enableCmdEdit
//                || editFragment.passCmdVariableEdit ==
//                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        return ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.PLAY_BUTTON_CONFIG,
            settingVariableList,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            String(),
        )
//        return when(isSettingEdit) {
//            true -> mapOf(
//                SettingButtonConfigMapKey.CLICK.key to
//                        listOf(
//                            JsActionKeyManager.JsActionsKey.JS_FUNC.key,
//                            MacroForToolbarButton.Macro.OK.name
//                        ).joinToString("=")
//            )
//            else -> {
//                ListSettingVariableListMaker.makeConfigMapFromSettingValList(
//                    CommandClickScriptVariable.PLAY_BUTTON_CONFIG,
//                    settingVariableList,
//                    editFragment.fannelInfoMap,
//                    editFragment.setReplaceVariableMap,
//                    String(),
//                )
//            }
//        }
    }

//    private fun makeListFromSettingPath(
//        editFragment: EditFragment,
//        settingVariableList: List<String>?,
//        settingValName: String,
//        defaultPath: String,
//    ): List<String> {
//        val fannelInfoMap = editFragment.fannelInfoMap
////        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////            fannelInfoMap
////        )
//        val currentFannelName = FannelInfoTool.getCurrentFannelName(
//            fannelInfoMap
//        )
//        val bottomScriptUrlPath =  SettingVariableReader.getStrValue(
//            settingVariableList,
//            settingValName,
//            defaultPath,
//        ).let {
//            val repPath = when(it.isEmpty()){
//                true -> defaultPath
//                else -> it
//            }
//            SetReplaceVariabler.execReplaceByReplaceVariables(
//                repPath,
//                editFragment.setReplaceVariableMap,
////                currentAppDirPath,
//                currentFannelName,
//            )
//        }
//        return SettingVariableReader.setListFromPath(
//            bottomScriptUrlPath
//        )
//    }
}

private object AlterToolForSetValType {

    private val alterKeyName = JsAcAlterIfTool.alterKeyName
    private const val mainSeparator = '|'
    private const val ifArgsSeparator = '?'

    fun updateConfigMapByAlter(
        context: Context?,
        configMap: Map<String, String>,
        busyboxExecutor: BusyboxExecutor?,
        replaceVariableMap: Map<String, String>?
    ): Map<String, String> {
        if(
            busyboxExecutor == null
        ) return configMap
        val alterKeyEqualStr = "${alterKeyName}="
        return configMap.map {
            val currentConfigKey = it.key
            val currentConfigValue = it.value
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
            val alterKeyValuePairList = makeAlterMap(
                alterValue,
                replaceVariableMap
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setVal_configKeyPairList.txt").absolutePath,
//                listOf(
//                    "currentConfigKey: ${currentConfigKey}",
//                    "currentConfigValue: ${currentConfigValue}",
//                    "currentConfigValueList: ${currentConfigValueList}",
//                    "alterTypeValue: ${alterTypeValue}",
//                    "alterValue: ${alterValue}",
//                    "alterValue: ${alterValue}",
//                    "alterKeyValuePairList: $alterKeyValuePairList",
//                ).joinToString("\n\n\n")
//            )
            val shellIfOutput = JsAcAlterIfTool.getIfOutput(
                context,
                busyboxExecutor,
                alterKeyValuePairList,
                replaceVariableMap,
                ifArgsSeparator
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
            val updateConfigValue = JsAcAlterIfTool.execAlter(
                currentConfigValueList,
                alterKeyValuePairList,
                shellIfOutput,
                mainSeparator
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_configValueByalter.txt").absolutePath,
//                listOf(
//                    "alterTypeValue: ${alterTypeValue}",
//                    "alterValue: ${alterValue}",
//                    "currentConfigKey: ${currentConfigKey}",
//                    "updateConfigValue: ${updateConfigValue}",
//                ).joinToString("\n\n-------\n")
//            )
            currentConfigKey to updateConfigValue
        }.toMap()
    }

    private fun makeAlterMap(
        alterValue: String,
        replaceVariableMap: Map<String, String>?,
    ): List<Pair<String, String>> {
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            alterValue,
            replaceVariableMap,
//            String(),
            String()
        ).let {
            CmdClickMap.createMap(
                it,
                mainSeparator,
            )
        }
    }
}
