package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExecCopyFile {
    fun copyFile(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
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
        CoroutineScope(Dispatchers.Main).launch {
            editFragment.directoryAndCopyGetter?.get(
                selectedItem,
                listIndexPosition,
            )
        }
    }
}