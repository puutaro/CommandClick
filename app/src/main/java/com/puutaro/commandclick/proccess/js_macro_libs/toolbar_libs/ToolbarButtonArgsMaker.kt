package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ClickSettingsForToolbarButton
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File
import java.lang.ref.WeakReference

class ToolbarButtonArgsMaker(
//    val editFragment: WeakReference<EditFragment>,
    val toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
    private val isLongClick: Boolean,
) {
//    private val context = editFragment.context
////    private val languageType = LanguageTypeSelects.JAVA_SCRIPT
////    private val languageTypeToSectionHolderMap =
////        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
////            languageType
////        )
//    val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
//    val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
//
////    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
////        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
////    ) as String
////
////    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
////        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
////    ) as String
//
//    val fannelInfoMap = editFragment.fannelInfoMap
//
//    val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
////        FannelInfoTool.getCurrentAppDirPath(
////        fannelInfoMap
////    )
//    val currentScriptFileName = FannelInfoTool.getCurrentFannelName(
//        fannelInfoMap
//    )
//    val fannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)
//
//    private val currentScriptContentsList = ReadText(
//        File(
//            cmdclickDefaultAppDirPath,
//            currentScriptFileName
//        ).absolutePath
//    ).textToList()
//    val setReplaceVariableMap = let {
//        val settingVariableList = CommandClickVariables.extractValListFromHolder(
//            currentScriptContentsList,
//            settingSectionStart,
//            settingSectionEnd
//        )
//        SetReplaceVariabler.makeSetReplaceVariableMap(
//            context,
//            settingVariableList,
////            cmdclickDefaultAppDirPath,
//            currentScriptFileName,
//        )
//    }

//    val toolbarButtonConfigMap =
//        editFragment.toolbarButtonConfigMap?.get(toolbarButtonBariantForEdit)

    companion object {
        private val onScriptSaveOffInClick = ClickSettingsForToolbarButton.OnScriptSave.OFF.name
        val onSaveDefaultMapInClick = mapOf(
            ToolbarButtonBariantForEdit.SETTING to onScriptSaveOffInClick,
            ToolbarButtonBariantForEdit.EDIT to onScriptSaveOffInClick,
            ToolbarButtonBariantForEdit.OK to onScriptSaveOffInClick,
            ToolbarButtonBariantForEdit.EXTRA to onScriptSaveOffInClick
        )
        val onSaveDefaultMapInLongClick = mapOf(
            ToolbarButtonBariantForEdit.SETTING to onScriptSaveOffInClick,
            ToolbarButtonBariantForEdit.EDIT to onScriptSaveOffInClick,
            ToolbarButtonBariantForEdit.OK to onScriptSaveOffInClick,
            ToolbarButtonBariantForEdit.EXTRA to onScriptSaveOffInClick
        )

        val defaultClickMacroMap = mapOf(
            ToolbarButtonBariantForEdit.SETTING to MacroForToolbarButton.Macro.SIZING.name,
            ToolbarButtonBariantForEdit.EDIT to MacroForToolbarButton.Macro.EDIT.name,
            ToolbarButtonBariantForEdit.OK to MacroForToolbarButton.Macro.OK.name,
        )

        val defaultLongClickMacroMap = mapOf(
            ToolbarButtonBariantForEdit.SETTING to MacroForToolbarButton.Macro.MENU.name,
            ToolbarButtonBariantForEdit.EDIT to MacroForToolbarButton.Macro.NORMAL.name,
            ToolbarButtonBariantForEdit.OK to MacroForToolbarButton.Macro.NORMAL.name,
            ToolbarButtonBariantForEdit.EXTRA to MacroForToolbarButton.Macro.NORMAL.name,
        )

//        private val menuDefaultConForCmdIndex = makeSettingMenuDefaultConForCmdIndex()
        private val menuDefaultConForEdit = makeSettingMenuDefaultConForEdit()

        fun makeSettingButtonMenuPairList(
            editFragment: EditFragment,
            jsActionMap: Map<String, String>?
        ): List<List<Pair<String, String>>> {
            val fannelInfoMap = editFragment.fannelInfoMap
//            val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//                fannelInfoMap
//            )
            val currentFannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val setReplaceVariableMap = editFragment.setReplaceVariableMap
            val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
                jsActionMap
            ) ?: emptyMap()
            val settingMenuSettingFilePath = argsMap.get(
                MacroForToolbarButton.MenuMacroArgsKey.MENU_PATH.key
            ) ?: String()
            val settingMenuSettingFilePathObj =
                File(settingMenuSettingFilePath)
            val isSettingMenuSettingFilePath =
                when(
                    settingMenuSettingFilePath.isNotEmpty()
                ){
                    true -> settingMenuSettingFilePathObj.isFile
                    else -> false
                }
            val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
            val settingMenuMapCon = when(isSettingMenuSettingFilePath){
                true -> SettingFile.read(
                    editFragment.context,
                    File(cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
                    setReplaceVariableMap,
                    null,
                    null,
                    null,
                    settingMenuSettingFilePathObj.absolutePath,

                )
                else -> SettingFile.formSettingContents(
                    makeToolbarbuttonMenuConHandler().split("\n")
                )
            }
            return MenuSettingTool.makeMenuPairListForMenuList(
                editFragment.context,
                editFragment.busyboxExecutor,
                settingMenuMapCon,
//                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap
            )
        }

        private fun makeToolbarbuttonMenuConHandler(): String {
            return menuDefaultConForEdit
        }
    }

//    fun decideClickKey(): String {
//        return when(isLongClick){
//            true -> SettingButtonConfigMapKey.LONG_CLICK.key
//            else -> SettingButtonConfigMapKey.CLICK.key
//        }
//    }
}

