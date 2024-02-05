package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.TitleFileNameAndPathConPairForListIndexAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.editor.EditorByIntent
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecWriteItem {
    fun write(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> writeFile(
                listIndexArgsMaker,
                listIndexListViewHolder,
                extraMapForJsPath,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> writeFileInTsvLine(
                listIndexArgsMaker,
                listIndexListViewHolder,
                extraMapForJsPath,
            )
        }

    }

    private fun writeFile(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val selectedItem = listIndexListViewHolder.fileName
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
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
        val editorByIntent = EditorByIntent(
            parentDirPath,
            selectedItem,
            context
        )
        editorByIntent.byIntent()
    }

    private fun writeFileInTsvLine(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        extraMapForJsPath:  Map<String, String>?,
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
        val tsvPath = FilePrefixGetter.get(
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
        val titleFileNameAndPathConPair =
            TitleFileNameAndPathConPairForListIndexAdapter.get(selectedTsvLine)
                ?: return
        val filePathOrCon = titleFileNameAndPathConPair.second
        val filePathOrConObj = File(filePathOrCon)
        val isWithFileRename = filePathOrConObj.isFile
        if(!isWithFileRename) return
        val parentDirPath = filePathOrConObj.parent ?: return
        val fileName = filePathOrConObj.name
        val editorByIntent = EditorByIntent(
            parentDirPath,
            fileName,
            context
        )
        editorByIntent.byIntent()
    }
}