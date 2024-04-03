package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexMenuLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyAppDir
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFile
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileHere
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileSimple
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyPath
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecEditCmdVal
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecEditSettingVal
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemCat
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecItemDelete
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecRenameAppDir
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecRenameFile
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecShowDescription
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleEditItem
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecWriteItem

object ExecMacroHandlerForListIndex {

    fun handle(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        selectedItem: String,
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
                val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                ExecItemDelete.execItemDelete(
                    editFragment,
                    filterDir,
                    selectedItem,
                )
            }
            JsPathMacroForListIndex.SIMPLE_DELETE -> {
                val binding = editFragment.binding
                val editListRecyclerView = binding.editListRecyclerView
                val listIndexForEditAdapter =
                    editListRecyclerView.adapter as ListIndexForEditAdapter
                ExecSimpleDelete.removeController(
                    editFragment,
                    editListRecyclerView,
                    listIndexForEditAdapter,
                    selectedItem,
                    listIndexPosition,
                )
            }
            JsPathMacroForListIndex.CAT
            -> ExecItemCat.cat(
                editFragment,
                selectedItem,
                listIndexPosition,
            )
            JsPathMacroForListIndex.COPY_PATH ->
                ExecCopyPath.copyPath(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE ->
                ExecCopyFile.copyFile(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE_HERE ->
                ExecCopyFileHere.copyFileHere(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.COPY_FILE_SIMPLE ->
                ExecCopyFileSimple.copy(
                    editFragment,
                    selectedItem,
                    jsActionMap,
                )
            JsPathMacroForListIndex.COPY_APP_DIR ->
                ExecCopyAppDir.copyAppDir(
                    editFragment,
                    selectedItem
                )
            JsPathMacroForListIndex.EDIT_C ->
                ExecEditCmdVal.edit(
                    editFragment,
                    selectedItem,
                )
            JsPathMacroForListIndex.EDIT_S ->
                ExecEditSettingVal.edit(
                    editFragment,
                    selectedItem,
                )
            JsPathMacroForListIndex.RENAME ->
                ExecRenameFile.rename(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.RENAME_APP_DIR ->
                ExecRenameAppDir.execRenameAppDir(
                    editFragment,
                    selectedItem,
                )
            JsPathMacroForListIndex.MENU ->
                    ListIndexMenuLauncher.launch(
                    editFragment,
                    jsActionMap,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.DESC ->
                ExecShowDescription.desc(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.SIMPLE_EDIT ->
                ExecSimpleEditItem.edit(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            JsPathMacroForListIndex.WRITE ->
                ExecWriteItem.write(
                    editFragment,
                    selectedItem,
                    listIndexPosition
                )
        }
    }

}