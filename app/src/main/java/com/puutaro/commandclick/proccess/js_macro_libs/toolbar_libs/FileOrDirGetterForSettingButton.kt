package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder


class FileOrDirGetterForSettingButton(
    terminalFragment: TerminalFragment,
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    private val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_state
    )
    private var onDirectoryPick = false
    private var parentDirPath = String()
    private val prefixRegex = Regex("^content.*fileprovider/root/storage")
    private val getFile = terminalFragment.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri ->
        if (
            uri == null
            || uri.toString() == String()
        ) return@registerForActivityResult
        val pathSource = runBlocking {
            File(
                withContext(Dispatchers.IO) {
                    URLDecoder.decode(
                        uri.toString(), Charsets.UTF_8.name()
                    )
                }.replace(prefixRegex, "/storage")
            )
        }
        val sourceFileOrDirPath = if(onDirectoryPick) {
            pathSource.parent
        } else {
            pathSource.absolutePath
        }  ?: String()
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return@registerForActivityResult
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {
                if(onDirectoryPick) return@registerForActivityResult
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

    fun get(
        parentDirPathSrc: String,
        onDirectoryPickSrc: Boolean = false
    ){
        onDirectoryPick = onDirectoryPickSrc
        parentDirPath =
            parentDirPathSrc
        getFile.launch(
            arrayOf(Intent.CATEGORY_OPENABLE)
        )
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
}
