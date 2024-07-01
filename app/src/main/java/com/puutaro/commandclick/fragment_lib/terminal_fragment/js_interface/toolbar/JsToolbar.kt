package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.SiteUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JsToolbar(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = SharePrefTool.getCurrentStateName(
        readSharePreferenceMap
    )
    private val targetFragmentInstance = TargetFragmentInstance()
    private val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
        activity,
        currentAppDirPath,
        currentFannelName,
        currentFannelState
    )

    @JavascriptInterface
    fun getListPath(): String? {
        if(
            editFragment == null
        ) return null
        val listKeyCon = FilePrefixGetter.get(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
        return listKeyCon
    }

    @JavascriptInterface
    fun addUrlCon_S(
        title: String,
        con: String,
        urlConSaveParentDirPathSrc: String,
        compSuffix: String,
    ){
        val listIndexPath = getListPath()
            ?: return
        val targetFragmentInstance = TargetFragmentInstance()
        val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val listIndexType = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        val extraMap = makeSaveDirPathAndCompSuffixMap(
            urlConSaveParentDirPathSrc,
            compSuffix,
        )
        val urlConSaveParentDirPath = makeUrlConSaveParentDirPath(
            extraMap,
            listIndexType,
            listIndexPath
        ) ?: return
        val fileName = when(
            title.isEmpty()
        ) {
            true -> CommandClickScriptVariable.makeCopyPrefix()
            else -> title.replace(
                Regex(
                    "[\"#$%&'()~^|{}\\[\\];:`<>*\t]"),
                String()
            )
        }
        val compFileName = EditSettingExtraArgsTool.makeCompFileName(
            editFragment,
            fileName,
            extraMap
        )
        FileSystems.writeFile(
            File(
                urlConSaveParentDirPath,
                compFileName
            ).absolutePath,
            con,
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                addAndSort(
                    listIndexType,
                    listIndexPath,
                    urlConSaveParentDirPath,
                    compFileName,
                )
            }
        }
    }

    @JavascriptInterface
    fun addUrl_S(
        title: String,
        urlString: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val siteTitle = withContext(Dispatchers.IO) {
                if(
                    title.isNotEmpty()
                ) return@withContext title
                val siteTitleSrc = SiteUrl.getTitle(
                    context,
                    urlString
                )
                if(
                    siteTitleSrc.isNotEmpty()
                ) return@withContext siteTitleSrc
                urlString
            }
            val insertLine = "${siteTitle}\t${urlString}"
            withContext(Dispatchers.Main) {
                val targetFragmentInstance = TargetFragmentInstance()
                val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
                    activity,
                    currentAppDirPath,
                    currentFannelName,
                    currentFannelState
                ) ?: return@withContext
                try {
                    ExecAddForListIndexAdapter.execAddForTsv(
                        editFragment,
                        insertLine
                    )
                } catch(e: Exception){
                    LogSystems.stdErr(
                        context,
                        "$e"
                    )
                }
            }
        }
    }

    private fun makeSaveDirPathAndCompSuffixMap(
        urlConSaveParentDirPath: String,
        compSuffix: String,
    ): Map<String, String> {
        return mapOf(
                EditSettingExtraArgsTool.ExtraKey.PARENT_DIR_PATH.key
                        to urlConSaveParentDirPath,
                EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key
                        to compSuffix,
            )
    }

    private fun makeUrlConSaveParentDirPath(
        extraMap: Map<String, String>,
        listIndexType: TypeSettingsForListIndex.ListIndexTypeKey,
        listIndexPath: String
    ): String? {
        if(
            editFragment == null
        ) return null
        return when(listIndexType){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> EditSettingExtraArgsTool.getParentDirPath(
                    extraMap,
                    currentAppDirPath,
                )
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> listIndexPath
        }
    }

    private fun addAndSort(
        listIndexType: TypeSettingsForListIndex.ListIndexTypeKey,
        listIndexPath: String,
        urlConSaveParentDirPath: String,
        fileName: String,
    ){
        if(
            editFragment == null
        ) return
        when(listIndexType){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> {
                val insertLine = listOf(
                    fileName,
                    File(urlConSaveParentDirPath, fileName).absolutePath
                ).joinToString("\t")
                ExecAddForListIndexAdapter.execAddForTsv(
                    editFragment,
                    insertLine
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> ExecAddForListIndexAdapter.sortInAddFile(
                editFragment,
                File(listIndexPath, fileName).absolutePath
            )
        }
    }
}