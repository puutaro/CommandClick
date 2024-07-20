package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File


class ListIndexArgsMaker(
    val editFragment: EditFragment,
    val clickConfigPairList: List<Pair<String, String>>?,
) {
    val setReplaceVariableMap = editFragment.setReplaceVariableMap
    val fannelInfoMap = editFragment.fannelInfoMap
    val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )

    companion object {
        fun makeListIndexClickMenuPairList(
            fragment: Fragment,
            jsActionMap: Map<String, String>
        ): List<List<Pair<String, String>>> {
            val fannelInfoMap = FannelInfoTool.getFannelInfoMap(
                fragment,
                null
            )
            val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
                fannelInfoMap
            )
            val currentFannelName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap,
            )
            val setReplaceVariableMap = FannelInfoTool.getReplaceVariableMap(
                fragment,
                null
            )
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
            val busyboxExecutor = when(fragment){
                is EditFragment -> fragment.busyboxExecutor
                is TerminalFragment -> fragment.busyboxExecutor
                else -> {
                    fragment.context?.let {
                        BusyboxExecutor(
                            it,
                            UbuntuFiles(it)
                        )
                    }
                }
            }
            return MenuSettingTool.makeMenuPairListForMenuList(
                fragment.context,
                busyboxExecutor,
                settingMenuMapCon,
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap
            ).filter { it.isNotEmpty() }
        }
    }
}