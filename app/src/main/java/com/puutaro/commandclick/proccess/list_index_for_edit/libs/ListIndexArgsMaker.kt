package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File


class ListIndexArgsMaker(
    val editFragment: EditFragment,
    private val isLongClick: Boolean,
    val clickConfigPairList: List<Pair<String, String>>?,
) {
    val setReplaceVariableMap = editFragment.setReplaceVariableMap
    val readSharePreffernceMap = editFragment.readSharePreferenceMap
    val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    companion object {
        fun makeListIndexClickMenuPairList(
            editFragment: EditFragment,
            jsActionMap: Map<String, String>
        ): List<List<Pair<String, String>>> {
            val readSharePreferenceMap = editFragment.readSharePreferenceMap
            val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreferenceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreferenceMap,
                SharePrefferenceSetting.current_fannel_name
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
                when (
                    settingMenuSettingFilePath.isNotEmpty()
                ) {
                    true -> settingMenuSettingFilePathObj.isFile
                    else -> false
                }
            val settingMenuMapCon = when (isSettingMenuSettingFilePath) {
                true -> SettingFile.read(
                    settingMenuSettingFilePathObj.absolutePath,
                    File(currentAppDirPath, currentFannelName).absolutePath,
                    setReplaceVariableMap,
                )

                else -> String()
            }
            return MenuSettingTool.makeMenuPairListForMenuList(
                settingMenuMapCon,
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap
            ).filter { it.isNotEmpty() }
        }
    }
}