package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.widget.Toast
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
        val context = editFragment.context
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        val extractedPath = when(type) {
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> getCatPathForNomal(
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
            Toast.makeText(
                context,
                "Not exist: ${extractedPath}",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        if(
            !File(extractedPath).isFile
        ) {
            Toast.makeText(
                context,
                "Not exist: ${extractedPath}",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        return extractedPath
    }

    private fun getCatPathForNomal(
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
        val context = editFragment.context
            ?: return null
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
            Toast.makeText(
                context,
                "No exist tsv path",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        val titleFileNameAndPathConPair =
            TitleFileNameAndPathConPairForListIndexAdapter.get(selectedTsvLine)
                ?: return null
        val filePathOrCon = titleFileNameAndPathConPair.second
        return filePathOrCon
    }
}