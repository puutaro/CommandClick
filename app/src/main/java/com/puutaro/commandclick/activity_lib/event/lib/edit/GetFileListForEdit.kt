package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.RequestCode
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

class GetFileListForEdit (
    private val activity: MainActivity,
    private val storageHelper: SimpleStorageHelper
) {

    private var onDirectoryPick = false
    private var parentDirPath = String()
    private var prefixSuffixSeparator = "&"

    fun get(
        parentDirPathSrc: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
        initialPath: String,
        onDirectoryPickSrc: Boolean = false,
    ){
        onDirectoryPick = onDirectoryPickSrc
        parentDirPath =
            parentDirPathSrc
        execGetFileList(
            filterPrefixListCon,
            filterSuffixListCon,
            filterShellCon,
            initialPath,
        )
    }

    private fun execGetFileList(
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
        initialPath: String,
    ){
        when(initialPath.isEmpty()){
            true -> storageHelper.openFolderPicker()
            else -> storageHelper.openFolderPicker(
                    RequestCode.FOLDER_PICKER_FOR_GET_FILE_LIST.code,
                    FileFullPath(
                        activity,
                        initialPath
                    )
                )
        }

        storageHelper.onFolderSelected = {
                requestCode, folder ->
            val srcDirPath = folder.getAbsolutePath(activity)
            val  srcFirOrDirList = when(onDirectoryPick){
                true -> showDirNameList(
                    srcDirPath,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    filterShellCon,
                )
                else -> showFileNameList(
                    srcDirPath,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    filterShellCon,
                )
            }.map {
                File(srcDirPath, it).absolutePath
            }
            registerFileHandler(srcFirOrDirList)
        }
    }

    private fun showDirNameList(
        srcDirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
    ): List<String> {
        val dirList = FileSystems.showDirList(srcDirPath)
        return dirList.filter {
            FilterPathTool.isFilterByDir(
                it,
                srcDirPath,
                filterPrefixListCon,
                filterSuffixListCon,
                true,
                prefixSuffixSeparator,
            )
        }
    }

    private fun showFileNameList(
        srcDirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
    ): List<String> {
        val fileList = FileSystems.sortedFiles(srcDirPath)
        return fileList.filter {
            FilterPathTool.isFilterByFile(
                it,
                srcDirPath,
                filterPrefixListCon,
                filterSuffixListCon,
                true,
                prefixSuffixSeparator,
            )
        }
    }

    private fun execGetFileOrDirListForTsv(
        editFragment: EditFragment,
        sourceFilePathList: List<String>,
    ){
        val updateInsertLineList = sourceFilePathList.map {
            val insertFileObj = File(it)
            "${insertFileObj.name}\t${insertFileObj.absoluteFile}"
        }
        ExecAddForListIndexAdapter.execAddListForTsv(
            editFragment,
            updateInsertLineList
        )
    }

    private fun getEditFragment(
    ): EditFragment? {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val readSharePreferenceMap = SharePrefTool.makeReadSharePrefMapByShare(
            sharedPref
        )
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val currentFannelState = SharePrefTool.getCurrentStateName(
            readSharePreferenceMap
        )
        return TargetFragmentInstance().getCurrentEditFragmentFromActivity(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        )
    }

    private fun registerFileHandler(
        srcFileOrDirList: List<String>
    ){
        val editFragment = getEditFragment()
            ?: return
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "getfile_registerFileHandler.txt").absolutePath,
//            listOf(
//                "listIndexConfigMap: ${editFragment.listIndexConfigMap}",
//                "indexListMap ${ListIndexForEditAdapter.indexListMap}",
//                "srcFileOrDirList: ${srcFileOrDirList}",
//                "type: ${type}",
//            ).joinToString("\n")
//        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {
                if(onDirectoryPick) {
                    ExecAddForListIndexAdapter.execAddByCopyDirListHere(
                        editFragment,
                        srcFileOrDirList
                    )
                    return
                }
                ExecAddForListIndexAdapter.execAddByCopyFileListHere(
                    editFragment,
                    srcFileOrDirList
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            ->  execGetFileOrDirListForTsv(
                editFragment,
                srcFileOrDirList,
            )
        }
    }
}
