package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object ConfigFromScriptFileSetter {

    fun set(
        editFragment: EditFragment,
        currentScriptContentsList: List<String>
    ){
        val readSharePreferenceMap = editFragment.readSharePreffernceMap
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

        editFragment.existIndexList =
            judgeExistListIndex(
                editFragment,
                currentAppDirPath,
                currentScriptFileName,
                currentScriptContentsList
            )
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            currentScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )?.joinToString("\n")?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptFileName,
            )
        }?.split("\n")
        if (
            !onShortcut
        ) {
            setButtonVisible(
                editFragment,
                settingVariableList,
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

        editFragment.statusBarIconColorMode = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE,
            editFragment.statusBarIconColorMode,
            SettingVariableSelects.StatusBarIconColorModeSelects.INHERIT.name,
            editFragment.statusBarIconColorMode,
            listOf(
                SettingVariableSelects.StatusBarIconColorModeSelects.BLACK.name
            ),
        )

        editFragment.runShell = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_RUN_SHELL,
            editFragment.runShell
        )

        editFragment.shiban = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.CMDCLICK_SHIBAN,
            editFragment.shiban
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

        editFragment.execPlayBtnLongPress = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EXEC_PLAY_BTN_LONG_PRESS,
            String()
        )

        editFragment.overrideItemClickExec = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.OVERRIDE_ITEM_CLICK_EXEC,
            CommandClickScriptVariable.OVERRIDE_ITEM_CLICK_EXEC_DEFAULT_VALUE,
        )

        editFragment.execEditBtnLongPress = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EXEC_EDIT_BTN_LONG_PRESS,
            String()
        )

        editFragment.passCmdVariableEdit = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT,
            String(),
        )

        setButtonVisible(
            editFragment,
            settingVariableList,
            onShortcut
        )
        setEditToolBarButtonIcon(
            editFragment,
            settingVariableList,
            onShortcut
        )

        val bottomScriptUrlList = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.homeScriptUrlsFilePath,
                currentAppDirPath,
                currentScriptFileName,
            )
        )
        if(
            bottomScriptUrlList.isNotEmpty()
        ) editFragment.bottomScriptUrlList = bottomScriptUrlList


        val homeFannelHistoryNameList = SettingVariableReader.setListFromPath(
            ScriptPreWordReplacer.replace(
                UsePath.homeFannelsFilePath,
                currentAppDirPath,
                currentScriptFileName,
            )
        )
        if(
            homeFannelHistoryNameList.isNotEmpty()
        ) editFragment.homeFannelHistoryNameList = homeFannelHistoryNameList

        if(
            editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.SETTING_EDIT_PREFIX.str
            ) == true
        ) return
        editFragment.terminalOn = CommandClickVariables.substituteCmdClickVariable(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_DO
        ) ?: CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE

        editFragment.onNoUrlSaveMenu = !CommandClickVariables.substituteCmdClickVariable(
            settingVariableList,
            CommandClickScriptVariable.NO_SCROLL_SAVE_URLS
        ).isNullOrEmpty()
        editFragment.onUpdateLastModify = !(
                CommandClickVariables.substituteCmdClickVariable(
                    settingVariableList,
                    CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
                ) == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
                )
    }

    private fun judgeExistListIndex(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String,
        currentShellContentsList: List<String>
    ): Boolean {
        val isSetting =
            editFragment.editTypeSettingKey ==
                    EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT
        if(
            isSetting
        ) return false
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
                editFragment.settingSectionStart,
                editFragment.settingSectionEnd,
                true,
                currentScriptFileName
            )
        val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
            recordNumToMapNameValueInSettingHolder,
            currentAppDirPath,
            currentScriptFileName,
        )
        return setVariableTypeList?.any {
            it.contains(
                ":${EditTextSupportViewName.LIST_INDEX.str}="
            )
        } ?: false
    }

    private fun setButtonVisible(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        onShortcut: Boolean
    ){
        editFragment.editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
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
//        val isCmdEditExecute = enableCmdEdit
//                && editFragment.enableEditExecute
        editFragment.toolBarButtonDisableMap.put(
            ToolbarButtonBariantForEdit.HISTORY,
            isOnlyCmdEdit || isSettingEdit
        )
        editFragment.toolBarButtonDisableMap.put(
            ToolbarButtonBariantForEdit.SETTING,
            when(true) {
                isSettingEdit,
                isOnlyCmdEdit -> true
                else -> SettingVariableReader.getCbValue(
                    settingVariableList,
                    CommandClickScriptVariable.DISABLE_SETTING_BUTTON,
                    CommandClickScriptVariable.DISABLE_SETTING_BUTTON_DEFAULT_VALUE,
                    String(),
                    CommandClickScriptVariable.DISABLE_SETTING_BUTTON_DEFAULT_VALUE,
                    listOf(
                        SettingVariableSelects.disableSettingButtonSelects.ON.name
                    ),
                ) == SettingVariableSelects.disableSettingButtonSelects.ON.name
            }
        )
        editFragment.toolBarButtonDisableMap.put(
            ToolbarButtonBariantForEdit.EDIT,
            when (true) {
                isSettingEdit -> true
                isOnlyCmdEdit -> false
                else -> SettingVariableReader.getCbValue(
                    settingVariableList,
                    CommandClickScriptVariable.DISABLE_EDIT_BUTTON,
                    CommandClickScriptVariable.DISABLE_EDIT_BUTTON_DEFAULT_VALUE,
                    String(),
                    CommandClickScriptVariable.DISABLE_EDIT_BUTTON_DEFAULT_VALUE,
                    listOf(
                        SettingVariableSelects.disableEditButtonSelects.ON.name
                    ),
                ) == SettingVariableSelects.disableEditButtonSelects.ON.name
            }
        )

        editFragment.toolBarButtonDisableMap.put(
            ToolbarButtonBariantForEdit.OK,
            when(true) {
                isSettingEdit -> false
                isOnlyCmdEdit -> false
                else -> SettingVariableReader.getCbValue(
                    settingVariableList,
                    CommandClickScriptVariable.DISABLE_PLAY_BUTTON,
                    CommandClickScriptVariable.DISABLE_PLAY_BUTTON_DEFAULT_VALUE,
                    String(),
                    CommandClickScriptVariable.DISABLE_PLAY_BUTTON_DEFAULT_VALUE,
                    listOf(
                        SettingVariableSelects.disablePlayButtonSelects.ON.name
                    ),
                ) == SettingVariableSelects.disablePlayButtonSelects.ON.name
            }
        )
    }

    private fun setEditToolBarButtonIcon(
        editFragment: EditFragment,
        settingVariableList: List<String>?,
        onShortcut: Boolean
    ){
        editFragment.editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
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
                    val selectedIconName =
                        SettingVariableReader.getStrValue(
                            settingVariableList,
                            CommandClickScriptVariable.SETTING_BUTTON_ICON,
                            CmdClickIcons.SETTING.str
                        )
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultSettingButtonIconId
                }
            }
        )
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.OK,
            when(true) {
                isSettingEdit ->
                    R.drawable.icons8_check_ok
                isOnlyCmdEdit ->
                    R.drawable.icons8_check_ok
                else -> {
                    val selectedIconName =
                        SettingVariableReader.getStrValue(
                            settingVariableList,
                            CommandClickScriptVariable.PLAY_BUTTON_ICON,
                            CmdClickIcons.PLAY.str
                        )
                    val defaultIconId = R.drawable.icons_play
                    iconNameIdPairList.find {
                        it.str == selectedIconName
                    }?.id ?: defaultIconId
                }
            }
        )
    }
}