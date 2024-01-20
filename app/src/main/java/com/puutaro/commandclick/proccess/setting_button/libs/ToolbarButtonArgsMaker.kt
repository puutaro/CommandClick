package com.puutaro.commandclick.proccess.setting_button.libs

import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.setting_button.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.setting_button.OnScriptSave
import com.puutaro.commandclick.proccess.setting_button.SettingButtonClickConfigMapKey
import com.puutaro.commandclick.proccess.setting_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.setting_button.SettingButtonMenuMapKey
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

class ToolbarButtonArgsMaker(
    val fragment: Fragment,
    val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
//    val recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
    val toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    val settingButtonView: ImageButton?,
    private val isLongClick: Boolean,
) {
    val fileGetterForSettingButton = when(fragment) {
        is CommandIndexFragment -> fragment.fileGetterForSettingButton
        is EditFragment -> fragment.fileGetterForSettingButton
        else -> null
    }
    private val menuNameKey = SettingButtonMenuMapKey.NAME.str
    private val jsPathKey = SettingButtonMenuMapKey.JS_PATH.str
    private val languageType = LanguageTypeSelects.JAVA_SCRIPT
    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    val readSharePreffernceMap = when(fragment){
        is CommandIndexFragment -> fragment.readSharePreffernceMap
        is EditFragment -> fragment.readSharePreffernceMap
        else -> mapOf()
    }

    val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    val currentScriptFileName = CcPathTool.getCurrentScriptFileName(
        readSharePreffernceMap
    )
    val fannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)

    private val currentScriptContentsList = ReadText(
        currentAppDirPath,
        currentScriptFileName
    ).textToList()

    val setReplaceVariableMap =
        RecordNumToMapNameValueInHolder.parse(
            currentScriptContentsList,
            settingSectionStart,
            settingSectionEnd,
            true,
        ).let {
            SetReplaceVariabler.makeSetReplaceVariableMap(
                it,
                currentAppDirPath,
                currentScriptFileName,
            )
        }

