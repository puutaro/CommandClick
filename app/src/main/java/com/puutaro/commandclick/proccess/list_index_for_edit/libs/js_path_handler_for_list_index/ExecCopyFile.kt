package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.content.Intent
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker

object ExecCopyFile {
    fun copyFile(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        if(
            ListIndexArgsMaker.judgeNoFile(selectedItem)
        ) {
            ListIndexArgsMaker.noFileToast(context)
            return
        }
        listIndexArgsMaker.editFragment.directoryAndCopyGetter?.get(
            listIndexArgsMaker,
            selectedItem,
            extraMapForJsPath
        )
    }
}