package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExecCopyFile {
    fun copyFile(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
        filterMap: Map<String, String>,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        val tag = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.TAG.key,
        ) ?: String()
        val pickerMacroStr = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.MACRO.key,
        )
        val pickerMacro = FilePickerTool.PickerMacro.values().firstOrNull {
            it.name == pickerMacroStr
        }
        val fannelName = FannelInfoTool.getCurrentFannelName(
            editFragment.fannelInfoMap
        )
        val initialPath = FilePickerTool.makeInitialDirPath(
            filterMap,
            fannelName,
            pickerMacro,
            tag,
        )
        CoroutineScope(Dispatchers.Main).launch {
            editFragment.directoryAndCopyGetter?.get(
                selectedItem,
                listIndexPosition,
                initialPath,
                pickerMacro,
                fannelName,
                tag,
            )
        }
    }
}