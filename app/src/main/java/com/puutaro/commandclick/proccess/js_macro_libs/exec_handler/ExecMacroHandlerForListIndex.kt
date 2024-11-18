package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.edit_list.libs.ListIndexMenuLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecCopyFile
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecCopyFileHere
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecCopyFileSimple
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecCopyPath
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecItemCat
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecRenameFile
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecShowDescription
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecSimpleDelete
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecSimpleEditItem
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecWriteItem
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object ExecMacroHandlerForListIndex {

    fun handle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        listIndexPosition: Int,
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val context = fragment.context
            ?: return
        val macroStr =
            jsActionMap.get(
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
            )
        val macro = JsPathMacroForListIndex.values().firstOrNull {
            it.name == macroStr
        } ?: return
        val editComponentListAdapter =
            editListRecyclerView?.adapter as EditComponentListAdapter
        when(macro){
            JsPathMacroForListIndex.DELETE -> {
//                val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                    editFragment,
//                    ListIndexAdapter.indexListMap,
//                    ListIndexAdapter.listIndexTypeKey
//                )
//                ExecItemDelete.execItemDelete(
//                    editFragment,
////                    filterDir,
//                    selectedItem,
//                    listIndexPosition,
//                )
            }
            JsPathMacroForListIndex.SIMPLE_DELETE -> {
//                val binding = fragment.binding
//                val editListRecyclerView = binding.editListRecyclerView
                ExecSimpleDelete.removeController(
                    fragment,
                    editListRecyclerView,
                    editComponentListAdapter,
                    selectedItemLineMap,
                    listIndexPosition,
                )
            }
            JsPathMacroForListIndex.CAT
            -> ExecItemCat.cat(
                context,
                editComponentListAdapter,
                listIndexPosition,
            )
            JsPathMacroForListIndex.COPY_PATH ->
                ExecCopyPath.copyPath(
                    context,
                    editComponentListAdapter,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE -> {
                val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
                    jsActionMap
                ) ?: emptyMap()
                ExecCopyFile.copyFile(
                    fragment,
                    fannelInfoMap,
                    listIndexPosition,
                    argsMap
                )
            }
            JsPathMacroForListIndex.COPY_FILE_HERE ->
                ExecCopyFileHere.copyFileHere(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    editListRecyclerView,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE_SIMPLE -> {
                val selectedSrcPath = selectedItemLineMap.get(
                    ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
                ) ?: return
                ExecCopyFileSimple.copy(
                    fragment,
                    selectedSrcPath,
                    jsActionMap,
                )
            }
//            JsPathMacroForListIndex.COPY_APP_DIR ->
//                ExecCopyAppDir.copyAppDir(
//                    editFragment,
//                    selectedItem
//                )
//            JsPathMacroForListIndex.EDIT_C ->
//                ExecEditCmdVal.edit(
//                    editFragment,
//                    selectedItemLineMap,
//                )
//            JsPathMacroForListIndex.EDIT_S ->
//                ExecEditSettingVal.edit(
//                    editFragment,
//                    selectedItemLineMap,
//                )
            JsPathMacroForListIndex.RENAME ->
                ExecRenameFile.rename(
                    fragment,
                    editListRecyclerView,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.MENU ->
                ListIndexMenuLauncher.launch(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    editListRecyclerView,
                    jsActionMap,
                    selectedItemLineMap,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.DESC ->
                ExecShowDescription.desc(
                    fragment,
                    editComponentListAdapter,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.SIMPLE_EDIT ->
                ExecSimpleEditItem.edit(
                    editComponentListAdapter,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.WRITE ->
                ExecWriteItem.write(
                    fragment.context,
                    editComponentListAdapter,
                    listIndexPosition
                )
        }
    }

}