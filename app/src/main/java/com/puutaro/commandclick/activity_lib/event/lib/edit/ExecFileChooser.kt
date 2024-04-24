package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.widget.EditText
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.RequestCode

object ExecFileChooser {
    fun exec(
        activity: MainActivity,
        storageHelper: SimpleStorageHelper,
        onDirectoryPick: Boolean,
        insertEditText: EditText,
        initialPath: String,
    ){
        if (onDirectoryPick) {
            storageHelper.openFolderPicker()
            storageHelper.onFolderSelected = {
                    requestCode, folder ->
                val absolutePath = folder.getAbsolutePath(activity)
                insertEditText.setText(absolutePath)
            }
            return
        }
        when(initialPath.isEmpty()){
            true ->
                storageHelper.openFilePicker(
                    filterMimeTypes = arrayOf("*/*")
                )
            else -> storageHelper.openFilePicker(
                RequestCode.FILE_PICKER_FOR_GET_FILE.code,
                false,
                FileFullPath(
                    activity,
                    initialPath,
                ),
                "*/*",
            )
        }
        storageHelper.onFileSelected = { requestCode, file ->
            val absolutePath = file.getOrNull(0)?.getAbsolutePath(activity)
            insertEditText.setText(absolutePath)
        }
    }
}