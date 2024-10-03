package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexMenuLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFile
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileHere
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileSimple
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyPath
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecEditCmdVal
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecEditSettingVal
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemCat
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecRenameFile
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecShowDescription
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleEditItem
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecWriteItem
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex

object ExecMacroHandlerForListIndex {

    fun handle(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        listIndexPosition: Int,
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val macroStr =
            jsActionMap.get(
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
            )
        val macro = JsPathMacroForListIndex.values().firstOrNull {
            it.name == macroStr
        } ?: return
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
                val binding = editFragment.binding
                val editListRecyclerView = binding.editListRecyclerView
                val listIndexForEditAdapter =
                    editListRecyclerView.adapter as EditComponentListAdapter
                ExecSimpleDelete.removeController(
                    editFragment,
                    editListRecyclerView,
                    listIndexForEditAdapter,
                    selectedItemLineMap,
                    listIndexPosition,
                )
            }
            JsPathMacroForListIndex.CAT
            -> ExecItemCat.cat(
                editFragment,
                listIndexPosition,
            )
            JsPathMacroForListIndex.COPY_PATH ->
                ExecCopyPath.copyPath(
                    editFragment,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE -> {
                val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
                    jsActionMap
                ) ?: emptyMap()
                ExecCopyFile.copyFile(
                    editFragment,
                    listIndexPosition,
                    argsMap
                )
            }
            JsPathMacroForListIndex.COPY_FILE_HERE ->
                ExecCopyFileHere.copyFileHere(
                    editFragment,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE_SIMPLE -> {
                val selectedSrcPath = selectedItemLineMap.get(
                    ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
                ) ?: return
                ExecCopyFileSimple.copy(
                    editFragment,
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
                    editFragment,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.MENU ->
                ListIndexMenuLauncher.launch(
                    editFragment,
                    jsActionMap,
                    selectedItemLineMap,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.DESC ->
                ExecShowDescription.desc(
                    editFragment,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.SIMPLE_EDIT ->
                ExecSimpleEditItem.edit(
                    editFragment,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.WRITE ->
                ExecWriteItem.write(
                    editFragment,
                    listIndexPosition
                )
        }
    }

}