package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.RequestCode
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

class GetFileForEdit(
    private val activity: MainActivity,
    private val storageHelper: SimpleStorageHelper
) {
    private var prefixSuffixSeparator = "&"

    fun get(
        parentDirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
        initialPath: String,
        onDirectoryPick: Boolean = false,
        pickerMacro: FilePickerTool.PickerMacro?,
        currentFannelName: String,
        tag: String,
    ){
        when (onDirectoryPick) {
            true -> execGetDir(
                parentDirPath,
                filterPrefixListCon,
                filterSuffixListCon,
                filterShellCon,
                initialPath,
                pickerMacro,
                currentFannelName,
                tag,
            )
            else -> execGetFile(
                parentDirPath,
                filterPrefixListCon,
                filterSuffixListCon,
                filterShellCon,
                initialPath,
                pickerMacro,
                currentFannelName,
                tag,
            )
        }
    }

    private fun execGetDir(
        parentDirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
        initialPath: String,
        pickerMacro: FilePickerTool.PickerMacro?,
        currentFannelName: String,
        tag: String,
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
            val absolutePath = folder.getAbsolutePath(activity)
                .split("\n").filter {
                    FilterPathTool.isFilterByDir(
                        it,
                        parentDirPath,
                        filterPrefixListCon,
                        filterSuffixListCon,
                        true,
                        prefixSuffixSeparator,
                    )
                }.firstOrNull() ?: String()
            FilePickerTool.registerRecentDir(
                currentFannelName,
                tag,
                pickerMacro,
                absolutePath,
            )
            registerFileHandler(
                absolutePath,
                true,
            )
        }
    }

    private fun execGetFile(
        parentDirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
        initialPath: String,
        pickerMacro: FilePickerTool.PickerMacro?,
        currentFannelName: String,
        tag: String,
    ){
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

        storageHelper.onFileSelected = {
                requestCode, file ->
            file.getOrNull(0)
                ?.getAbsolutePath(activity)?.split("\n")?.filter{
                    FilterPathTool.isFilterByFile(
                        it,
                        parentDirPath,
                        filterPrefixListCon,
                        filterSuffixListCon,
                        true,
                        prefixSuffixSeparator,
                    )
                }?.firstOrNull()?.let {
                    FilePickerTool.registerRecentDir(
                        currentFannelName,
                        tag,
                        pickerMacro,
                        it,
                    )
                    registerFileHandler(
                        it,
                        false,
                    )
                } ?: String()

        }
    }

//    private fun execGetForNormal(
//        editFragment: EditFragment,
//        sourceFilePath: String,
//    ){
//        ExecAddForListIndexAdapter.execAddByCopyFileHere(
//            editFragment,
//            sourceFilePath,
//        )
//    }

    private fun execGetForTsv(
        editFragment: EditFragment,
        sourceFilePath: String,
    ){
        val sourceScriptFilePathObj = File(sourceFilePath)
        val sourceScriptName = sourceScriptFilePathObj.name
        val insertLine = "${sourceScriptName}\t${sourceFilePath}"
        ExecAddForListIndexAdapter.execAddForTsv(
            editFragment,
            insertLine
        )
    }

    private fun getEditFragment(
    ): EditFragment? {
        val sharedPref = FannelInfoTool.getSharePref(activity)
        val fannelInfoMap = FannelInfoTool.makeFannelInfoMapByShare(
            sharedPref
        )
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        return TargetFragmentInstance.getCurrentEditFragmentFromActivity(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        )
    }

    private fun registerFileHandler(
        sourceFileOrDirPath: String,
        onDirectoryPick: Boolean,
    ){
        val editFragment = getEditFragment()
            ?: return
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        execGetForTsv(
            editFragment,
            sourceFileOrDirPath,
        )
//        when(type){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////            -> {}
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> {
//                if(onDirectoryPick) return
//                execGetForNormal(
//                    editFragment,
//                    sourceFileOrDirPath,
//                )
//            }
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> execGetForTsv(
//                editFragment,
//                sourceFileOrDirPath,
//            )
//        }
    }
}
