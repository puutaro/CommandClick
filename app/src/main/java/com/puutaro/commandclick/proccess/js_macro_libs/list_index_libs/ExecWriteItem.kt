package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.TitleFileNameAndPathConPairForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.editor.EditorByIntent
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecWriteItem {
    fun write(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> writeFile(
                editFragment,
                selectedItem,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> writeFileInTsvLine(
                editFragment,
                listIndexPosition,
            )
        }

    }

    private fun writeFile(
        editFragment: EditFragment,
        selectedItem: String,
    ){
        val context = editFragment.context
        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
        if(
            NoFileChecker.isNoFile(
                parentDirPath,
                selectedItem,
            )
        ) return
        val editorByIntent = EditorByIntent(
//            parentDirPath,
            selectedItem,
            context
        )
        editorByIntent.byIntent()
    }

    private fun writeFileInTsvLine(
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
        ) ?: return
        val isExist = ReadText(tsvPath)
            .textToList()
            .contains(selectedTsvLine)
        if(!isExist){
            ToastUtils.showShort("No exist")
            return
        }
        val titleFileNameAndPathConPair =
            TitleFileNameAndPathConPairForListIndexAdapter.get(selectedTsvLine)
                ?: return
        val filePathOrCon = titleFileNameAndPathConPair.second
        val filePathOrConObj = File(filePathOrCon)
        val isWithFileRename = filePathOrConObj.isFile
        if(!isWithFileRename) return
//        val parentDirPath = filePathOrConObj.parent ?: return
        val fileName = filePathOrConObj.name
        val editorByIntent = EditorByIntent(
//            parentDirPath,
            fileName,
            context
        )
        editorByIntent.byIntent()
    }
}