package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.MapListFileRenamer
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExecRenameFile {
    fun rename(
        fragment: Fragment,
        editListRecyclerView: RecyclerView,
        listIndexPosition: Int,
    ){
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        CoroutineScope(Dispatchers.Main).launch {
            execTsvLineFileRename(
                fragment,
                editListRecyclerView,
                listIndexPosition,
            )
//            when (type) {
////                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//                -> execFileRename(
//                    editFragment,
//                    selectedItem,
//                )
//
//                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//                -> execTsvLineFileRename(
//                    editFragment,
//                    listIndexPosition,
//                )
//            }
        }
    }

//    private fun execFileRename(
//        editFragment: EditFragment,
//        selectedItem: String,
//    ){
////        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
////            editFragment,
////            ListIndexAdapter.indexListMap,
////            ListIndexAdapter.listIndexTypeKey
////        )
//
//        if(
//            NoFileChecker.isNoFile(
////                parentDirPath,
//                selectedItem,
//            )
//        ) return
//        FileRenamer.rename(
//            editFragment,
////            parentDirPath,
//            selectedItem
//        )
//    }

    private fun execTsvLineFileRename(
        fragment: Fragment,
        editListRecyclerView: RecyclerView,
//        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
//        val binding = fragment.binding
//        val listIndexForEditAdapter =
//            blinding.editListRecycerView.adapter as EditComponentListAdapter
        val editConstraintListAdapter =
            editListRecyclerView.adapter as EditConstraintListAdapter
        val mapListSeparator = ListSettingsForEditList.MapListPathManager.mapListSeparator
        val selectedLineMap =
            editConstraintListAdapter.lineMapList.getOrNull(
                listIndexPosition
            ) ?: return
        val mapListPath = FilePrefixGetter.get(
            editConstraintListAdapter.fannelInfoMap,
            editConstraintListAdapter.setReplaceVariableMap,
            editConstraintListAdapter.indexListMap,
            ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
        ) ?: String()
        val isExist = ReadText(
            mapListPath
        ).textToList().map {
            CmdClickMap.createMap(
                it,
                mapListSeparator
            ).toMap()
        }.contains(selectedLineMap)
        if(!isExist){
            ToastUtils.showShort("No exist")
            return
        }
        MapListFileRenamer.rename(
            fragment.context,
            editListRecyclerView,
            mapListPath,
            selectedLineMap
        )
    }
}