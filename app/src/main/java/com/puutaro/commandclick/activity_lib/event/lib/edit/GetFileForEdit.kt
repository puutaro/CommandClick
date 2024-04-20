package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.getAbsolutePath
import com.blankj.utilcode.util.UriUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.GetFileEditTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder

class GetFileForEdit(
    private val activity: MainActivity,
    private val storageHelper: SimpleStorageHelper
) {

    private var onDirectoryPick = false
    private var parentDirPath = String()
    private var prefixSuffixSeparator = "&"
    private var filterPrefixListCon = String()
    private var filterSuffixListCon = String()
    private var filterShellCon = String()

    private val getFile = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (
            result.resultCode != Activity.RESULT_OK
        ) return@registerForActivityResult
        result.data?.data?.let { uri ->
            if (
                uri.toString() == String()
            ) return@registerForActivityResult
            val sourceFileOrDirPath = makeSourceFileOrDirPath(uri)
            if(
                sourceFileOrDirPath.isEmpty()
            ) return@registerForActivityResult
            registerFileHandler(sourceFileOrDirPath)
        }
    }

    fun get(
        parentDirPathSrc: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellCon: String,
        onDirectoryPickSrc: Boolean = false
    ){
        onDirectoryPick = onDirectoryPickSrc
        parentDirPath =
            parentDirPathSrc
        this.filterPrefixListCon = filterPrefixListCon
        this.filterSuffixListCon = filterSuffixListCon
        this.filterShellCon = filterSuffixListCon
        when(Build.VERSION.SDK_INT < 30){
            true -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"  // Set the MIME type to filter files
//            val uri = Uri.parse(
//                "content://com.android.externalstorage.documents/document/primary:$folderName")
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
                }
                getFile.launch(
                    intent
                )
            }
            else -> getFileOverSdk30()
        }
    }

    private fun getFileOverSdk30(){
        when (onDirectoryPick) {
            true -> {
                storageHelper.openFolderPicker()
                storageHelper.onFolderSelected = {
                        requestCode, folder ->
                    val absolutePath = folder.getAbsolutePath(activity)
                        .split("\n").filter {
                            FilterPathTool.isFilterByDir(
                                it,
                                parentDirPath,
                                filterPrefixListCon,
                                filterSuffixListCon,
                                prefixSuffixSeparator,
                            )
                        }.firstOrNull() ?: String()
                    registerFileHandler(absolutePath)
                }
                return
            }

            else -> {
                storageHelper.openFilePicker()
                storageHelper.onFileSelected = {
                        requestCode, file ->
                    file.getOrNull(0)
                        ?.getAbsolutePath(activity)?.split("\n")?.filter{
                            FilterPathTool.isFilterByFile(
                                it,
                                parentDirPath,
                                filterPrefixListCon,
                                filterSuffixListCon,
                                prefixSuffixSeparator,
                            )
                        }?.firstOrNull()?.let {
                            registerFileHandler(it)
                        } ?: String()

                }
            }
        }
    }

    private fun execGetForNormal(
        editFragment: EditFragment,
        sourceFilePath: String,
    ){
        ExecAddForListIndexAdapter.execAddByCopyFileHere(
            editFragment,
            sourceFilePath,
        )
    }

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

    private fun makeSourceFileOrDirPath(
        uri: Uri?
    ): String {
        val pathSource = runBlocking {
            GetFileEditTool.makeGetName(
                uri
            )
        }
        return if(onDirectoryPick) {
            listOf(pathSource.parent ?: String()).filter {
                FilterPathTool.isFilterByDir(
                    it,
                    parentDirPath,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    prefixSuffixSeparator,
                )
            }.firstOrNull() ?: String()
        } else {
            listOf(pathSource.absolutePath).filter {
                FilterPathTool.isFilterByFile(
                    it,
                    parentDirPath,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    prefixSuffixSeparator,
                )
            }.firstOrNull() ?: String()
        }
    }
    private fun registerFileHandler(
        sourceFileOrDirPath: String
    ){
        val editFragment = getEditFragment()
            ?: return
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {
                if(onDirectoryPick) return
                execGetForNormal(
                    editFragment,
                    sourceFileOrDirPath,
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> execGetForTsv(
                editFragment,
                sourceFileOrDirPath,
            )
        }
    }
}
