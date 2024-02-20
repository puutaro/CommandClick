package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FormDialogForListIndexOrButton
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.NoFileChecker

object ExecEditSettingVal {
    fun edit(
        editFragment: EditFragment,
        selectedItem: String,
    ){
        val context = editFragment.context ?: return
        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
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
        val formDialogForListIndexOrButton = FormDialogForListIndexOrButton(
            editFragment
        )
        formDialogForListIndexOrButton.create(
            "edit setting variable",
            parentDirPath,
            selectedItem,
            "setting"
        )
    }
}