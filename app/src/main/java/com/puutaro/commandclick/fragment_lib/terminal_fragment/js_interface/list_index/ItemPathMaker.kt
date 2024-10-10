package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ItemPathMaker {

    fun make(
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ): String? {
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        val extractedPath = getCatPathForTsv(
            editComponentListAdapter,
            listIndexPosition
        )?.trim()
//            when(type) {
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> getCatPathForNormal(
//                editFragment,
//                selectedItem
//            )
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> getCatPathForTsv(
//                editFragment,
//                listIndexPosition
//            )
//        }?.trim()
        if(
            extractedPath.isNullOrEmpty()
        ) {
            ToastUtils.showShort("Not exist: ${extractedPath}")
            return null
        }
        if(
            !File(extractedPath).isFile
        ) {
            ToastUtils.showShort("Not exist: ${extractedPath}")
            return null
        }
        return extractedPath
    }

//    private fun getCatPathForNormal(
//        editFragment: EditFragment,
//        selectedItem: String
//    ): String {
//        val parentDirPath =
//            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                editFragment,
//                ListIndexAdapter.indexListMap,
//                ListIndexAdapter.listIndexTypeKey
//            )
//        return File(parentDirPath, selectedItem).absolutePath
//    }

    private fun getCatPathForTsv(
//        fragment: Fragment,
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ): String? {
//        val binding = fragment.binding
//        val listIndexForEditAdapter =
//            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val selectedLineMap =
            editComponentListAdapter.lineMapList.getOrNull(
                listIndexPosition
            ) ?: return null
//        val editComponentListAdapter =
//            fragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val mapListPath = FilePrefixGetter.get(
            editComponentListAdapter.fannelInfoMap,
            editComponentListAdapter.setReplaceVariableMap,
            editComponentListAdapter.editListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
        ) ?: return null
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val isExist = ReadText(
            mapListPath
        ).textToList().map {
            CmdClickMap.createMap(
                it,
                mapListSeparator
            ).toMap()
        }.contains(selectedLineMap)
        if(!isExist){
            ToastUtils.showShort("No exist tsv path")
            return null
        }
//        val titleFileNameAndPathConPair =
//            TitleFileNameAndPathConPairForListIndexAdapter.get(selectedLineMap)
//                ?: return null
        val filePathOrCon = selectedLineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
        )
//            titleFileNameAndPathConPair.second
        return filePathOrCon
    }
}