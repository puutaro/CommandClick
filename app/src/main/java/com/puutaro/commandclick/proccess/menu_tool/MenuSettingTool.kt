package com.puutaro.commandclick.proccess.menu_tool

import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.map.CmdClickMap

object MenuSettingTool {
    enum class MenuSettingKey(
        val key: String,
    ) {
        NAME("name"),
        ICON("icon"),
        JS_PATH("jsPath"),
        PARENT_NAME("parentName"),
        EXTRA(ExtraArgsTool.extraSettingKeyName),
    }

    fun makeMenuMapForMenuList(
        settingMenuMapCon: String,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): List<Map<String, String>?> {
        val menuSeparator = ","
        val keySeparator = "|"
        return settingMenuMapCon.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentFannelName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
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

    fun createListMenuListMap(
        settingButtonMenuMapList: List<Map<String, String>?>
    ): List<Pair<String, Int>> {
        val parentMenuKey = MenuSettingKey.PARENT_NAME.key
        return settingButtonMenuMapList.filter {
            it?.get(parentMenuKey).isNullOrEmpty()
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }

    fun createSubMenuListMap(
        menuMapList: List<Map<String, String>?>,
        parentMenuName: String,
    ): List<Pair<String, Int>>{
        val parentMenuKey = MenuSettingKey.PARENT_NAME.key
        return menuMapList.filter {
            it?.get(parentMenuKey) == parentMenuName
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }

    private fun execCreateMenuListMap(
        srcMenuMapList: List<Map<String, String>?>
    ): List<Pair<String, Int>>{
        val menuNameKey = MenuSettingKey.NAME.key
        val iconKey = MenuSettingKey.ICON.key
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

}