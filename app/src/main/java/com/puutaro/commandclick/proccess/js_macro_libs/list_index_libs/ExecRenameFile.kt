package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.filer.FileRenamer
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvLineRenamer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExecRenameFile {
    fun rename(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        CoroutineScope(Dispatchers.Main).launch {
            when (type) {
                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
                -> execFileRename(
                    editFragment,
                    selectedItem,
                )

                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
                -> execTsvLineFileRename(
                    editFragment,
                    listIndexPosition,
                )
            }
        }
    }

    private fun execFileRename(
        editFragment: EditFragment,
        selectedItem: String,
    ){
        val context = editFragment.context ?: return
        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
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
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
        val context = editFragment.context ?: return
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val selectedTsvLine =
            listIndexForEditAdapter.listIndexList.getOrNull(
                listIndexPosition
            ) ?: return
        val tsvPath = FilePrefixGetter.get(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        ) ?: String()
        val isExist = ReadText(
            tsvPath
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