package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ExtraMapToolForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.editor.EditorByIntent

object ExecWriteItem {
    fun write(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
        if(
            ListIndexArgsMaker.judgeNoFile(selectedItem)
        ) {
            ListIndexArgsMaker.noFileToast(context)
            return
        }
        val parentDirPath =
            ListIndexForEditAdapter.filterDir
        val editorByIntent = EditorByIntent(
            parentDirPath,
            selectedItem,
            context
        )
        editorByIntent.byIntent()
    }
}