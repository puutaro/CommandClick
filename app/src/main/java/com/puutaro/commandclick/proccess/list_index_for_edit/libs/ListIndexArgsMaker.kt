package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ClickSettingsForListIndex
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File


class ListIndexArgsMaker(
    val editFragment: EditFragment,
    private val isLongClick: Boolean,
    val clickConfigMap: Map<String, String>,
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

    val listIndexClickMenuMapList = makeListIndexClickMenuMapList()

    private fun makeListIndexClickMenuMapList(
    ): List<Map<String, String>?> {
        val settingMenuSettingFilePath =
            clickConfigMap.get(ClickSettingsForListIndex.ClickSettingKey.MENU_PATH.key)
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
            true -> SettingFile.read(
                    settingMenuSettingFilePathObj.absolutePath
                )
            else -> String()
        }
        return MenuSettingTool.makeMenuMapForMenuList(
            settingMenuMapCon,
            currentAppDirPath,
            currentFannelName,
            setReplaceVariableMap
        )
    }

    fun extractJsPathMacroFromSettingMenu(
        menuName: String,
    ): String {
        val nameKey = MenuSettingTool.MenuSettingKey.NAME.key
        val jsPathKey = MenuSettingTool.MenuSettingKey.JS_PATH.key
        return listIndexClickMenuMapList.firstOrNull {
            it?.get(nameKey) == menuName
        }?.get(jsPathKey)
            ?: String()
    }
}