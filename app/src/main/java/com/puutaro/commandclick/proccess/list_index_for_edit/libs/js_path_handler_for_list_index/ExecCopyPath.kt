package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.NoFileChecker

object ExecCopyPath {
    fun copyPath(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
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
        val selectedItemPath = "${parentDirPath}/${selectedItem}"
        val clipboard = context.getSystemService(
            Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(
            "cmdclick path",
            selectedItemPath
        )
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            "copy ok",
            Toast.LENGTH_SHORT
        ).show()
    }
}