package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object ExecSwitcherForListIndexAdapter {

    fun updateTsv(
        editFragment: EditFragment,
        listIndexList: List<String>,
    ){
        val tsvPath = ListSettingsForListIndex.getListSettingKeyHandler(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
        if(
            tsvPath.isEmpty()
        ) return
        val sortType = ListSettingsForListIndex.getSortType(ListIndexForEditAdapter.indexListMap)
        val sortListIndexListForTsvSave =
            ListSettingsForListIndex.ListIndexListMaker.sortListForTsvSave(
                sortType,
                listIndexList
            )
        val tsvPathObj = File(tsvPath)
        val tsvParentDirPath = tsvPathObj.parent
            ?: return
        val tsvName = tsvPathObj.name
        val curTsvConList = ReadText(
            tsvParentDirPath,
            tsvName
        ).textToList().filter {
            it.isNotEmpty()
        }
        if(
            curTsvConList.size != sortListIndexListForTsvSave.size
        ) return
        TsvTool.updateTsv(
            tsvPath,
            sortListIndexListForTsvSave,
        )
    }
}