package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecRemoveForListIndexAdapter {

    fun updateTsv(
        editFragment: EditFragment,
        removeItemLineList: List<Map<String, String>>,
    ){
        val editComponentListAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val mapListPath = FilePrefixGetter.get(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editComponentListAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
        )
        if(
            mapListPath.isNullOrEmpty()
        ) return
        MapListFileTool.updateMapListFileByRemove(
            mapListPath,
            removeItemLineList,
        )
    }

    fun removeCon(
        editFragment: EditFragment,
//        listIndexType: TypeSettingsForListIndex.ListIndexTypeKey,
        removeItemLineMap: Map<String, String>,
    ){
//        when(listIndexType) {
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> return
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> {}
//        }
        val onDeleteConFile = DeleteSettingsForListIndex.howOnDeleteConFileValue(
            EditComponentListAdapter.deleteConfigMap
        )
        if(
            !onDeleteConFile
        ) return
//        val removeTitleConList = removeItemLineMap.split("\t")
//        if(
//            removeTitleConList.size != 2
//        ) return
        val filePath = removeItemLineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
        ) ?: String()
//            removeTitleConList.last()
        val filePathObj = File(filePath)
        val fileName = filePathObj.name
        val parentDirPath = filePathObj.parent
            ?: return
        ExecItemDelete.DeleteAfterConfirm.execDeleteAfterConfirm(
            editFragment,
            parentDirPath,
            fileName,
        )
    }
}