package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
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

    private val getFile = activity.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri ->
        if (
            uri == null
            || uri.toString() == String()
        ) return@registerForActivityResult
        val sourceFileOrDirPath = makeSourceFileOrDirPath(uri)
        registerFileHandler(sourceFileOrDirPath)
    }

    fun get(
        parentDirPathSrc: String,
        onDirectoryPickSrc: Boolean = false
    ){
        onDirectoryPick = onDirectoryPickSrc
        parentDirPath =
            parentDirPathSrc
        when(Build.VERSION.SDK_INT < 30){
            true -> {
                getFile.launch(arrayOf(Intent.CATEGORY_OPENABLE))
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
                    registerFileHandler(absolutePath)
                }
                return
            }

            else -> {
                storageHelper.openFilePicker()
                storageHelper.onFileSelected = {
                        requestCode, file ->
                    file.getOrNull(0)
                        ?.getAbsolutePath(activity)?.let {
                            registerFileHandler(it)
                        }

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
            val prefixRegex = Regex("^content.*fileprovider/root/storage")
            File(
                withContext(Dispatchers.IO) {
                    URLDecoder.decode(
                        uri.toString(), Charsets.UTF_8.name()
                    )
                }.replace(prefixRegex, "/storage")
            )
        }
        return if(onDirectoryPick) {
            pathSource.parent
        } else {
            pathSource.absolutePath
        }  ?: String()
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