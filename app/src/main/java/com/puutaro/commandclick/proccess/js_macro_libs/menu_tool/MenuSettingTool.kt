package com.puutaro.commandclick.proccess.js_macro_libs.menu_tool

import android.content.Context
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.JsAcAlterIfTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap

object MenuSettingTool {
    enum class MenuSettingKey(
        val key: String,
    ) {
        NAME("name"),
        ICON("icon"),
        PARENT_NAME("parentName"),
        DISABLE("disable")
    }

    private fun howDisable(
        menuPairList: List<Pair<String, String>>
    ): Boolean {
        val disableOn = "ON"
        val disableKeyName = MenuSettingKey.DISABLE.key
        return menuPairList.any {
            val key = it.first
            if(
                key != disableKeyName
            ) return@any false
            val disableValue =
                QuoteTool.trimBothEdgeQuote(it.second)
            disableValue == disableOn
        }
    }

    fun makeMenuPairListForMenuList(
        context: Context?,
        busyboxExecutor: BusyboxExecutor?,
        settingMenuMapCon: String,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): List<List<Pair<String, String>>> {
        val menuSeparator = ','

        val menuPairConListSrc = SetReplaceVariabler.execReplaceByReplaceVariables(
                settingMenuMapCon,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            ).let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    menuSeparator
                )
        }
        return menuPairConListSrc.map {
            currentMenuPairConList ->
            val updateConfigPairList = AlterToolForMenu.makeUpdateConfigPairList(
                context,
                busyboxExecutor,
                currentMenuPairConList,
                setReplaceVariableMap,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_configValueByalter.txt").absolutePath,
//                listOf(
//                    "alterTypeValue: ${alterTypeValue}",
//                    "alterValue: ${alterValue}",
//                    "updateConfigValue: ${updateConfigValue}",
//                    "updateConfigPairList: ${updateConfigPairList}",
//                ).joinToString("\n\n-------\n")
//            )
            val onDisable= howDisable(updateConfigPairList)
            when(onDisable){
                true -> emptyList()
                else -> updateConfigPairList
            }
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

private object AlterToolForMenu{

    private const val keySeparator = '|'
    private const val alterKeyName = JsAcAlterIfTool.alterKeyName
    private const val ifArgsSeparator = '?'

    fun makeUpdateConfigPairList(
        context: Context?,
        busyboxExecutor: BusyboxExecutor?,
        currentMenuPairConList: String,
        setReplaceVariableMap: Map<String, String>?,
    ): List<Pair<String, String>> {
        val alterKeyEqualStr = "${alterKeyName}="
        if(
            currentMenuPairConList.isEmpty()
            || busyboxExecutor == null
        ) return CmdClickMap.createMap(
            currentMenuPairConList,
            keySeparator
        )
        val currentConfigValueList = QuoteTool.splitBySurroundedIgnore(
            currentMenuPairConList,
            keySeparator
        )
        val alterTypeValue = currentConfigValueList.firstOrNull {
            it.startsWith(alterKeyEqualStr)
        }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_alterTypeValue.txt").absolutePath,
//                listOf(
//                    "busyboxExecutor: ${busyboxExecutor}",
//                    "currentConfigValueList: ${currentConfigValueList}",
//                    "alterTypeValue: ${alterTypeValue}",
//                ).joinToString("\n\n-------\n")
//            )
        if(
            alterTypeValue.isNullOrEmpty()
        ) return CmdClickMap.createMap(
            currentMenuPairConList,
            keySeparator
        )
        val alterValue = QuoteTool.trimBothEdgeQuote(
            alterTypeValue.removePrefix(
                alterKeyEqualStr
            ).trim()
        )
        val alterKeyValuePairList = makeAlterMap(
            alterValue,
            setReplaceVariableMap,
            keySeparator
        )
        val shellIfOutput = JsAcAlterIfTool.getIfOutput(
            context,
            busyboxExecutor,
            alterKeyValuePairList,
            setReplaceVariableMap,
            ifArgsSeparator
        )
        val disableAlter = shellIfOutput.isEmpty()
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_disableAlter.txt").absolutePath,
//                listOf(
//                    "busyboxExecutor: ${busyboxExecutor}",
//                    "currentConfigValueList: ${currentConfigValueList}",
//                    "alterTypeValue: ${alterTypeValue}",
//                    "alterValue: ${alterValue}",
//                    "alterKeyValuePairList: ${alterKeyValuePairList}",
//                    "shellIfOutput: ${shellIfOutput}",
//                ).joinToString("\n\n-------\n")
//            )
        if(
            disableAlter
        ) return CmdClickMap.createMap(
            currentMenuPairConList,
            keySeparator
        )
        val updateConfigValue = JsAcAlterIfTool.execAlter(
            currentConfigValueList,
            alterKeyValuePairList,
            shellIfOutput,
            keySeparator
        )
        return CmdClickMap.createMap(
            updateConfigValue,
            keySeparator
        )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_configValueByalter.txt").absolutePath,
//                listOf(
//                    "alterTypeValue: ${alterTypeValue}",
//                    "alterValue: ${alterValue}",
//                    "updateConfigValue: ${updateConfigValue}",
//                    "updateConfigPairList: ${updateConfigPairList}",
//                ).joinToString("\n\n-------\n")
//            )
    }

    private fun makeAlterMap(
        alterValue: String,
        replaceVariableMap: Map<String, String>?,
        keySeparator: Char,
    ): List<Pair<String, String>> {
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            alterValue,
            replaceVariableMap,
            String(),
            String()
        ).let {
            CmdClickMap.createMap(
                it,
                keySeparator,
            )
        }
    }
}