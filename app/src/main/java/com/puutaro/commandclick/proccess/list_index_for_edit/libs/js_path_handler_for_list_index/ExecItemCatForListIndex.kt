package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.dialog.DialogObject
import com.puutaro.commandclick.util.file.NoFileChecker

object ExecItemCatForListIndex {

    fun cat(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMap:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
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
        val scriptContents = ReadText(
            parentDirPath,
            selectedItem
        ).readText()
        val displayContents = "\tpath: ${parentDirPath}/${selectedItem}" +
                "\n---\n${scriptContents}"
        DialogObject.simpleTextShow(
            context,
            "Show contents",
            displayContents
        )
    }
}