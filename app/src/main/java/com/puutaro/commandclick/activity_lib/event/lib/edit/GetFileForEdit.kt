package com.puutaro.commandclick.activity_lib.event.lib.edit

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.RequestCode
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.CcFilterTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

class GetFileForEdit(
    private val activity: MainActivity,
    private val storageHelper: SimpleStorageHelper
) {
    private var prefixSuffixSeparator = "&"

    fun get(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
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
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
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
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
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
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
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
                    CcFilterTool.isFilterByDir(
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
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
                absolutePath,
                true,
            )
        }
    }

    private fun execGetFile(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
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
                    CcFilterTool.isFilterByFile(
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
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        editListRecyclerView,
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
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
        sourceFilePath: String,
    ){
        val sourceScriptFilePathObj = File(sourceFilePath)
        val sourceScriptName = sourceScriptFilePathObj.name
        val insertLine = "${sourceScriptName}\t${sourceFilePath}"
        ExecAddForListIndexAdapter.execAddForTsv(
            fragment.context,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
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
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
        sourceFileOrDirPath: String,
        onDirectoryPick: Boolean,
    ){
        val editFragment = getEditFragment()
            ?: return
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        execGetForTsv(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
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
