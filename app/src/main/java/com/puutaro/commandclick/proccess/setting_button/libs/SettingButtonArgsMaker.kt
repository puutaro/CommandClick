package com.puutaro.commandclick.proccess.setting_button.libs

import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.setting_button.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.setting_button.SettingButtonClickConfigMapKey
import com.puutaro.commandclick.proccess.setting_button.SettingButtonConfigMapKey
import com.puutaro.commandclick.proccess.setting_button.SettingButtonMenuMapKey
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Map.CmdClickMap
import com.puutaro.commandclick.util.Map.ConfigMapTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

class SettingButtonArgsMaker(
    val fragment: Fragment,
    val readSharePreffernceMap: Map<String, String>,
    val settingButtonView: ImageButton?,
    val fileGetterForSettingButton: FileGetterForSettingButton,
    private val isLongClick: Boolean,
) {

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

    val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
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

    private val recordNumToMapNameValueInSettingHolder =
        RecordNumToMapNameValueInHolder.parse(
            currentScriptContentsList,
            settingSectionStart,
            settingSectionEnd,
            true,
            currentScriptFileName
        )
    val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder,
        currentAppDirPath,
        currentScriptFileName,
    )
    val settingButtonConfigMap = ConfigMapTool.create(
        UsePath.settingButtonConfigPath,
        makeSettingButtonConfigConForEdit(),
        readSharePreffernceMap,
        setReplaceVariableMap

    )


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
        val clickConfigMap =   settingButtonConfigMap?.get(clickKey).let {
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
        val settingMenuSettingFilePathObj = File(settingMenuSettingFilePath)
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
                makeSettingMenuConForCmdIndex()
            is EditFragment ->
                makeSettingMenuConForEdit()
            else -> String()
        }
    }

    private fun makeSettingMenuConForCmdIndex(): String {
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

    private fun makeSettingMenuConForEdit(): String {
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

    private fun makeSettingButtonConfigConForEdit(): String {
//    val clickMacroKey = SettingButtonMapKey.CLICK_MACRO.str
//    val longClickMacroKey = SettingButtonMapKey.LONG_CLICK_MACRO.str
        return String()
//    return """
//        ${clickMacroKey}=${JsPathMacro.MENU},
//        ${longClickMacroKey}=${JsPathMacro.SIZING}
//    """.trimIndent()
    }

}