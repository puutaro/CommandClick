package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExecCopyFile {
    fun copyFile(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        listIndexPosition: Int,
        filterMap: Map<String, String>,
    ){
        if(
            fragment !is EditFragment
        ) return
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
//        when(type){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
////            -> return
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT,
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> {}
//        }
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
            fannelInfoMap
        )
        val initialPath = FilePickerTool.makeInitialDirPath(
            filterMap,
            fannelName,
            pickerMacro,
            tag,
        )
        CoroutineScope(Dispatchers.Main).launch {
            fragment.directoryAndCopyGetter?.get(
                listIndexPosition,
                initialPath,
                pickerMacro,
                fannelName,
                tag,
            )
        }
    }
}