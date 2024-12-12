package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ItemPathMaker {

    fun make(
        editConstraintListAdapter: EditConstraintListAdapter,
        listIndexPosition: Int,
    ): String? {
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        val extractedPath = getCatPathForTsv(
            editConstraintListAdapter,
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
        editConstraintListAdapter: EditConstraintListAdapter,
        listIndexPosition: Int,
    ): String? {
//        val binding = fragment.binding
//        val listIndexForEditAdapter =
//            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val selectedLineMap =
            editConstraintListAdapter.lineMapList.getOrNull(
                listIndexPosition
            ) ?: return null
//        val editComponentListAdapter =
//            fragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val mapListPath = FilePrefixGetter.get(
            editConstraintListAdapter.fannelInfoMap,
            editConstraintListAdapter.setReplaceVariableMap,
            editConstraintListAdapter.indexListMap,
            ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
        ) ?: return null
        val mapListSeparator = ListSettingsForEditList.MapListPathManager.mapListSeparator
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
            ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
        )
//            titleFileNameAndPathConPair.second
        return filePathOrCon
    }
}