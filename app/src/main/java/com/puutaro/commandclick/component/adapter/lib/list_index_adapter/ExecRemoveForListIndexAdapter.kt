package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object ExecRemoveForListIndexAdapter {

    fun updateTsv(
        editFragment: EditFragment,
        removeItemLineList: List<String>,
    ){
        val tsvPath = FilePrefixGetter.get(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
        if(
            tsvPath.isNullOrEmpty()
        ) return
        TsvTool.updateTsvByRemove(
            tsvPath,
            removeItemLineList,
        )
    }

    fun removeCon(
        editFragment: EditFragment,
        listIndexType: TypeSettingsForListIndex.ListIndexTypeKey,
        removeItemLine: String,
    ){
        when(listIndexType) {
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> {}
        }
        val onDeleteConFile = DeleteSettingsForListIndex.howOnDeleteConFileValue(
            ListIndexForEditAdapter.deleteConfigMap
        )
        if(!onDeleteConFile) return
        val removeTitleConList = removeItemLine.split("\t")
        if(removeTitleConList.size != 2) return
        val filePath = removeTitleConList.last()
//        FileSystems.removeFiles(filePath)
        val filePathObj = File(filePath)
        val parentDirPath = filePathObj.parent
            ?: return
        val fileName = filePathObj.name
        ExecItemDelete.execDeleteAfterConfirm(
            editFragment,
            parentDirPath,
            fileName,
        )
    }
}