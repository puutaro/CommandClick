package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ExecAddForEditListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ListIndexDuplicate
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.edit.lib.EditSeparator
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddFileForEdit
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File
import java.lang.ref.WeakReference

class JsFileAdder(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun add(
        compFileNameMapCon: String,
        separator: String,
    ){
        /*
        ## Description

        Add file or tsv line by [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md##type) listIndex

        ## Corresponding macro

        -> [ADD](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add)

        ## compFileNameMapCon arg

        -> [args for add macro in toolbar macro](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add)

        ## separator arg

        separator for compFileNameMapCon

        ## Example

        ```js.js
        var=runAdd
            ?func=jsFleAdder.add
            ?args=
                &compFileNameMapCon=`
                    dirPath="${image2AsciiArtGalleryDirPath}"\n
                    titleArgs="macro=CAMEL_TO_BLANK_SNAKE?compSuffix=List"\\n
                    `
            ?separator=`\n`
        ```

        */

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
//            currentAppDirPath,
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
        val fileName = JsDialog(terminalFragmentRef).prompt(
            "Type item name",
            String(),
            String()
        ).trim()
        if(
            fileName.isEmpty()
        ) return
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        execAddForMapList(
            editFragment,
            fileName,
            compFileNameMapCon,
            separator,
        )
//        when(type){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////            -> return
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
//                execAddForTsv(
//                    editFragment,
//                    fileName,
//                    compFileNameMapCon,
//                    separator,
//                )
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> execAddForNormal(
//                editFragment,
//                fileName,
//                compFileNameMapCon,
//                separator,
//            )
//        }
    }

//    private fun execAddForNormal(
//        editFragment: EditFragment,
//        fileName: String,
//        compFileNameMapCon: String,
//        separator: String,
//    ){
//        val context = editFragment.context
////        val parentDirPath =
////            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
////                editFragment,
////                ListIndexAdapter.indexListMap,
////                ListIndexAdapter.listIndexTypeKey
////            )
//        val compFileNameMap = CmdClickMap.createMap(
//            compFileNameMapCon,
//            separator.firstOrNull() ?: '|',
//        ).toMap()
//        val compFileName = EditSettingExtraArgsTool.makeCompFileName(
//            editFragment,
//            fileName,
//            compFileNameMap,
//        )
//        ListIndexDuplicate.isFileDetect(
////            parentDirPath,
//            compFileName,
//        ).let {
//            isDetect ->
//            if(
//                isDetect
//            ) return
//        }
//        FileSystems.writeFile(
//            File(
//                parentDirPath,
//                compFileName
//            ).absolutePath,
//            String()
//        )
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.IO){
//                delay(200)
//            }
//            withContext(Dispatchers.IO) {
//                BroadcastSender.normalSend(
//                    context,
//                    BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
//                )
//            }
//        }
//    }

    private fun execAddForMapList(
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
        val title = makeCompTitle(
            editFragment,
            fileName,
            compFileNameMap,
            separator,
        )
        val compFilePath = makeCompFilePath(
            editFragment,
            fileName,
            compFileNameMap,
        )
        val editComponentListAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val tsvPath =
            FilePrefixGetter.get(
                editFragment.fannelInfoMap,
                editFragment.setReplaceVariableMap,
                editComponentListAdapter.editListMap,
                ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
            )  ?: String()
        ListIndexDuplicate.isTsvDetect(
            tsvPath,
            title,
            compFilePath
        ).let {
                isDetect ->
            if(
                isDetect
            ) return
        }
//        val insertLine = "${title}\t${compFilePath}"
        val insertLineMap = mapOf(
            ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key to title,
            ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key to compFilePath,
        )
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
        val editContext = editFragment.context ?: return
        ExecAddForEditListAdapter.execAddForEditList(
            editContext,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.binding.editListRecyclerView,
            insertLineMap,
        )
    }

    private fun makeCompFilePath(
        editFragment: EditFragment,
        fileName: String,
        compFileNameMap: Map<String, String>,
    ): String {
        val compFileName = EditSettingExtraArgsTool.makeCompFileName(
            editFragment,
            fileName,
            compFileNameMap,
        )
        val dirPath = compFileNameMap.get(AddFileForEdit.AddFileExtraArgs.DIR_PATH.key)
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
