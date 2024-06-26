package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.TitleFileNameAndPathConPairForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ItemPathMaker {

    fun make(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ): String? {
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        val extractedPath = when(type) {
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> getCatPathForNormal(
                editFragment,
                selectedItem
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> getCatPathForTsv(
                editFragment,
                listIndexPosition
            )
        }?.trim()
        if(
            extractedPath.isNullOrEmpty()
        ) {
            ToastUtils.showShort("Not exist: ${extractedPath}")
            return null
        }
        if(
            !File(extractedPath).isFile
        ) {
            ToastUtils.showShort("Not exist: ${extractedPath}")
            return null
        }
        return extractedPath
    }

    private fun getCatPathForNormal(
        editFragment: EditFragment,
        selectedItem: String
    ): String {
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        return File(parentDirPath, selectedItem).absolutePath
    }

    private fun getCatPathForTsv(
        editFragment: EditFragment,
        listIndexPosition: Int,
    ): String? {
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val selectedTsvLine =
            listIndexForEditAdapter.listIndexList.getOrNull(
                listIndexPosition
            ) ?: return null
        val tsvPath = FilePrefixGetter.get(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        ) ?: return null
        val isExist = ReadText(
            tsvPath
        ).textToList().contains(selectedTsvLine)
        if(!isExist){
            ToastUtils.showShort("No exist tsv path")
            return null
        }
        val titleFileNameAndPathConPair =
            TitleFileNameAndPathConPairForListIndexAdapter.get(selectedTsvLine)
                ?: return null
        val filePathOrCon = titleFileNameAndPathConPair.second
        return filePathOrCon
    }
}