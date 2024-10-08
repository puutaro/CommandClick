package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecSimpleEditItem {

    fun edit(
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
        writeFileInTsvLine(
            editComponentListAdapter,
            listIndexPosition
        )
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
//        when(type){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//            -> writeFile(
//                editFragment,
//                selectedItem,
//            )
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//            -> writeFileInTsvLine(
//                editFragment,
//                listIndexPosition
//            )
//        }

    }

    private fun writeFile(
        editFragment: EditFragment,
        selectedItem: String,
    ){
//        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexAdapter.indexListMap,
//            ListIndexAdapter.listIndexTypeKey
//        )
        if(
            NoFileChecker.isNoFile(
//                parentDirPath,
                selectedItem,
            )
        ) return
        val listIndexUpdateIntent = Intent()
        listIndexUpdateIntent.action = BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
//        execEdit(
//            editFragment,
////            parentDirPath,
//            selectedItem,
//            listIndexUpdateIntent
//        )
    }

    private fun writeFileInTsvLine(
//        fragment: Fragment,
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
//        val binding = fragment.binding
//        val editComponentListAdapter =
//            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val selectedLineMap =
            editComponentListAdapter.lineMapList.getOrNull(
                listIndexPosition
            ) ?: return
//        val editComponentListAdapter =
//            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val tsvPath = FilePrefixGetter.get(
            editComponentListAdapter.fannelInfoMap,
            editComponentListAdapter.setReplaceVariableMap,
            editComponentListAdapter.indexListMap,
            ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
        ) ?: return
        val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
        val isExist = ReadText(
            tsvPath
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
//        val titleFileNameAndPathConPair =
//            TitleFileNameAndPathConPairForListIndexAdapter.get(selectedLineMap)
//                ?: return
        val filePathOrCon = selectedLineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
        ) ?: String()
        val filePathOrConObj = File(filePathOrCon)
        val isWithFileRename = filePathOrConObj.isFile
        if(!isWithFileRename) return
        val parentDirPath = filePathOrConObj.parent ?: return
        val fileName = filePathOrConObj.name
        val listIndexUpdateIntent = Intent()
        listIndexUpdateIntent.action = BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
//        execEdit(
//            editFragment,
//            parentDirPath,
//            fileName,
//            null
//        )
    }

//    private fun execEdit(
//        editFragment: EditFragment,
////        parentDirPath: String,
//        selectedItem: String,
//        listIndexUpdateIntent: Intent?
//    ){
//        val firstCon = ReadText(
//            File(
//                parentDirPath,
//                selectedItem
//            ).absolutePath
//        ).readText()
//        EditorByEditText.byEditText(
//            editFragment,
////            parentDirPath,
//            selectedItem,
//            firstCon,
//            listIndexUpdateIntent
//        )
//    }
}