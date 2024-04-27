package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.widget.EditText
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.RequestCode
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool

object ExecFileChooser {

    private var prefixSuffixSeparator = "&"
    fun exec(
        activity: MainActivity,
        storageHelper: SimpleStorageHelper,
        onDirectoryPick: Boolean,
        insertEditText: EditText,
        chooserMap: Map<String, String>?,
        fannelName: String,
        currentVariableName: String,
    ){
        val pickerMacroStr = chooserMap?.get(
            EditSettingExtraArgsTool.ExtraKey.MACRO.key,
        )
        val pickerMacro = FilePickerTool.PickerMacro.values().firstOrNull {
            it.name == pickerMacroStr
        }
        val initialPath = FilePickerTool.makeInitialDirPath(
            chooserMap,
            fannelName,
            pickerMacro,
            currentVariableName,
        )
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
                fannelName,
                pickerMacro,
                currentVariableName
            )
            else -> SetFile.handle(
                activity,
                storageHelper,
                initialPath,
                filterPrefixListCon,
                filterSuffixListCon,
                insertEditText,
                fannelName,
                pickerMacro,
                currentVariableName,
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
            fannelName: String,
            pickerMacro: FilePickerTool.PickerMacro?,
            currentVariableName: String,
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
                    fannelName,
                    pickerMacro,
                    currentVariableName,
                )
            }
        }

        private fun execSetFile(
            activity: MainActivity,
            file: List<DocumentFile>,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            insertEditText: EditText,
            fannelName: String,
            pickerMacro: FilePickerTool.PickerMacro?,
            currentVariableName: String,
        ){
            val absolutePath = file.getOrNull(0)
                ?.getAbsolutePath(activity)?.split("\n")?.filter{
                    FilterPathTool.isFilterByFile(
                        it,
                        String(),
                        filterPrefixListCon,
                        filterSuffixListCon,
                        true,
                        prefixSuffixSeparator,
                    )
                }?.firstOrNull()
            if(
                absolutePath.isNullOrEmpty()
            ) return
            FilePickerTool.registerRecentDir(
                fannelName,
                currentVariableName,
                pickerMacro,
                absolutePath,
            )
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
            fannelName: String,
            pickerMacro: FilePickerTool.PickerMacro?,
            currentVariableName: String,
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
                    fannelName,
                    pickerMacro,
                    currentVariableName
                )
            }
        }

        private fun execSetDir(
            activity: MainActivity,
            folder: DocumentFile,
            filterPrefixListCon: String,
            filterSuffixListCon: String,
            insertEditText: EditText,
            fannelName: String,
            pickerMacro: FilePickerTool.PickerMacro?,
            currentVariableName: String,
        ){
            val absolutePath = folder.getAbsolutePath(activity)
                .split("\n").firstOrNull {
                    FilterPathTool.isFilterByDir(
                        it,
                        String(),
                        filterPrefixListCon,
                        filterSuffixListCon,
                        true,
                        prefixSuffixSeparator,
                    )
                } ?: String()
            if(
                absolutePath.isEmpty()
            ) return
            FilePickerTool.registerRecentDir(
                fannelName,
                currentVariableName,
                pickerMacro,
                absolutePath,
            )
            insertEditText.setText(absolutePath)
        }

    }

}