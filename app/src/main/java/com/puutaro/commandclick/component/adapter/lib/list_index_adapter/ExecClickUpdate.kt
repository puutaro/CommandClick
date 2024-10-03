package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ClickSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.map.FilePrefixGetter

object ExecClickUpdate {

    fun update(
        editFragment: EditFragment,
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: EditComponentListAdapter.ListIndexListViewHolder,
    ){
        val clickConfigMap = listIndexArgsMaker.clickConfigPairList
        val enableClickUpdate =
            ClickSettingsForListIndex.howEnableClickUpdate(
                clickConfigMap
            )
        if(!enableClickUpdate) return
        updateForTsv(
            editFragment,
            listIndexListViewHolder
        )
//        when(ListIndexAdapter.listIndexTypeKey) {
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////            -> {}
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> updateForNormal(
//                editFragment,
//                listIndexListViewHolder.fileName,
//            )
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> updateForTsv(
//                editFragment,
//                listIndexListViewHolder
//            )
//        }

    }

    private fun updateForTsv(
        editFragment: EditFragment,
        listIndexListViewHolder: EditComponentListAdapter.ListIndexListViewHolder,
    ){
        val editComponentAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val sortType = ListSettingsForListIndex.getSortType(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editComponentAdapter.indexListMap
        )
        when(sortType){
            ListSettingsForListIndex.SortByKey.SORT_TYPE,
            ListSettingsForListIndex.SortByKey.REVERSE
            -> return
            ListSettingsForListIndex.SortByKey.LAST_UPDATE,
            -> {}
        }
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val editComponentListAdapter = editListRecyclerView.adapter as EditComponentListAdapter
        val LineMap =
            editComponentListAdapter.lineMapList.getOrNull(
                listIndexListViewHolder.bindingAdapterPosition
            ) ?: return
        val mapListPath = FilePrefixGetter.get(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editComponentAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
        )
        MapListFileTool.insertMapFileInFirst(
            mapListPath,
            LineMap
        )
        BroadcastSender.normalSend(
            editFragment.context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

//    private fun updateForNormal(
//        editFragment: EditFragment,
//        selectedItem: String,
//    ){
//        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexAdapter.indexListMap,
//            ListIndexAdapter.listIndexTypeKey
//        )
//        FileSystems.updateLastModified(
//            File(
//                filterDir,
//                selectedItem
//            ).absolutePath
//        )
//        BroadcastSender.normalSend(
//            editFragment.context,
//            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
//        )
//    }
}