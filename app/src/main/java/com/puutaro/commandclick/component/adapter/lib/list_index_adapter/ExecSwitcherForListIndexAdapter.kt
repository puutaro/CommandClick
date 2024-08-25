package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool

object ExecSwitcherForListIndexAdapter {

    fun updateTsv(
        editFragment: EditFragment,
        listIndexList: List<String>,
    ){
        val tsvPath = FilePrefixGetter.get(
            editFragment,
            ListIndexAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val sortType = ListSettingsForListIndex.getSortType(
            editFragment,
            ListIndexAdapter.indexListMap
        )
        val sortListIndexListForTsvSave =
            ListSettingsForListIndex.ListIndexListMaker.sortListForTsvSave(
                sortType,
                listIndexList
            )
        val curTsvConList = ReadText(
            tsvPath
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