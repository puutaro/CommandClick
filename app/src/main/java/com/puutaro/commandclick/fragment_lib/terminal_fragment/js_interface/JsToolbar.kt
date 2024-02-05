package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.SiteUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JsToolbar(
    private val terminalFragment: TerminalFragment
) {

    private val activity = terminalFragment.activity
    private val readSharedPreferences = terminalFragment.readSharedPreferences
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharedPreferences,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharedPreferences,
        SharePrefferenceSetting.current_fannel_name
    )
    private val targetFragmentInstance = TargetFragmentInstance()
    private val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
        activity,
        currentAppDirPath,
        currentFannelName,
    )

    @JavascriptInterface
    fun getListPath(): String? {
        if(
            editFragment == null
        ) return null
        return FilePrefixGetter.get(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
        )
    }

    @JavascriptInterface
    fun addUrlCon(
        clickKey: String,
        buttonType: String,
        fileName: String,
        con: String,
    ){
        val listIndexPath = getListPath()
            ?: return
        val targetFragmentInstance = TargetFragmentInstance()
        val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
        ) ?: return
        val listIndexType = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        val extraMap = makeExtraMap(
            editFragment,
            clickKey,
            buttonType,
        )
        val parentDirPath = makeParentDirPath(
            extraMap,
            listIndexType,
            listIndexPath
        ) ?: return
        val compFileName = ExtraArgsTool.makeCompFileName(
            fileName,
            extraMap
        )
        FileSystems.writeFile(
            File(
                parentDirPath,
                compFileName
            ).absolutePath,
            con,
        )
        addAndSort(
            listIndexType,
            listIndexPath,
            parentDirPath,
            compFileName,
        )
    }

    @JavascriptInterface
    fun addUrl(
        urlString: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val siteTitle = withContext(Dispatchers.IO) {
                val siteTitleSrc = SiteUrl.getTitle(urlString)
                if(
                    siteTitleSrc.isNotEmpty()
                ) return@withContext siteTitleSrc
                urlString
            }
            val insertLine = "${siteTitle}\t${urlString}"
            val targetFragmentInstance = TargetFragmentInstance()
            val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
                activity,
                currentAppDirPath,
                currentFannelName,
            ) ?: return@launch
            ExecAddForListIndexAdapter.execAddForTsv(
                editFragment,
                insertLine
            )
        }
    }

    private fun makeExtraMap(
        editFragment: EditFragment,
        clickKey: String,
        buttonType: String,
    ): Map<String, String> {
        val toolbarButtonBariantForEdit = ToolbarButtonBariantForEdit.values().firstOrNull {
            it.str == buttonType
        } ?: return mapOf()
        val clickConfigMap =
            editFragment.toolbarButtonConfigMap?.get(
                toolbarButtonBariantForEdit
            )?.get(clickKey)?.let {
                CmdClickMap.createMap(
                    it,
                    "|"
                )
            }?.toMap() ?: return mapOf()
        return ExtraArgsTool.createExtraMapFromMap(
            clickConfigMap,
            "!"
        )
    }

    private fun makeParentDirPath(
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
            -> ExtraArgsTool.getParentDirPath(
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
        parentDirPath: String,
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
                    File(parentDirPath, fileName).absolutePath
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