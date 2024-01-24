package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.list_index_for_edit.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecCopyAppDir
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecCopyFile
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecCopyFileHere
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecCopyPath
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecEditCmdVal
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecEditSettingVal
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecItemCatForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecItemDelete
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecRenameAppDir
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecRenameFile
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecShowDescription
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecWriteItem
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.editor.EditorByEditText
import java.io.File

object JsPathHandlerForListIndex {

    fun handle(
        listIndexArgsMaker: ListIndexArgsMaker,
        extraMapForJsPath:  Map<String, String>?,
        jsPathMacroStr: String,
        selectedItem: String,
    ){
        val filterdJsPath = JsPathMacroForListIndex.values().filter {
            it.name == jsPathMacroStr
        }.firstOrNull()
        when (filterdJsPath != null) {
            true -> jsPathMacroHandler(
                listIndexArgsMaker,
                filterdJsPath,
                extraMapForJsPath,
                selectedItem,
            )

            else -> execJs(
                listIndexArgsMaker,
                jsPathMacroStr,
                extraMapForJsPath,
                selectedItem
            )
        }
    }

    private fun jsPathMacroHandler(
        listIndexArgsMaker: ListIndexArgsMaker,
        jsPathMacroForListIndex:  JsPathMacroForListIndex,
        extraMapForJsPath: Map<String, String>?,
        selectedItem: String,
    ){
        when(jsPathMacroForListIndex){
            JsPathMacroForListIndex.DELETE -> {
                ExecItemDelete.execItemDelete(
                    listIndexArgsMaker,
                    selectedItem,
                    extraMapForJsPath,
                )
            }
            JsPathMacroForListIndex.CAT
            -> ExecItemCatForListIndex.cat(
                listIndexArgsMaker,
                selectedItem,
                extraMapForJsPath,
            )
            JsPathMacroForListIndex.COPY_PATH ->
                ExecCopyPath.copyPath(
                    listIndexArgsMaker,
                    selectedItem,
                    extraMapForJsPath,
                )
            JsPathMacroForListIndex.COPY_FILE -> {
                ExecCopyFile.copyFile(
                    listIndexArgsMaker,
                    selectedItem,
                    extraMapForJsPath,
                )
            }
            JsPathMacroForListIndex.COPY_FILE_HERE ->
                ExecCopyFileHere.copyFileHere(
                    listIndexArgsMaker,
                    selectedItem,
                    extraMapForJsPath,
                )
            JsPathMacroForListIndex.COPY_APP_DIR ->
                ExecCopyAppDir.copyAppDir(
                    listIndexArgsMaker,
                    selectedItem
                )
            JsPathMacroForListIndex.EDIT_C ->
                ExecEditCmdVal.edit(
                    listIndexArgsMaker,
                    selectedItem,
                )
            JsPathMacroForListIndex.EDIT_S ->
                ExecEditSettingVal.edit(
                    listIndexArgsMaker,
                    selectedItem,
                )
            JsPathMacroForListIndex.RENAME ->
                ExecRenameFile.rename(
                    listIndexArgsMaker,
                    selectedItem,
                )
            JsPathMacroForListIndex.RENAME_APP_DIR ->
                ExecRenameAppDir.execRenameAppDir(
                    listIndexArgsMaker,
                    selectedItem,
                )
            JsPathMacroForListIndex.MENU -> {
                ListIndexMenuLauncher.launch(
                    listIndexArgsMaker,
                    selectedItem,
                )
            }
            JsPathMacroForListIndex.DESC ->
                ExecShowDescription.desc(
                    listIndexArgsMaker,
                    selectedItem,
                )
            JsPathMacroForListIndex.SIMPLE_EDIT ->
                EditorByEditText.byEditText(
                    listIndexArgsMaker.editFragment,
                    ListIndexForEditAdapter.filterDir,
                    selectedItem,
                    ReadText(
                        ListIndexForEditAdapter.filterDir,
                        selectedItem
                    ).readText()
                )
            JsPathMacroForListIndex.WRITE ->
                ExecWriteItem.write(
                    listIndexArgsMaker,
                    selectedItem,
                    extraMapForJsPath,
                )
        }
    }

    private fun execJs(
        listIndexArgsMaker: ListIndexArgsMaker,
        jsPath: String,
        extraMap:  Map<String, String>?,
        selectedItem: String,
    ){
        if(
            jsPath.isEmpty()
            || !File(jsPath).isFile
        ) return
        val execJsCon = makeJsCon(
            listIndexArgsMaker,
            jsPath,
            extraMap,
            selectedItem,
        )
        if(
            execJsCon.isEmpty()
        ) return
        ExecJsLoad.jsUrlLaunchHandler(
            listIndexArgsMaker.editFragment,
            execJsCon
        )
    }

    private fun makeJsCon(
        listIndexArgsMaker: ListIndexArgsMaker,
        jsPath: String,
        extraMap:  Map<String, String>?,
        selectedItem: String,
    ): String {
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
        return JavaScriptLoadUrl.make(
            context,
            jsPath,
            setReplaceVariableMapSrc = editFragment.setReplaceVariableMap
        )?.let {
                jsConSrc ->
            var jsCon = jsConSrc.replace(
                "\${ITEM_NAME}",
                selectedItem,
            ).replace(
                "\${INDEX_LIST_DIR_PATH}",
                ListIndexForEditAdapter.filterDir,
            )
            extraMap?.forEach {
                jsCon = jsCon.replace(
                    "\${${it.key}}",
                    it.value
                )
            }
            jsCon
        } ?: String()
    }

}