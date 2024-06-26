package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.EditSeparator
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddFileForEdit
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

class JsFileAdder(
    private val terminalFragment: TerminalFragment
) {

    private val activity = terminalFragment.activity
    private val readSharePreferenceMap =
        terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = SharePrefTool.getCurrentStateName(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun add(
        compFileNameMapCon: String,
        separator: String,
    ){

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return

        execAddItemForEdit(
            editFragment,
            compFileNameMapCon,
            separator,
        )
    }

    private fun execAddItemForEdit(
        editFragment: EditFragment,
        compFileNameMapCon: String,
        separator: String,
    ){
        val fileName = JsDialog(terminalFragment).prompt(
            "Type item name",
            String(),
            String()
        )
        if(
            fileName.isEmpty()
        ) return
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
                execAddForTsv(
                    editFragment,
                    fileName,
                    compFileNameMapCon,
                    separator,
                )
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> execAddForNormal(
                editFragment,
                fileName,
                compFileNameMapCon,
                separator,
            )
        }
    }

    private fun execAddForNormal(
        editFragment: EditFragment,
        fileName: String,
        compFileNameMapCon: String,
        separator: String,
    ){
        val context = editFragment.context
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        val compFileNameMap = CmdClickMap.createMap(
            compFileNameMapCon,
            separator.firstOrNull() ?: '|',
        ).toMap()
        val compFileName = EditSettingExtraArgsTool.makeCompFileName(
            editFragment,
            fileName,
            compFileNameMap,
        )
        FileSystems.writeFile(
            File(
                parentDirPath,
                compFileName
            ).absolutePath,
            String()
        )
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

    private fun execAddForTsv(
        editFragment: EditFragment,
        fileName: String,
        compFileNameMapCon: String,
        separatorSrc: String,
    ){
        val separator = separatorSrc.firstOrNull() ?: '|'
        val compFileNameMap = CmdClickMap.createMap(
            compFileNameMapCon,
            separator,
        ).toMap()
        val compFilePath = makeCompFilePath(
            editFragment,
            fileName,
            compFileNameMap,
        )
        val title = makeCompTitle(
            editFragment,
            fileName,
            compFileNameMap,
            separator,
        )
        val insertLine = "${title}\t${compFilePath}"
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "add_makeCompfileName.txt").absolutePath,
//            listOf(
//                "compFileNameMap: ${compFileNameMap}",
//                "separator: ${separator}",
//                "next_separator: ${EditSeparator.getNextSeparator(separator)}",
//                "fileName: ${fileName}",
//                "compFilePath: ${compFilePath}",
//                "title: ${title}",
//                "insertLine: ${insertLine}",
//            ).joinToString("\n\n\n")
//        )
        ExecAddForListIndexAdapter.execAddForTsv(
            editFragment,
            insertLine,
        )
    }

    private fun makeCompFilePath(
        editFragment: EditFragment,
        fileName: String,
        compFileNameMap: Map<String, String>,
    ): String {
        val dirPath = compFileNameMap.get(AddFileForEdit.AddFileExtraArgs.DIR_PATH.key)
        val compFileName = EditSettingExtraArgsTool.makeCompFileName(
            editFragment,
            fileName,
            compFileNameMap,
        )
        return when(
            dirPath.isNullOrEmpty()
        ){
            true -> compFileName
            else -> File(dirPath, compFileName).absolutePath
        }
    }

    private fun makeCompTitle(
        editFragment: EditFragment,
        fileName: String,
        compFileNameMap: Map<String, String>,
        separator: Char,
    ): String {
        val nextSeparator = EditSeparator.getNextSeparator(separator) ?: '|'
        val compTitleMap = CmdClickMap.createMap(
            compFileNameMap.get(AddFileForEdit.AddFileExtraArgs.TITLE_ARGS.key),
            nextSeparator
        ).toMap()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "add_makeCompTitle.txt").absolutePath,
//            listOf(
//                "compFileNameMap: ${compFileNameMap}",
//                "separator: ${separator}",
//                "next_separator: ${EditSeparator.getNextSeparator(separator)}",
//                "compTitleMap: ${compTitleMap}",
//                "filename: ${fileName}",
//                "compF: ${EditSettingExtraArgsTool.makeCompFileName(
//                    editFragment,
//                    fileName,
//                    compTitleMap,
//                )}"
//            ).joinToString("\n\n\n")
//        )
        return EditSettingExtraArgsTool.makeCompFileName(
            editFragment,
            fileName,
            compTitleMap,
        )
    }
}
