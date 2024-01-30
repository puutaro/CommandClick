package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.editor.EditorByIntent
import com.puutaro.commandclick.util.file.NoFileChecker

object ExecWriteItem {
    fun write(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
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
        val editorByIntent = EditorByIntent(
            parentDirPath,
            selectedItem,
            context
        )
        editorByIntent.byIntent()
    }
}