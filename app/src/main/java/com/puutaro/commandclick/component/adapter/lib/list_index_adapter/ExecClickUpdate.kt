package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ClickSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool

object ExecClickUpdate {

    fun update(
        editFragment: EditFragment,
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
    ){
        val clickConfigMap = listIndexArgsMaker.clickConfigMap
        val enableClickUpdate = ClickSettingsForListIndex.howEnableClickUpdate(clickConfigMap)
        if(!enableClickUpdate) return
        when(ListIndexForEditAdapter.listIndexTypeKey) {
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> updateForNormal(
                editFragment,
                listIndexListViewHolder.fileName,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> updateForTsv(
                editFragment,
                listIndexListViewHolder
            )
        }

    }

    private fun updateForTsv(
        editFragment: EditFragment,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
    ){
        val sortType = ListSettingsForListIndex.getSortType(
            editFragment,
            ListIndexForEditAdapter.indexListMap
        )
        when(sortType){
            ListSettingsForListIndex.SortByKey.SORT,
            ListSettingsForListIndex.SortByKey.REVERSE
            -> return
            ListSettingsForListIndex.SortByKey.LAST_UPDATE,
            -> {}
        }
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexForEditAdapter = editListRecyclerView.adapter as ListIndexForEditAdapter
        val tsvLine =
            listIndexForEditAdapter.listIndexList.getOrNull(
                listIndexListViewHolder.bindingAdapterPosition
            ) ?: return
        val tsvPath = FilePrefixGetter.get(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
        TsvTool.updateTsvByClick(
            tsvPath,
            tsvLine
        )
        BroadcastSender.normalSend(
            editFragment.context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

    private fun updateForNormal(
        editFragment: EditFragment,
        selectedItem: String,
    ){
        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
        FileSystems.updateLastModified(
            filterDir,
            selectedItem
        )
        BroadcastSender.normalSend(
            editFragment.context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }
}