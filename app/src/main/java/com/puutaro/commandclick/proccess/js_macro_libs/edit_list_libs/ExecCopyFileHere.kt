package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ExecAddForEditListAdapter

object ExecCopyFileHere {
    fun copyFileHere(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
//        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        execCopyHereForTsv(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
            listIndexPosition,
        )
        ToastUtils.showShort("Copy ok")
//        when(type){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
////            -> {}
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> execCopyHereForNormal(
//                editFragment,
//                selectedItem,
//            )
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> execCopyHereForTsv(
//                editFragment,
//                listIndexPosition,
//            )
//        }
    }

    private fun execCopyHereForTsv(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
//        listIndexForEditAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
        val editConstraintListAdapter =
            editListRecyclerView.adapter as EditConstraintListAdapter
        val addLineMap =
            editConstraintListAdapter.lineMapList[listIndexPosition]
//                .let {
//                lineMap ->
////                val titleConList = it.split("\t")
//
//                val title = lineMap.get(
//                    ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
//                )?.let {
//                    "${it}_${CommandClickScriptVariable.makeRndPrefix()}"
//                }
//                val con = lineMap.get(
//                    ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
//                )
//                listOf(
//                    title,
//                    con,
//                ).joinToString("\t")
//            }
        ExecAddForEditListAdapter.execAddForEditList(
            fragment.context,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
            addLineMap
        )
    }

//    private fun execCopyHereForNormal(
//        editFragment: EditFragment,
//        selectedItem: String,
//    ){
//        val parentDirPath =
//            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                editFragment,
//                ListIndexAdapter.indexListMap,
//                ListIndexAdapter.listIndexTypeKey
//            )
//        val srcFilePath = File(parentDirPath, selectedItem).absolutePath
//        ExecAddForListIndexAdapter.execAddByCopyFileHere(
//            editFragment,
//            srcFilePath,
//        )
//    }
}