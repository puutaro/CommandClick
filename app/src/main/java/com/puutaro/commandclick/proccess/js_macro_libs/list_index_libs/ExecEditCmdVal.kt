package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.file.NoFileChecker

object ExecEditCmdVal {
    fun edit(
        editFragment: EditFragment,
        selectedItem: String,
    ){
//        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexAdapter.indexListMap,
//            ListIndexAdapter.listIndexTypeKey
//        )
        if(
            NoFileChecker.isNoFile(
//                parentDirPath,
                selectedItem,
            )
        ) return
//        FormDialogForListIndexOrButton.create(
//            editFragment,
//            "edit command variable",
////            parentDirPath,
//            selectedItem,
//            String()
//        )
    }
}