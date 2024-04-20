package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.ItemPathMaker
import com.puutaro.commandclick.proccess.edit.lib.GetFileEditTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
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
//    private val prefixRegex = Regex("^content.*fileprovider/root/storage")

    private val getDirectoryAndCopy = editFragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (
            result.resultCode != Activity.RESULT_OK
        ) return@registerForActivityResult
        result.data?.data?.let { uri ->
            if (
                uri.toString() == String()
            ) return@registerForActivityResult
            if(
                NoFileChecker.isNoFile(
                    editFragment.context,
                    parentDirPath,
                    selectedItemForCopy,
                )
            ) return@registerForActivityResult
            val pathSource = runBlocking {
                GetFileEditTool.makeGetName(
                    uri
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
    }
    fun get(
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val copySrcFilePath = ItemPathMaker.make(
            editFragment,
            selectedItem,
            listIndexPosition,
        ) ?: return
        val copySrcFilePathObj = File(copySrcFilePath)
        val copySrcFileParentDirPath = copySrcFilePathObj.parent
            ?: return
        parentDirPath = copySrcFileParentDirPath
        val srcFileName = copySrcFilePathObj.name
        selectedItemForCopy = srcFileName
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"  // Set the MIME type to filter files
        }
        getDirectoryAndCopy.launch(
            intent
        )
    }

}