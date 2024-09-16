package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.TitleFileNameAndPathConPairForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.editor.EditorByEditText
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecSimpleEditItem {

    fun edit(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
//            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> writeFile(
                editFragment,
                selectedItem,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> writeFileInTsvLine(
                editFragment,
                listIndexPosition
            )
        }

    }

    private fun writeFile(
        editFragment: EditFragment,
        selectedItem: String,
    ){
        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexAdapter.indexListMap,
            ListIndexAdapter.listIndexTypeKey
        )
        if(
            NoFileChecker.isNoFile(
                parentDirPath,
                selectedItem,
            )
        ) return
        val listIndexUpdateIntent = Intent()
        listIndexUpdateIntent.action = BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        execEdit(
            editFragment,
            parentDirPath,
            selectedItem,
            listIndexUpdateIntent
        )
    }

    private fun writeFileInTsvLine(
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as ListIndexAdapter
        val selectedTsvLine =
            listIndexForEditAdapter.listIndexList.getOrNull(
                listIndexPosition
            ) ?: return
        val tsvPath = FilePrefixGetter.get(
            editFragment,
            ListIndexAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        ) ?: return
        val isExist = ReadText(
            tsvPath
        ).textToList().contains(selectedTsvLine)
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
        val parentDirPath = filePathOrConObj.parent ?: return
        val fileName = filePathOrConObj.name
        val listIndexUpdateIntent = Intent()
        listIndexUpdateIntent.action = BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        execEdit(
            editFragment,
            parentDirPath,
            fileName,
            null
        )
    }

    private fun execEdit(
        editFragment: EditFragment,
        parentDirPath: String,
        selectedItem: String,
        listIndexUpdateIntent: Intent?
    ){
        val firstCon = ReadText(
            File(
                parentDirPath,
                selectedItem
            ).absolutePath
        ).readText()
        EditorByEditText.byEditText(
            editFragment,
            parentDirPath,
            selectedItem,
            firstCon,
            listIndexUpdateIntent
        )
    }
}