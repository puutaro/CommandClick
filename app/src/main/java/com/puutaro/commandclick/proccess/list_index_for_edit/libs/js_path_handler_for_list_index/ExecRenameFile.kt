package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.filer.FileRenamer
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvLineRenamer
import java.io.File

object ExecRenameFile {
    fun rename(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        selectedItem: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> execFileRename(
                listIndexArgsMaker,
                selectedItem,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> execTsvLineFileRename(
                listIndexArgsMaker,
                listIndexListViewHolder,
            )
        }
    }

    private fun execFileRename(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            listIndexArgsMaker.editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )

        if(
            NoFileChecker.isNoFile(
                context,
                parentDirPath,
                selectedItem,
            )
        ) return
        FileRenamer.rename(
            editFragment,
            parentDirPath,
            selectedItem
        )
    }

    private fun execTsvLineFileRename(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val selectedTsvLine =
            listIndexForEditAdapter.listIndexList.getOrNull(
                listIndexListViewHolder.bindingAdapterPosition
            ) ?: return
        val tsvPath = ListSettingsForListIndex.getListSettingKeyHandler(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
        val tsvPathObj = File(tsvPath)
        val tsvParentDirPath = tsvPathObj.parent ?: return
        val tsvName = tsvPathObj.name
        val isExist = ReadText(
            tsvParentDirPath,
            tsvName
        ).textToList().contains(selectedTsvLine)
        if(!isExist){
            Toast.makeText(
                context,
                "No exist",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        TsvLineRenamer.rename(
            editFragment,
            tsvPath,
            selectedTsvLine
        )



    }
}