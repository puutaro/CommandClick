package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.RecordNumToMapNameValueInHolderMaker
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonIconSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonVisibleSettingForToolbarButton
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
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
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val onShortcut = SharePrefTool.getOnShortcut(
            readSharePreferenceMap
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val settingVariableList = FannelStateRooterManager.makeSettingVariableList(
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
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
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
            String(),
        )
        TitleImageAndViewSetter.set(
            editFragment
        )
        setToolbarButtonConfigMapFromSettingValList(
            editFragment,
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
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
            String(),
        )
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
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
            String(),
        )


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
        settingVariableList: List<String>?,
        onShortcut: Boolean,
    ){
        editFragment.toolbarButtonConfigMap =
            mapOf(
                ToolbarButtonBariantForEdit.SETTING to execMakeToolbarButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.SETTING_BUTTON_CONFIG,
                    String(),
                    onShortcut,
                ),
                ToolbarButtonBariantForEdit.EXTRA to execMakeToolbarButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.EXTRA_BUTTON_CONFIG,
                    String(),
                    onShortcut,
                ),
                ToolbarButtonBariantForEdit.EDIT to execMakeToolbarButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.EDIT_BUTTON_CONFIG,
                    String(),
                    onShortcut,
                ),
                ToolbarButtonBariantForEdit.OK to makePlayButtonConfigMap(
                    editFragment,
                    settingVariableList,
                    onShortcut,
                )
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