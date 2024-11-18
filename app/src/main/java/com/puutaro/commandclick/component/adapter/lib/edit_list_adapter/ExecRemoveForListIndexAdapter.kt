package com.puutaro.commandclick.component.adapter.lib.edit_list_adapter

import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.edit_list.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecRemoveForListIndexAdapter {

    fun updateTsv(
        editComponentListAdapter: EditComponentListAdapter,
        removeItemLineList: List<Map<String, String>>,
    ){
        val mapListPath = FilePrefixGetter.get(
            editComponentListAdapter.fannelInfoMap,
            editComponentListAdapter.setReplaceVariableMap,
            editComponentListAdapter.editListMap,
            ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
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
        editComponentListAdapter: EditComponentListAdapter,
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
            editComponentListAdapter.deleteConfigMap
        )
        if(
            !onDeleteConFile
        ) return
//        val removeTitleConList = removeItemLineMap.split("\t")
//        if(
//            removeTitleConList.size != 2
//        ) return
        val filePath = removeItemLineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
        ) ?: String()
//            removeTitleConList.last()
        val filePathObj = File(filePath)
        val fileName = filePathObj.name
        val parentDirPath = filePathObj.parent
            ?: return
        ExecItemDelete.DeleteAfterConfirm.execDeleteAfterConfirm(
            editComponentListAdapter,
            parentDirPath,
            fileName,
        )
    }
}