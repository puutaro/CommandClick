package com.puutaro.commandclick.component.adapter.lib.edit_list_adapter

import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter

object ExecSwitcherForListIndexAdapter {

    fun updateTsv(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editConstraintListAdapter: EditConstraintListAdapter,
        lineMapList: List<Map<String, String>>,
    ){
//        val editComponentListAdapter =
//            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val tsvPath = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            editConstraintListAdapter.indexListMap,
            ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
        )
        if(
            tsvPath.isNullOrEmpty()
        ) return
        val sortType = ListSettingsForEditList.getSortType(
            fannelInfoMap,
            setReplaceVariableMap,
            editConstraintListAdapter.indexListMap
        )
        val sortListIndexListForTsvSave =
            ListSettingsForEditList.EditListMaker.sortListForTsvSave(
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
            ListSettingsForEditList.MapListPathManager.mapListSeparator
        )
    }
}