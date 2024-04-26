package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.widget.EditText
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.RequestCode
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool

object ExecFileChooser {

    private var prefixSuffixSeparator = "&"
    fun exec(
        activity: MainActivity,
        storageHelper: SimpleStorageHelper,
        onDirectoryPick: Boolean,
        insertEditText: EditText,
        chooserMap: Map<String, String>?
    ){
        val initialPath =
            chooserMap?.get(
                EditSettingExtraArgsTool.ExtraKey.INITIAL_PATH.key
            ) ?: String()
        val filterPrefixListCon =
            chooserMap?.get(
                EditSettingExtraArgsTool.ExtraKey.FILTER_PREFIX.key
            ) ?: String()
        val filterSuffixListCon =
            chooserMap?.get(
                EditSettingExtraArgsTool.ExtraKey.FILTER_SUFFIX.key
            ) ?: String()
        val filterShellCon =
            chooserMap?.get(
                EditSettingExtraArgsTool.ExtraKey.SHELL_PATH.key
            ) ?: String()
        when(onDirectoryPick) {
            true -> SetDir.handle(
                activity,
                storageHelper,
                initialPath,
                filterPrefixListCon,
                filterSuffixListCon,
                insertEditText,
            )
            else -> SetFile.handle(
                activity,
                storageHelper,
                initialPath,
                filterPrefixListCon,
                filterSuffixListCon,
                insertEditText,
            )
        }
    }

    private object SetFile {
        fun handle(
            activity: MainActivity,
            storageHelper: SimpleStorageHelper,
            initialPath: String,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            insertEditText: EditText,
        ){
            when(
                initialPath.isEmpty()
            ){
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
            storageHelper.onFileSelected = {
                    requestCode, file ->
                execSetFile(
                    activity,
                    file,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    insertEditText,
                )
            }
        }

        private fun execSetFile(
            activity: MainActivity,
            file: List<DocumentFile>,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            insertEditText: EditText,
        ){
            val absolutePath = file.getOrNull(0)
                ?.getAbsolutePath(activity)?.split("\n")?.filter{
                    FilterPathTool.isFilterByFile(
                        it,
                        String(),
                        filterPrefixListCon,
                        filterSuffixListCon,
                        prefixSuffixSeparator,
                    )
                }?.firstOrNull()
            if(
                absolutePath.isNullOrEmpty()
            ) return
            insertEditText.setText(absolutePath)
        }
    }

    private object SetDir {
        fun handle(
            activity: MainActivity,
            storageHelper: SimpleStorageHelper,
            initialPath: String,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            insertEditText: EditText,
        ){
            when(initialPath.isEmpty()){
                true -> storageHelper.openFolderPicker()
                else -> storageHelper.openFolderPicker(
                    RequestCode.FOLDER_PICKER_FOR_GET_FILE.code,
                    FileFullPath(
                        activity,
                        initialPath
                    )
                )
            }
            storageHelper.onFolderSelected = {
                    requestCode, folder ->
                execSetDir(
                    activity,
                    folder,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    insertEditText,
                )
            }
        }

        private fun execSetDir(
            activity: MainActivity,
            folder: DocumentFile,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            insertEditText: EditText,
        ){
            val absolutePath = folder.getAbsolutePath(activity)
                .split("\n").firstOrNull {
                    FilterPathTool.isFilterByDir(
                        it,
                        String(),
                        filterPrefixListCon,
                        filterSuffixListCon,
                        prefixSuffixSeparator,
                    )
                } ?: String()
            if(
                absolutePath.isEmpty()
            ) return
            insertEditText.setText(absolutePath)
        }

    }

}