//     UsePath.settingButtonConfigPath,
    val settingButtonConfigMap = when(fragment){
        is EditFragment -> fragment.toolbarButtonConfigMap?.get(toolbarButtonBariantForEdit)
        else -> mapOf()
    }

    companion object {

        val onSaveDefaultMapInClick = mapOf(
            ToolbarButtonBariantForEdit.SETTING to OnScriptSave.OFF.name,
            ToolbarButtonBariantForEdit.EDIT to OnScriptSave.OFF.name,
            ToolbarButtonBariantForEdit.OK to OnScriptSave.OFF.name,
        )
        val onSaveDefaultMapInLongClick = mapOf(
            ToolbarButtonBariantForEdit.SETTING to OnScriptSave.OFF.name,
            ToolbarButtonBariantForEdit.EDIT to OnScriptSave.OFF.name,
            ToolbarButtonBariantForEdit.OK to OnScriptSave.OFF.name,
        )

        val defaultClickMacroMap = mapOf(
            ToolbarButtonBariantForEdit.SETTING to JsPathMacroForSettingButton.SIZING.name,
            ToolbarButtonBariantForEdit.EDIT to JsPathMacroForSettingButton.EDIT.name,
            ToolbarButtonBariantForEdit.OK to JsPathMacroForSettingButton.OK.name,
        )

        val defaultLongClickMacroMap = mapOf(
            ToolbarButtonBariantForEdit.SETTING to JsPathMacroForSettingButton.MENU.name,
            ToolbarButtonBariantForEdit.EDIT to JsPathMacroForSettingButton.NORMAL.name,
            ToolbarButtonBariantForEdit.OK to JsPathMacroForSettingButton.NORMAL.name,
        )
        val setingButtonDefaultConfigCon = makeButtonConfigConForEdit()

        private val menuDefaultConForCmdIndex = makeSettingMenuDefaultConForCmdIndex()
        private val menuDefaultConForEdit = makeSettingMenuDefaultConForEdit()
    }

    fun makeSettingButtonConfigMapList(
        jsPathMacroStr: String,
    ): List<Map<String, String>?> {
        return listOf(
            "${menuNameKey}=${jsPathMacroStr}",
            "${jsPathKey}=${jsPathMacroStr}",
        ).joinToString("|").let {
            makeSettingMenuMapList(
                it,
            )
        }
    }

    fun decideClickKey(): String {
        return when(isLongClick){
            true -> SettingButtonConfigMapKey.LONG_CLICK.str
            else -> SettingButtonConfigMapKey.CLICK.str
        }
    }

    fun execCreateMenuListMap(
        srcMenuMapList: List<Map<String, String>?>
    ): List<Pair<String, Int>>{
        val menuNameKey = SettingButtonMenuMapKey.NAME.str
        val iconKey = SettingButtonMenuMapKey.ICON.str
        val ringIconId = CmdClickIcons.RING.id
        return srcMenuMapList.map {
            val iconMacroName = it?.get(iconKey)
            val menuName = it?.get(menuNameKey) ?: String()
            val iconId = CmdClickIcons.values().filter {
                it.str == iconMacroName
            }.firstOrNull()?.id ?: ringIconId
            menuName to iconId
        }.filter { it.first.isNotEmpty() }
    }

    fun makeSettingButtonMenuMapList(
    ): List<Map<String, String>?> {
        val clickKey = decideClickKey()
        val clickConfigMap = settingButtonConfigMap?.get(clickKey).let {
            if(
                it.isNullOrEmpty()
            ) return@let mapOf()
            CmdClickMap.createMap(
                it,
                "|"
            ).toMap()
        }
        val settingMenuSettingFilePath =
            clickConfigMap.get(SettingButtonClickConfigMapKey.MENU_PATH.str)
                ?: String()
        val settingMenuSettingFilePathObj =
            File(settingMenuSettingFilePath)
        val isSettingMenuSettingFilePath =
            when(
                settingMenuSettingFilePath.isNotEmpty()
            ){
                true -> settingMenuSettingFilePathObj.isFile
                else -> false
            }
        val settingMenuMapCon = when(isSettingMenuSettingFilePath){
            true -> {
                val parentDirPath = settingMenuSettingFilePathObj.parent
                    ?: return emptyList()
                SettingFile.read(
                    parentDirPath,
                    settingMenuSettingFilePathObj.name
                )
            }
            else -> {
                SettingFile.formSettingContents(
                    makeSettingMenuConHandler(fragment).split("\n")
                )
            }
        }
        return makeSettingMenuMapList(
            settingMenuMapCon,
        )
    }

    private fun makeSettingMenuMapList(
        settingMenuMapCon: String,
    ): List<Map<String, String>?> {
        val menuSeparator = ","
        val keySeparator = "|"
        return settingMenuMapCon.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptFileName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentScriptFileName
            )
        }.split(menuSeparator).map {
            if(
                it.isEmpty()
            ) return@map mapOf()
            CmdClickMap.createMap(
                it,
                keySeparator
            ).toMap()
        }.filter {
            it.isNotEmpty()
        }
    }

    private fun makeSettingMenuConHandler(
        fragment: Fragment
    ): String {
        return when(fragment){
            is CommandIndexFragment ->
                menuDefaultConForCmdIndex
            is EditFragment ->
                menuDefaultConForEdit
            else -> String()
        }
    }
}

private fun makeButtonConfigConForEdit(): String {
//    val clickMacroKey = SettingButtonMapKey.CLICK_MACRO.str
//    val longClickMacroKey = SettingButtonMapKey.LONG_CLICK_MACRO.str
    return String()
//    return """
//        ${clickMacroKey}=${JsPathMacro.MENU},
//        ${longClickMacroKey}=${JsPathMacro.SIZING}
//    """.trimIndent()
}

