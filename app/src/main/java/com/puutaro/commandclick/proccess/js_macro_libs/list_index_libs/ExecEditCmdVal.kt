package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FormDialogForListIndexOrButton
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.NoFileChecker

object ExecEditCmdVal {
    fun edit(
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
        val formDialogForListIndexOrButton = FormDialogForListIndexOrButton(
            editFragment
        )
        formDialogForListIndexOrButton.create(
            "edit command variable",
            parentDirPath,
            selectedItem,
            String()
        )
    }
}