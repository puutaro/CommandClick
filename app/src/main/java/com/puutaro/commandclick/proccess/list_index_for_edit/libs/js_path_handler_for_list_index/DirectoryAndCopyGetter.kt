package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder

class DirectoryAndCopyGetter(
    private val editFragment: EditFragment,
) {
    private val context = editFragment.context
    private var parentDirPath = String()
    private var selectedItemForCopy = String()
    private val prefixRegex = Regex("^content.*fileprovider/root/storage")
    private var listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder? = null

    private val getDirectoryAndCopy = editFragment.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri ->
        if (
            uri == null
            || uri.toString() == String()
        ) return@registerForActivityResult
        if(
            NoFileChecker.isNoFile(
                editFragment.context,
                parentDirPath,
                selectedItemForCopy,
            )
        ) return@registerForActivityResult
        val pathSource = runBlocking {
            File(
                withContext(Dispatchers.IO) {
                    URLDecoder.decode(
                        uri.toString(), Charsets.UTF_8.name()
                    )
                }.replace(prefixRegex, "/storage")
            )
        }
        val targetDirectoryPath =
            pathSource.parent ?: String()
        val sourceScriptFilePath = "${parentDirPath}/${selectedItemForCopy}"
        val targetScriptFilePathSource = "${targetDirectoryPath}/${selectedItemForCopy}"
        val targetScriptFilePath = when(
            targetScriptFilePathSource == sourceScriptFilePath
        ) {
            true -> "${targetDirectoryPath}/" +
                    "${CommandClickScriptVariable.makeCopyPrefix()}_${selectedItemForCopy}"
            else -> targetScriptFilePathSource
        }
        val insertFilePath = FileSystems.execCopyFileWithDir(
            File(sourceScriptFilePath),
            File(targetScriptFilePath),
        )
        ExecAddForListIndexAdapter.sortInAddFile(
            editFragment,
            insertFilePath,
        )
        ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
            editFragment,
            ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        )
        Toast.makeText(
            context,
            "copy file ok",
            Toast.LENGTH_LONG
        ).show()
    }

    fun get(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolderSrc: ListIndexForEditAdapter.ListIndexListViewHolder,
        extraMapForJsPath:  Map<String, String>?,
    ){
        listIndexListViewHolder = listIndexListViewHolderSrc
        val selectedItem = listIndexListViewHolderSrc.fileName
        parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            listIndexArgsMaker.editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
        selectedItemForCopy = selectedItem
        getDirectoryAndCopy.launch(
            arrayOf(Intent.CATEGORY_OPENABLE)
        )
    }
}