package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonIconSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonVisibleSettingForToolbarButton
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FDialogTempFile
import com.puutaro.commandclick.util.map.ConfigMapTool
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object ConfigFromScriptFileSetter {

    fun set(
        editFragment: EditFragment,
    ){
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptFileName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val settingVariableList = FannelStateRooterManager.makeSettingVariableList(
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
            editFragment.settingFannelPath
        )

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

        editFragment.existIndexList =
            judgeExistListIndex(
                editFragment,
                currentAppDirPath,
                currentScriptFileName,
            )

        val defaultEditBoxTitle = TitleImageAndViewSetter.makeTitle(
            editFragment,
            currentAppDirPath,
            currentScriptFileName
        )
        editFragment.editBoxTitle = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_BOX_TITLE,
            String(),
        ).let {
            if(
                it.isNotEmpty()
            ) return@let TitleImageAndViewSetter.makeTitleForEditTitle(
                    editFragment,
                    it
                )
            defaultEditBoxTitle
        }
        makeToolbarButtonConfigMap(
            editFragment,
            settingVariableList,
        )

        editFragment.editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        editFragment.listIndexConfigMap = makeSettingConfigMap(
            editFragment,
            settingVariableList,
            CommandClickScriptVariable.LIST_INDEX_CONFIG,
            String(),
        )
        editFragment.qrDialogConfig = makeSettingConfigMap(
            editFragment,
            settingVariableList,
            CommandClickScriptVariable.QR_DIALOG_CONFIG,
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
        currentAppDirPath: String,
        currentScriptFileName: String,
    ): Boolean {
        val isSetting =
            editFragment.editTypeSettingKey ==
                    EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT
        if(
            isSetting
        ) return false
//        val recordNumToMapNameValueInSettingHolder =
//            RecordNumToMapNameValueInHolder.parse(
//                editFragment.currentScriptContentsList,
//                editFragment.settingSectionStart,
//                editFragment.settingSectionEnd,
//                true,
//            )
        val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
            editFragment.recordNumToMapNameValueInSettingHolder,
            currentAppDirPath,
            currentScriptFileName,
            editFragment.setReplaceVariableMap
        )
        return setVariableTypeList?.any {
            it.contains(
                ":${EditTextSupportViewName.LIST_INDEX.str}="
            )
        } ?: false
    }

    private fun setButtonVisible(
        editFragment: EditFragment,
        onShortcut: Boolean
    ){
        val readSharePreffernceMap = editFragment.readSharePreferenceMap
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
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
        editFragment.toolBarButtonVisibleMap.put(
            ToolbarButtonBariantForEdit.SETTING,
            when(true) {
                isSettingEdit,
                isOnlyCmdEdit -> false
                else -> {
                    val settingButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.SETTING)
                    settingButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.str
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let true
                        it == buttonVisibleOn
                    }
                }
            }
        )
        editFragment.toolBarButtonVisibleMap.put(
            ToolbarButtonBariantForEdit.EDIT,
            when (true) {
                isSettingEdit -> false
                isOnlyCmdEdit ->
                    !FDialogTempFile.howFDialogFile(currentFannelName)
                else -> {
                    val editButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.EDIT)
                    editButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.str
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let true
                        it == buttonVisibleOn
                    }
                }
            }
        )

        editFragment.toolBarButtonVisibleMap.put(
            ToolbarButtonBariantForEdit.OK,
            when(true) {
                isSettingEdit -> true
                isOnlyCmdEdit -> true
                else -> {
                    val okButtonConfigMap =
                        editFragment.toolbarButtonConfigMap?.get(ToolbarButtonBariantForEdit.EDIT)
                    okButtonConfigMap?.get(
                        SettingButtonConfigMapKey.VISIBLE.str
                    ).let {
                        if(
                            it.isNullOrEmpty()
                        ) return@let true
                        it == buttonVisibleOn
                    }
                }
            }
        )
    }

    private fun makeToolbarButtonConfigMap(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
    ){
        editFragment.toolbarButtonConfigMap =
            mapOf(
                ToolbarButtonBariantForEdit.SETTING to makeSettingConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.SETTING_BUTTON_CONFIG,
                    String(),
                ),
                ToolbarButtonBariantForEdit.EDIT to makeSettingConfigMap(
                    editFragment,
                    settingVariableList,
                    CommandClickScriptVariable.EDIT_BUTTON_CONFIG,
                    String(),
                ),
                ToolbarButtonBariantForEdit.OK to makePlayButtonConfigMap(
                    editFragment,
                    settingVariableList,
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
                        SettingButtonConfigMapKey.ICON.str
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.SETTING.str
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultSettingButtonIconId
                }
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
                        SettingButtonConfigMapKey.ICON.str
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
                        SettingButtonConfigMapKey.ICON.str
                    ) ?: ButtonIconSettingsForToolbarButton.ButtonIcons.EDIT.str
                    val defaultIconId = R.drawable.icons8_check_ok
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultIconId
                }
            }
        )
    }

    private fun makePlayButtonConfigMap(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
    ): Map<String, String> {
        val isSettingEdit = !editFragment.enableCmdEdit
                || editFragment.passCmdVariableEdit ==
                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        return when(isSettingEdit) {
            true -> mapOf(
                SettingButtonConfigMapKey.CLICK.str to
                        listOf(
                            JsActionKeyManager.JsActionsKey.JS_PATH.key,
                            MacroForToolbarButton.Macro.OK.name
                        ).joinToString("=")
            )
            else -> makeSettingConfigMap(
                editFragment,
                settingVariableList,
                CommandClickScriptVariable.PLAY_BUTTON_CONFIG,
                String(),
            )
        }
    }

    private fun makeSettingConfigMap(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        targetSettingConfigValName: String,
        defaultButtonConfigCon: String,
    ): Map<String, String> {
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val settingButtonConfigMapStr =
            ListSettingVariableListMaker.makeFromSettingVariableList(
                targetSettingConfigValName,
                readSharePreferenceMap,
                editFragment.setReplaceVariableMap,
                settingVariableList
            ).joinToString(",")
                .let {
                    if(
                        it.isNotEmpty()
                    ) return@let it
                    defaultButtonConfigCon
                }
        return ConfigMapTool.createFromSettingVal(
            settingButtonConfigMapStr,
            String(),
            editFragment.readSharePreferenceMap,
            editFragment.setReplaceVariableMap
        )
    }

    private fun makeListFromSettingPath(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        settingValName: String,
        defaultPath: String,
    ): List<String> {
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
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