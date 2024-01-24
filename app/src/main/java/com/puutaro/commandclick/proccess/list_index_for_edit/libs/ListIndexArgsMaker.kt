package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.DirectoryAndCopyGetter
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File


class ListIndexArgsMaker(
    val editFragment: EditFragment,
    private val isLongClick: Boolean,
    val clickConfigMap: Map<String, String>,
) {
    val setReplaceVariableMap = editFragment.setReplaceVariableMap
    val readSharePreffernceMap = editFragment.readSharePreffernceMap
    val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    val listIndexClickMenuMapList = makeListIndexClickMenuMapList()

    companion object {
        val throughMark = "-"
        val blankListMark = "Let's press sync button at right bellow"

        fun judgeNoFile(
            selectedItem: String,
        ): Boolean {
            return selectedItem == throughMark
                    || selectedItem.trim() == blankListMark
        }


        fun noFileToast(
            context: Context?,
            message: String = "No file"
        ){
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun makeListIndexClickMenuMapList(
    ): List<Map<String, String>?> {
        val settingMenuSettingFilePath =
            clickConfigMap.get(ListIndexEditConfig.ListIndexClickConfigMapKey.MENU_PATH.str)
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
            else -> String()
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

    fun execCreateMenuListMap(
        srcMenuMapList: List<Map<String, String>?>
    ): List<Pair<String, Int>> {
        val menuNameKey = ListIndexEditConfig.ListIndexMenuMapKey.NAME.str
        val iconKey = ListIndexEditConfig.ListIndexMenuMapKey.ICON.str
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

    fun extractJsPathMacroFromSettingMenu(
        menuName: String,
    ): String {
        val nameKey = ListIndexEditConfig.ListIndexMenuMapKey.NAME.str
        return listIndexClickMenuMapList.firstOrNull {
            it?.get(nameKey) == menuName
        }?.get(ListIndexEditConfig.ListIndexMenuMapKey.JS_PATH.str)
            ?: String()
    }
}