private fun makeSettingMenuDefaultConForEdit(): String {
    val menuNameKey = MenuSettingTool.MenuSettingKey.NAME.key
    val iconKey = MenuSettingTool.MenuSettingKey.ICON.key
    val parentMenuKey = MenuSettingTool.MenuSettingKey.PARENT_NAME.key
    val jsPathKey = JsActionKeyManager.JsActionsKey.JS_FUNC.key
    return """
        ${menuNameKey}=kill
            |${iconKey}=cancel
            |${jsPathKey}=${MacroForToolbarButton.Macro.KILL.name},
        ${menuNameKey}=usage
            |${iconKey}=info
            |${jsPathKey}=${MacroForToolbarButton.Macro.USAGE.name},
        ${menuNameKey}=no scroll save url
            |${iconKey}=ok
            |${jsPathKey}=${MacroForToolbarButton.Macro.NO_SCROLL_SAVE_URL.name},
        ${menuNameKey}=scan QR
            |${iconKey}=qr
            |${jsPathKey}=${MacroForToolbarButton.Macro.QR_SCAN.name},
        ${menuNameKey}=manage
            |${iconKey}=setup,
                ${menuNameKey}=refresh monitor
                    |${iconKey}=reflesh
                    |${jsPathKey}=${MacroForToolbarButton.Macro.REFRESH_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=select monitor
                    |${iconKey}=file
                    |${jsPathKey}=${MacroForToolbarButton.Macro.SELECT_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=restart ubuntu
                    |${iconKey}=launch
                    |${jsPathKey}=${MacroForToolbarButton.Macro.RESTART_UBUNTU.name}
                    |${parentMenuKey}=manage,
        ${menuNameKey}=setting
            |${iconKey}=setting,
                ${menuNameKey}=create short cut
                    |${iconKey}=setting
                    |${jsPathKey}=${MacroForToolbarButton.Macro.SHORTCUT.name}
                    |${parentMenuKey}=setting,
    """.trimIndent()
}


//${menuNameKey}=config
//|${iconKey}=edit_frame
//|${jsPathKey}=${MacroForToolbarButton.Macro.CONFIG.name}
//|${parentMenuKey}=setting,
//${menuNameKey}=termux setup
//|${iconKey}=setup
//|${jsPathKey}=${MacroForToolbarButton.Macro.TERMUX_SETUP.name}
//|${parentMenuKey}=setting,