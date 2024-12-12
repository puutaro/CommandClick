package com.puutaro.commandclick.component.adapter.lib.edit_list_adapter

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.map.FilePrefixGetter

object ExecClickUpdate {

//    fun update(
//        fragment: Fragment,
//        fannelInfoMap: Map<String, String>,
//        setReplaceVariableMap: Map<String, String>?,
//        editComponentListAdapter: EditComponentListAdapter,
//        listIndexArgsMaker: ListIndexArgsMaker,
//        bindingAdapterPosition: Int,
//    ){
//        val clickConfigMap = listIndexArgsMaker.clickConfigPairList
//        val enableClickUpdate =
//            ClickSettingsForListIndex.howEnableClickUpdate(
//                clickConfigMap
//            )
//        if(!enableClickUpdate) return
//        updateForTsv(
//            fragment,
//            fannelInfoMap,
//            setReplaceVariableMap,
//            editComponentListAdapter,
//            bindingAdapterPosition
//        )
////        when(ListIndexAdapter.listIndexTypeKey) {
//////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
//////            -> {}
////            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
////            -> updateForNormal(
////                editFragment,
////                listIndexListViewHolder.fileName,
////            )
////            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
////            -> updateForTsv(
////                editFragment,
////                listIndexListViewHolder
////            )
////        }
//
//    }

    private fun updateForTsv(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editConstraintListAdapter: EditConstraintListAdapter,
        bindingAdapterPosition: Int,
    ){
//        val editComponentAdapter =
//            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val sortType = ListSettingsForEditList.getSortType(
            fannelInfoMap,
            setReplaceVariableMap,
            editConstraintListAdapter.indexListMap
        )
        when(sortType){
            ListSettingsForEditList.SortByKey.SORT,
            ListSettingsForEditList.SortByKey.REVERSE
            -> return
            ListSettingsForEditList.SortByKey.LAST_UPDATE,
            -> {}
        }
//        val binding = editFragment.binding
//        val editListRecyclerView = binding.editListRecyclerView
//        val editComponentListAdapter = editListRecyclerView.adapter as EditComponentListAdapter
        val lineMap =
            editConstraintListAdapter.lineMapList.getOrNull(
                bindingAdapterPosition
            ) ?: return
        val mapListPath = FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            editConstraintListAdapter.indexListMap,
            ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
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