private fun makeSettingMenuDefaultConForCmdIndex(): String {
    val menuNameKey = SettingButtonMenuMapKey.NAME.str
    val iconKey = SettingButtonMenuMapKey.ICON.str
    val jsPathKey = SettingButtonMenuMapKey.JS_PATH.str
    val parentMenuKey = SettingButtonMenuMapKey.PARENT_NAME.str
    return """
        ${menuNameKey}=usage
            |${iconKey}=info
            |${jsPathKey}=${JsPathMacroForSettingButton.USAGE.name},
        ${menuNameKey}=edit startup
            |${iconKey}=edit_frame
            |${jsPathKey}=${JsPathMacroForSettingButton.EDIT_STARTUP.name},
        ${menuNameKey}=no scroll save url
            |${iconKey}=ok
            |${jsPathKey}=${JsPathMacroForSettingButton.NO_SCROLL_SAVE_URL.name},
        ${menuNameKey}=install fannel
            |${iconKey}=puzzle
            |${jsPathKey}=${JsPathMacroForSettingButton.INSTALL_FANNEL.name},
        ${menuNameKey}=scan QR
            |${iconKey}=qr
            |${jsPathKey}=${JsPathMacroForSettingButton.QR_SCAN.name},
        ${menuNameKey}=manage
            |${iconKey}=setup,
                ${menuNameKey}=refresh monitor
                    |${iconKey}=reflesh
                    |${jsPathKey}=${JsPathMacroForSettingButton.REFRESH_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=select monitor
                    |${iconKey}=file
                    |${jsPathKey}=${JsPathMacroForSettingButton.SELECT_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=restart ubuntu
                    |${iconKey}=launch
                    |${jsPathKey}=${JsPathMacroForSettingButton.RESTART_UBUNTU.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=js import manager
                    |${iconKey}=folda
                    |${jsPathKey}=${JsPathMacroForSettingButton.JS_IMPORT.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=add
                    |${iconKey}=plus
                    |${jsPathKey}=${JsPathMacroForSettingButton.ADD.name}
                    |${parentMenuKey}=manage,
        ${menuNameKey}=setting
            |${iconKey}=setting,
                ${menuNameKey}=app dir manager
                    |${iconKey}=setting
                    |${jsPathKey}=${JsPathMacroForSettingButton.APP_DIR_MANAGER.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=create short cut
                    |${iconKey}=shortcut
                    |${jsPathKey}=${JsPathMacroForSettingButton.SHORTCUT.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=termux setup
                    |${iconKey}=setup
                    |${jsPathKey}=${JsPathMacroForSettingButton.TERMUX_SETUP.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=config
                    |${iconKey}=edit_frame
                    |${jsPathKey}=${JsPathMacroForSettingButton.CONFIG.name}
                    |${parentMenuKey}=setting,
    """.trimIndent()
}

private fun makeSettingMenuDefaultConForEdit(): String {
    val menuNameKey = SettingButtonMenuMapKey.NAME.str
    val iconKey = SettingButtonMenuMapKey.ICON.str
    val jsPathKey = SettingButtonMenuMapKey.JS_PATH.str
    val parentMenuKey = SettingButtonMenuMapKey.PARENT_NAME.str
    return """
        ${menuNameKey}=kill
            |${iconKey}=cancel
            |${jsPathKey}=${JsPathMacroForSettingButton.KILL.name},
        ${menuNameKey}=usage
            |${iconKey}=info
            |${jsPathKey}=${JsPathMacroForSettingButton.USAGE.name},
        ${menuNameKey}=no scroll save url
            |${iconKey}=ok
            |${jsPathKey}=${JsPathMacroForSettingButton.NO_SCROLL_SAVE_URL.name},
        ${menuNameKey}=scan QR
            |${iconKey}=qr
            |${jsPathKey}=${JsPathMacroForSettingButton.QR_SCAN.name},
        ${menuNameKey}=manage
            |${iconKey}=setup,
                ${menuNameKey}=refresh monitor
                    |${iconKey}=reflesh
                    |${jsPathKey}=${JsPathMacroForSettingButton.REFRESH_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=select monitor
                    |${iconKey}=file
                    |${jsPathKey}=${JsPathMacroForSettingButton.SELECT_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=restart ubuntu
                    |${iconKey}=launch
                    |${jsPathKey}=${JsPathMacroForSettingButton.RESTART_UBUNTU.name}
                    |${parentMenuKey}=manage,
        ${menuNameKey}=setting
            |${iconKey}=setting,
                ${menuNameKey}=create short cut
                    |${iconKey}=setting
                    |${jsPathKey}=${JsPathMacroForSettingButton.SHORTCUT.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=termux setup
                    |${iconKey}=setup
                    |${jsPathKey}=${JsPathMacroForSettingButton.TERMUX_SETUP.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=config
                    |${iconKey}=edit_frame
                    |${jsPathKey}=${JsPathMacroForSettingButton.CONFIG.name}
                    |${parentMenuKey}=setting,
    """.trimIndent()
}
