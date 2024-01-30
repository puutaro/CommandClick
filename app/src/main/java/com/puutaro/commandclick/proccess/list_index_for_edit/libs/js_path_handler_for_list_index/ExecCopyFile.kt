package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker

object ExecCopyFile {
    fun copyFile(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        listIndexArgsMaker.editFragment.directoryAndCopyGetter?.get(
            listIndexArgsMaker,
            listIndexListViewHolder,
            extraMapForJsPath
        )
    }
}