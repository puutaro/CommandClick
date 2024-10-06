package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.content.Context
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.editor.EditorByIntent
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import java.io.File

object ExecWriteItem {
    fun write(
        context: Context?,
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
//        val type = ListIndexEditConfig.getListIndexType(
//            editFragment
//        )
        writeFileInTsvLine(
            context,
            editComponentListAdapter,
            listIndexPosition,
        )
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
//                listIndexPosition,
//            )
//        }

    }

//    private fun writeFile(
//        editFragment: EditFragment,
//        selectedItem: String,
//    ){
//        val context = editFragment.context
//        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexAdapter.indexListMap,
//            ListIndexAdapter.listIndexTypeKey
//        )
//        if(
//            NoFileChecker.isNoFile(
//                parentDirPath,
//                selectedItem,
//            )
//        ) return
//        val editorByIntent = EditorByIntent(
////            parentDirPath,
//            selectedItem,
//            context
//        )
//        editorByIntent.byIntent()
//    }

    private fun writeFileInTsvLine(
        context: Context?,
        editComponentListAdapter: EditComponentListAdapter,
        listIndexPosition: Int,
    ){
        if(
            context == null
        ) return
//        val context = fragment.context ?: return
//        val binding = fragment.binding
//        val listIndexForEditAdapter =
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
        val isExist = ReadText(tsvPath)
            .textToList().map {
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
            ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
        ) ?: return
        val filePathOrConObj = File(filePathOrCon)
        val isWithFileRename = filePathOrConObj.isFile
        if(!isWithFileRename) return
//        val parentDirPath = filePathOrConObj.parent ?: return
        val fileName = filePathOrConObj.name
        val editorByIntent = EditorByIntent(
//            parentDirPath,
            fileName,
            context
        )
        editorByIntent.byIntent()
    }
}