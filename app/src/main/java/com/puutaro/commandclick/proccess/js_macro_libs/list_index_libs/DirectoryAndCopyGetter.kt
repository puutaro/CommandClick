package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.RequestCode
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.ItemPathMaker
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import java.io.File

class DirectoryAndCopyGetter(
    private val editFragment: EditFragment,
) {
    private val context = editFragment.context
    private val storageHelper = SimpleStorageHelper(editFragment)
    fun get(
        selectedItem: String,
        listIndexPosition: Int,
        initialPath: String,
        pickerMacro: FilePickerTool.PickerMacro?,
        currentFannelName: String,
        tag: String,
    ){
        if(
            context == null
        ) return
        val copySrcFilePath = ItemPathMaker.make(
            editFragment,
            selectedItem,
            listIndexPosition,
        ) ?: return
        val copySrcFilePathObj = File(copySrcFilePath)
        val copySrcFileParentDirPath = copySrcFilePathObj.parent
            ?: return
        val parentDirPath = copySrcFileParentDirPath
        val srcFileName = copySrcFilePathObj.name
        val selectedItemForCopy = srcFileName
        if(
            NoFileChecker.isNoFile(
                parentDirPath,
                selectedItemForCopy,
            )
        ) return
        when(initialPath.isEmpty()){
            true -> storageHelper.openFolderPicker()
            else -> storageHelper.openFolderPicker(
                RequestCode.FOLDER_PICKER_FOR_DIR_AND_COPY.code,
                FileFullPath(
                    context,
                    initialPath,
                )
            )
        }
        storageHelper.onFolderSelected = {
                requestCode, folder ->
            val targetDirectoryPath = folder.getAbsolutePath(context)
            FilePickerTool.registerRecentDir(
                currentFannelName,
                tag,
                pickerMacro,
                targetDirectoryPath,
            )
            execCopy(
                parentDirPath,
                selectedItemForCopy,
                targetDirectoryPath
            )
        }
    }

    private fun execCopy(
        parentDirPath: String,
        selectedItemForCopy: String,
        targetDirectoryPath: String
    ){
        if(
            context == null
        ) return
        val sourceScriptFilePath = "${parentDirPath}/${selectedItemForCopy}"
        val targetScriptFilePathSource = "${targetDirectoryPath}/${selectedItemForCopy}"
        val targetScriptFilePath = when(
            targetScriptFilePathSource == sourceScriptFilePath
        ) {
            true -> "${targetDirectoryPath}/" +
                    "${CommandClickScriptVariable.makeRndPrefix()}_${selectedItemForCopy}"
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
                ListIndexAdapter.indexListMap,
                ListIndexAdapter.listIndexTypeKey
            )
        )
        ToastUtils.showLong("copy file ok")
    }

}