package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ExtraMapToolForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.FileSystems
import java.io.File

object ExecCopyFileHere {
    fun copyFileHere(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        if(
            ListIndexArgsMaker.judgeNoFile(selectedItem)
        ) {
            ListIndexArgsMaker.noFileToast(
                context
            )
            return
        }
        val parentDirPath =
            ListIndexForEditAdapter.filterDir
        val srcFilePath = "${parentDirPath}/${selectedItem}"
        val destiFilePath = "${parentDirPath}/${selectedItem}"
        FileSystems.execCopyFileWithDir(
            File(srcFilePath),
            File(destiFilePath),
        )
        Toast.makeText(
            context,
            "Copy ok",
            Toast.LENGTH_SHORT
        ).show()
        ListIndexForEditAdapter.listIndexListUpdateFileList(
            editFragment,
            ListIndexForEditAdapter.makeFileListHandler(
                editFragment.busyboxExecutor,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        )
    }
}