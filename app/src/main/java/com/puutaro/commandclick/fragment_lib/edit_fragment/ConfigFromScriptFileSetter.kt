package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.JsAcAlterIfTool
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonIconSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ButtonVisibleSettingForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool

object ConfigFromScriptFileSetter {

    fun set(
        editFragment: EditFragment,
        mainFannelConList: List<String>
    ){
        val context = editFragment.context
            ?: return
        val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
        val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
        val fannelInfoMap = editFragment.fannelInfoMap
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val onShortcut = FannelInfoTool.getOnShortcut(
            fannelInfoMap
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val settingVariableList = FannelStateRooterManager.makeSettingVariableList(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            settingSectionStart,
            settingSectionEnd,
            editFragment.settingFannelPath
        )
        editFragment.settingFannelConList = settingVariableList

        editFragment.editBoxTitleConfig = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            editFragment.busyboxExecutor,
            editFragment.settingActionAsyncCoroutine,
            editFragment.imageActionAsyncCoroutine,
            CommandClickScriptVariable.EDIT_BOX_TITLE_CONFIG,
            settingVariableList,

            String(),
        ).let {
            AlterToolForSetValType.updateConfigMapByAlter(
                editFragment.context,
                it,
                editFragment.busyboxExecutor,
                setReplaceVariableMap
            )
        }

        setToolbarButtonConfigMapFromSettingValList(
            editFragment,
            editFragment.busyboxExecutor,
            settingVariableList,
            onShortcut,
        )

        editFragment.editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        editFragment.editListConfigMap = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            editFragment.busyboxExecutor,
            editFragment.settingActionAsyncCoroutine,
            editFragment.imageActionAsyncCoroutine,
            CommandClickScriptVariable.EDIT_LIST_CONFIG,
            settingVariableList,
            String(),
        )
//            .let {
//            AlterToolForSetValType.updateConfigMapByAlter(
//                editFragment.context,
//                it,
//                editFragment.busyboxExecutor,
//                setReplaceVariableMap
//            )
//        }

        editFragment.enableEditExecute =
            (editFragment.editExecuteValue ==
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    ) && onShortcut
        val isOnlyCmdEdit = !editFragment.enableEditExecute

        if (
            !onShortcut
        ) {
            setButtonVisible(
                editFragment,
                editFragment.enableEditExecute,
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
            sequenceOf(
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name,
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
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

        editFragment.enableEditExecute =
            (editFragment.editExecuteValue ==
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    ) && onShortcut
        val isOnlyCmdEditForShortcut = !editFragment.enableEditExecute

        setButtonVisible(
            editFragment,
            editFragment.enableEditExecute,
            isOnlyCmdEditForShortcut,
        )
        setEditToolBarButtonIcon(
            editFragment,
            isOnlyCmdEditForShortcut,
        )

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

        editFragment.onUpdateLastModify = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY,
            String()
        ) != SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
    }

    private fun setButtonVisible(
        editFragment: EditFragment,
        enableEditExecute: Boolean,
        isOnlyCmdEdit: Boolean,
    ){
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
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        editFragment.toolbarButtonConfigMap =
            mapOf(
                ToolbarButtonBariantForEdit.SETTING to execMakeToolbarButtonConfigMap(
                    editFragment,
                    editFragment.settingActionAsyncCoroutine,
                    editFragment.imageActionAsyncCoroutine,
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
                    editFragment.settingActionAsyncCoroutine,
                    editFragment.imageActionAsyncCoroutine,
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
                    editFragment.settingActionAsyncCoroutine,
                    editFragment.imageActionAsyncCoroutine,
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
                ).let {
                    AlterToolForSetValType.updateConfigMapByAlter(
                        editFragment.context,
                        it,
                        busyboxExecutor,
                        setReplaceVariableMap
                    )
                }
            )
    }

    private fun setEditToolBarButtonIcon(
        editFragment: EditFragment,
        isOnlyCmdEdit: Boolean,
    ){
        val iconNameIdPairList = CmdClickIcons.entries.toTypedArray()
        val defaultIconId = R.drawable.icons8_setting
        val defaultSettingButtonIconIdToCaption =
            Pair(defaultIconId, "setting")
        editFragment.toolBarButtonIconMap.put(
            ToolbarButtonBariantForEdit.SETTING,
            when(true) {
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
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        settingVariableList: List<String>?,
        targetSettingConfigValName: String,
        defaultButtonConfigCon: String,
        onShortcut: Boolean,
    ): Map<String, String> {
        if(
            !onShortcut
        ) return mapOf()
        val context = editFragment.context
        return ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            context,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.busyboxExecutor,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            targetSettingConfigValName,
            settingVariableList,
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
        val context = editFragment.context
        return ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            context,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.busyboxExecutor,
            editFragment.settingActionAsyncCoroutine,
            editFragment.imageActionAsyncCoroutine,
            CommandClickScriptVariable.PLAY_BUTTON_CONFIG,
            settingVariableList,
            String(),
        )
    }
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
            String()
        ).let {
            CmdClickMap.createMap(
                it,
                mainSeparator,
            )
        }
    }
}
