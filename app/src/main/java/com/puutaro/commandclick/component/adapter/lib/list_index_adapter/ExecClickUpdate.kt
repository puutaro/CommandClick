package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import androidx.fragment.app.Fragment
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
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editComponentListAdapter: EditComponentListAdapter,
        listIndexArgsMaker: ListIndexArgsMaker,
        bindingAdapterPosition: Int,
    ){
        val clickConfigMap = listIndexArgsMaker.clickConfigPairList
        val enableClickUpdate =
            ClickSettingsForListIndex.howEnableClickUpdate(
                clickConfigMap
            )
        if(!enableClickUpdate) return
        updateForTsv(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editComponentListAdapter,
            bindingAdapterPosition
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
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editComponentListAdapter: EditComponentListAdapter,
        bindingAdapterPosition: Int,
    ){
//        val editComponentAdapter =
//            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val sortType = ListSettingsForListIndex.getSortType(
            fannelInfoMap,
            setReplaceVariableMap,
            editComponentListAdapter.indexListMap
        )
        when(sortType){
            ListSettingsForListIndex.SortByKey.SORT_TYPE,
            ListSettingsForListIndex.SortByKey.REVERSE
            -> return
            ListSettingsForListIndex.SortByKey.LAST_UPDATE,
            -> {}
        }
//        val binding = editFragment.binding
//        val editListRecyclerView = binding.editListRecyclerView
//        val editComponentListAdapter = editListRecyclerView.adapter as EditComponentListAdapter
        val lineMap =
            editComponentListAdapter.lineMapList.getOrNull(
                bindingAdapterPosition
            ) ?: return
        val mapListPath = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            editComponentListAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
        )
        MapListFileTool.insertMapFileInFirst(
            mapListPath,
            lineMap
        )
        BroadcastSender.normalSend(
            fragment.context,
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