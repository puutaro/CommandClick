package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.MapListFileRenamer
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExecRenameFile {
    fun rename(
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        CoroutineScope(Dispatchers.Main).launch {
            execTsvLineFileRename(
                editFragment,
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
        editFragment: EditFragment,
        listIndexPosition: Int,
    ){
        val binding = editFragment.binding
        val listIndexForEditAdapter =
            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val selectedLineMap =
            listIndexForEditAdapter.lineMapList.getOrNull(
                listIndexPosition
            ) ?: return
        val editComponentListAdapter =
            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val mapListPath = FilePrefixGetter.get(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editComponentListAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
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
            editFragment,
            mapListPath,
            selectedLineMap
        )
    }
}