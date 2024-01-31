package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.widget.Toast
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import java.io.File

object ExecCopyFileHere {
    fun copyFileHere(
        listIndexArgsMaker: ListIndexArgsMaker,
        holder: ListIndexForEditAdapter.ListIndexListViewHolder,
        extraMapForJsPath:  Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        Toast.makeText(
            editFragment.context,
            "Copy ok",
            Toast.LENGTH_SHORT
        ).show()
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> execCopyHereForNormal(
                listIndexArgsMaker,
                holder,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> execCopyHereForTsv(
                listIndexArgsMaker,
                holder,
            )
        }
    }

    private fun execCopyHereForTsv(
        listIndexArgsMaker: ListIndexArgsMaker,
        listIndexListViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val listIndexForEditAdapter =
            editFragment.binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val addLine =
            listIndexForEditAdapter.listIndexList[listIndexListViewHolder.bindingAdapterPosition].let {
                val titleConList = it.split("\t")
                val title = titleConList.firstOrNull()?.let {
                    "${it}_${CommandClickScriptVariable.makeCopyPrefix()}"
                }
                val con = titleConList.lastOrNull()
                listOf(
                    title,
                    con,
                ).joinToString("\t")
            }
        ListIndexForEditAdapter.execAddForTsv(
            editFragment,
            addLine
        )
    }

    private fun execCopyHereForNormal(
        listIndexArgsMaker: ListIndexArgsMaker,
        holder: ListIndexForEditAdapter.ListIndexListViewHolder,
    ){
        val insertFileName = holder.fileName
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                listIndexArgsMaker.editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        val srcFilePath = File(parentDirPath, insertFileName).absolutePath
        ListIndexForEditAdapter.execCopyForFile(
            listIndexArgsMaker.editFragment,
            srcFilePath,
        )
    }
}