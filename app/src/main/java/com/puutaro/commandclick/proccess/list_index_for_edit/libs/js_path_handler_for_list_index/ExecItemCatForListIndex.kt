package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ExtraMapToolForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.dialog.DialogObject

object ExecItemCatForListIndex {

    fun cat(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMap:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        if(
            ListIndexArgsMaker.judgeNoFile(selectedItem)
        ) {
            ListIndexArgsMaker.noFileToast(context)
            return
        }
        val parentDirPath =
            ListIndexForEditAdapter.filterDir
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