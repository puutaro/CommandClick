package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex

object ExecCopyFileHere {
    fun copyFileHere(
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        execCopyHereForTsv(
            editFragment,
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
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
        val listIndexForEditAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val addLine =
            listIndexForEditAdapter.lineMapList[listIndexPosition].let {
                lineMap ->
//                val titleConList = it.split("\t")

                val title = lineMap.get(
                    ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
                )?.let {
                    "${it}_${CommandClickScriptVariable.makeRndPrefix()}"
                }
                val con = lineMap.get(
                    ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
                )
                listOf(
                    title,
                    con,
                ).joinToString("\t")
            }
        ExecAddForListIndexAdapter.execAddForTsv(
            editFragment,
            addLine
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