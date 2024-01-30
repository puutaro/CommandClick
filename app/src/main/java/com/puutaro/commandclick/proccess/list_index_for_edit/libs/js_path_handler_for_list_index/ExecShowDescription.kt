package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText

object ExecShowDescription {
    fun desc(
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
        ScriptFileDescription.show(
            editFragment,
            ReadText(
                parentDirPath,
                selectedItem
            ).textToList(),
            parentDirPath,
            selectedItem
        )
    }
}