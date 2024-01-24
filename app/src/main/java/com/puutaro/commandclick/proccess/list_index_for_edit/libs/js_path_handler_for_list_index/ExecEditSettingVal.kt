package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FormDialogForListIndexOrButton
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker

object ExecEditSettingVal {
    fun edit(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        if(
            ListIndexArgsMaker.judgeNoFile(selectedItem)
        ) {
            ListIndexArgsMaker.noFileToast(context)
            return
        }
        val formDialogForListIndexOrButton = FormDialogForListIndexOrButton(
            editFragment
        )
        formDialogForListIndexOrButton.create(
            "edit setting variable",
            ListIndexForEditAdapter.filterDir,
            selectedItem,
            "setting"
        )
    }
}