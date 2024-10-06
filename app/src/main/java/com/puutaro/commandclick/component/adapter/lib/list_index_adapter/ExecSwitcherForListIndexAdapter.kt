package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool

object ExecSwitcherForListIndexAdapter {

    fun updateTsv(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editComponentListAdapter: EditComponentListAdapter,
        lineMapList: List<Map<String, String>>,
    ){
//        val editComponentListAdapter =
//            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val tsvPath = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            editComponentListAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
        )
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val sortType = ListSettingsForListIndex.getSortType(
            fannelInfoMap,
            setReplaceVariableMap,
            editComponentListAdapter.indexListMap
        )
        val sortListIndexListForTsvSave =
            ListSettingsForListIndex.ListIndexListMaker.sortListForTsvSave(
                sortType,
                lineMapList
            )
        val curTsvConList = ReadText(
            tsvPath
        ).textToList().filter {
            it.isNotEmpty()
        }
        if(
            curTsvConList.size != sortListIndexListForTsvSave.size
        ) return
        MapListFileTool.update(
            tsvPath,
            sortListIndexListForTsvSave,
            ListSettingsForListIndex.MapListPathManager.mapListSeparator
        )
    }
}