package com.puutaro.commandclick.proccess.js_macro_libs.menu_tool

import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap

object MenuSettingTool {
    enum class MenuSettingKey(
        val key: String,
    ) {
        NAME("name"),
        ICON("icon"),
        PARENT_NAME("parentName"),
    }

    fun makeMenuPairListForMenuList(
        settingMenuMapCon: String,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): List<List<Pair<String, String>>> {
        val menuSeparator = ','
        val keySeparator = '|'
        return SetReplaceVariabler.execReplaceByReplaceVariables(
                settingMenuMapCon,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            ).let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    menuSeparator
                )
        }.map {
            if(
                it.isEmpty()
            ) return@map emptyList()
            CmdClickMap.createMap(
                it,
                keySeparator
            )
        }.filter {
            it.isNotEmpty()
        }
    }

    fun firstOrNullByParentMenuName(
        settingButtonMenuPairListList: List<List<Pair<String, String>>?>,
        menuName: String,
    ): List<Pair<String, String>>? {
        val parentMenuNameKey = MenuSettingKey.PARENT_NAME.key
        return settingButtonMenuPairListList.firstOrNull {
            it?.toMap()?.get(parentMenuNameKey)?.trim() == menuName
        }
    }

    fun extractJsKeyToSubConByMenuNameFromMenuPairListList(
        settingButtonMenuPairListList: List<List<Pair<String, String>>>,
        menuName: String,
    ): String? {
        val nameKey = MenuSettingKey.NAME.key
        return settingButtonMenuPairListList.firstOrNull {
            it.toMap().get(nameKey)?.trim() == menuName
        }?.let {
            convertMenuPairListToJsKeyToSubCon(
                it
            )
        }
    }

    fun convertMenuPairListToJsKeyToSubCon(
        menuPairList: List<Pair<String, String>>?
    ): String? {
        return menuPairList?.map {
            "${it.first.trim()}=${it.second.trim()}"
        }?.joinToString("|")
    }

    fun createListMenuListMap(
        settingButtonMenuPairList: List<List<Pair<String, String>>?>
    ): List<Pair<String, Int>> {
        val parentMenuKey = MenuSettingKey.PARENT_NAME.key
        return settingButtonMenuPairList.filter {
            it?.toMap()?.get(parentMenuKey).isNullOrEmpty()
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }

    fun createSubMenuListMap(
        menuPairListList: List<List<Pair<String, String>>?>,
        parentMenuName: String,
    ): List<Pair<String, Int>>{
        val parentMenuKey = MenuSettingKey.PARENT_NAME.key
        return menuPairListList.filter {
            it?.toMap()?.get(parentMenuKey) == parentMenuName
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }

    private fun execCreateMenuListMap(
        srcMenuPairListList: List<List<Pair<String, String>>?>
    ): List<Pair<String, Int>>{
        val menuNameKey = MenuSettingKey.NAME.key
        val iconKey = MenuSettingKey.ICON.key
        val ringIconId = CmdClickIcons.RING.id
        return srcMenuPairListList.map {
            val currentMenuMap = it?.toMap()
            val iconMacroName = currentMenuMap?.get(iconKey)
            val menuName = currentMenuMap?.get(menuNameKey)
                ?: String()
            val iconId = CmdClickIcons.values().filter {
                it.str == iconMacroName
            }.firstOrNull()?.id ?: ringIconId
            menuName to iconId
        }.filter { it.first.isNotEmpty() }
    }

}