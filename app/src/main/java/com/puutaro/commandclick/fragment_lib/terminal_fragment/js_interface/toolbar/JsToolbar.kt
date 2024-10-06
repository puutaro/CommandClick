package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexDuplicate
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.SiteUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class JsToolbar(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun getListPath(): String? {
        val terminalFragment = terminalFragmentRef.get()
            ?: return null
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//        currentAppDirPath,
            currentFannelName,
            currentFannelState
        )
        if(
            editFragment == null
        ) return null
        val editComponentListAdapter = editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val listKeyCon = FilePrefixGetter.get(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editComponentListAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val listIndexPath = getListPath()
            ?: return
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
//        val listIndexType = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        val extraMap = makeSaveDirPathAndCompSuffixMap(
            urlConSaveParentDirPathSrc,
            compSuffix,
        )
//        val urlConSaveParentDirPath = makeUrlConSaveParentDirPath(
//            extraMap,
//            listIndexType,
//            listIndexPath
//        ) ?: return
        val fileName = when(
            title.isEmpty()
        ) {
            true -> CommandClickScriptVariable.makeRndPrefix()
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
        ListIndexDuplicate.isFileDetect(
//            urlConSaveParentDirPath,
            compFileName,
        ).let {
                isDetect ->
            if(
                isDetect
            ) return
        }
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        FileSystems.writeFile(
            File(
                cmdclickDefaultAppDirPath,
                compFileName
            ).absolutePath,
            con,
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                addAndSort(
//                    listIndexType,
                    listIndexPath,
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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )

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
                val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
                    activity,
//                    currentAppDirPath,
                    currentFannelName,
                    currentFannelState
                ) ?: return@withContext
                val editListRecyclerView =
                    editFragment.binding.editListRecyclerView
                val editComponentListAdapter =
                    editListRecyclerView.adapter as EditComponentListAdapter
                val tsvPath =
                    FilePrefixGetter.get(
                        editFragment.fannelInfoMap,
                        editFragment.setReplaceVariableMap,
                        editComponentListAdapter.indexListMap,
                        ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
                    )  ?: String()
                ListIndexDuplicate.isTsvDetect(
                    tsvPath,
                    siteTitle,
                    urlString,
                ).let {
                        isDetect ->
                    if(
                        isDetect
                    ) return@withContext
                }
                try {
                    val editContext = editFragment.context ?: return@withContext
                    ExecAddForListIndexAdapter.execAddForTsv(
                        editContext,
                        editFragment.fannelInfoMap,
                        editFragment.setReplaceVariableMap,
                        editListRecyclerView,
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

//    private fun makeUrlConSaveParentDirPath(
//        extraMap: Map<String, String>,
//        listIndexType: TypeSettingsForListIndex.ListIndexTypeKey,
//        listIndexPath: String
//    ): String? {
//        val terminalFragment = terminalFragmentRef.get()
//            ?: return null
//        val activity = terminalFragment.activity
//        val fannelInfoMap = terminalFragment.fannelInfoMap
//        val currentFannelName = FannelInfoTool.getCurrentFannelName(
//            fannelInfoMap
//        )
//        val currentFannelState = FannelInfoTool.getCurrentStateName(
//            fannelInfoMap
//        )
//        TargetFragmentInstance.getCurrentEditFragmentFromFragment(
//            activity,
////        currentAppDirPath,
//            currentFannelName,
//            currentFannelState
//        ) ?: return null
//        return when(listIndexType){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> EditSettingExtraArgsTool.getParentDirPath(
//                    extraMap,
////                    currentAppDirPath,
//                )
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> listIndexPath
//        }
//    }

    private fun addAndSort(
//        listIndexType: TypeSettingsForListIndex.ListIndexTypeKey,
        listIndexPath: String,
//        urlConSaveParentDirPath: String,
        fileName: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//        currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val editContext = editFragment.context ?: return
        val insertLine = listOf(
            fileName,
            File(UsePath.cmdclickDefaultAppDirPath, fileName).absolutePath
        ).joinToString("\t")
        ExecAddForListIndexAdapter.execAddForTsv(
            editContext,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.binding.editListRecyclerView,
            insertLine
        )
//        when(listIndexType){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> {
//                val insertLine = listOf(
//                    fileName,
//                    File(UsePath.cmdclickDefaultAppDirPath, fileName).absolutePath
//                ).joinToString("\t")
//                ExecAddForListIndexAdapter.execAddForTsv(
//                    editFragment,
//                    insertLine
//                )
//            }
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> ExecAddForListIndexAdapter.sortInAddFile(
//                editFragment,
//                File(listIndexPath, fileName).absolutePath
//            )
//        }
    }
}