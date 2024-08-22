package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import java.io.File

object ExecCopyFileHere {
    fun copyFileHere(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        ToastUtils.showShort("Copy ok")
        when(type){
//            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
//            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> execCopyHereForNormal(
                editFragment,
                selectedItem,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> execCopyHereForTsv(
                editFragment,
                listIndexPosition,
            )
        }
    }

    private fun execCopyHereForTsv(
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
        val listIndexForEditAdapter =
            editFragment.binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val addLine =
            listIndexForEditAdapter.listIndexList[listIndexPosition].let {
                val titleConList = it.split("\t")
                val title = titleConList.firstOrNull()?.let {
                    "${it}_${CommandClickScriptVariable.makeRndPrefix()}"
                }
                val con = titleConList.lastOrNull()
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

    private fun execCopyHereForNormal(
        editFragment: EditFragment,
        selectedItem: String,
    ){
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        val srcFilePath = File(parentDirPath, selectedItem).absolutePath
        ExecAddForListIndexAdapter.execAddByCopyFileHere(
            editFragment,
            srcFilePath,
        )
    }
}