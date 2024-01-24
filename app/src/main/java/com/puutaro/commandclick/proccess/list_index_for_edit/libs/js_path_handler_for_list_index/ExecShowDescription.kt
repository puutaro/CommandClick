package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.ReadText

object ExecShowDescription {
    fun desc(
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
        ScriptFileDescription.show(
            editFragment,
            ReadText(
                ListIndexForEditAdapter.filterDir,
                selectedItem
            ).textToList(),
            ListIndexForEditAdapter.filterDir,
            selectedItem
        )
    }
}