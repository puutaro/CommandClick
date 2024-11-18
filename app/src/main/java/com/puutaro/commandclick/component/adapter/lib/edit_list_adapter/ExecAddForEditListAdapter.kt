package com.puutaro.commandclick.component.adapter.lib.edit_list_adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecAddForEditListAdapter {

    private fun getInsertIndex(
        sortType: ListSettingsForEditList.SortByKey,
        listIndexForEditAdapter: EditComponentListAdapter,
        addLineMap: Map<String, String>,
    ): Int {
//        val addLineMap = CmdClickMap.createMap(
//            addLine,
//            ListSettingsForListIndex.MapListPathManager.mapListSeparator
//        ).toMap()
        val virtualListIndexList =
            listIndexForEditAdapter.lineMapList +
                    listOf(addLineMap)
//        val isReverseLayout = ListSettingsForListIndex.howReverseLayout(
//            listIndexForEditAdapter.fannelInfoMap,
//            listIndexForEditAdapter.setReplaceVariablesMap,
//            listIndexForEditAdapter.indexListMap
//        )
        return ListSettingsForEditList.EditListMaker.sortList(
            sortType,
            virtualListIndexList,
//            isReverseLayout
        ).indexOf(addLineMap)
    }

    private fun listUpdateByInsertItem(
//        editFragment: EditFragment,
        editListRecyclerView: RecyclerView,
//        editComponentListAdapter: EditComponentListAdapter,
        addLineMap: Map<String, String>,
        insertIndex: Int,
    ){
//        val binding = editFragment.binding
//        val editListRecyclerView = binding.editListRecyclerView
        val editComponentListAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
//        val addLineMap = CmdClickMap.createMap(
//            addLineMap,
//            ListSettingsForListIndex.MapListPathManager.mapListSeparator
//        ).toMap()
        editComponentListAdapter.lineMapList.add(insertIndex, addLineMap)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                editComponentListAdapter.notifyItemInserted(insertIndex)
            }
            withContext(Dispatchers.IO) {
                val listInsertWaitTime = 200L
                delay(listInsertWaitTime)
            }
            withContext(Dispatchers.Main){
                editListRecyclerView.layoutManager?.scrollToPosition(
                    insertIndex
                )
            }
        }
    }

//    fun execAddByCopyFileHere(
//        editFragment: EditFragment,
//        sourceFilePath: String,
//    ){
//        val indexListMap = ListIndexAdapter.indexListMap
////        val parentDirPath =
////            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
////                editFragment,
////                indexListMap,
////                ListIndexAdapter.listIndexTypeKey
////            )
//        val sourceFilePathObj = File(sourceFilePath)
//        val srcParentDirPath = sourceFilePathObj.parent
//            ?: return
//        val srcFileName = sourceFilePathObj.name
//        if(
//            NoFileChecker.isNoFile(
//                srcParentDirPath,
//                srcFileName,
//            )
//        ) return
//        val destiFilePath = "${parentDirPath}/${srcFileName}"
//        val insertFilePath = FileSystems.execCopyFileWithDir(
//            File(sourceFilePath),
//            File(destiFilePath),
//        )
//        sortInAddFile(
//            editFragment,
//            insertFilePath,
//        )
//    }

//    fun execAddByCopyFileListHere(
//        editFragment: EditFragment,
//        sourceFilePathList: List<String>,
//    ){
//        val indexListMap = ListIndexAdapter.indexListMap
////        val parentDirPath =
////            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
////                editFragment,
////                indexListMap,
////                ListIndexAdapter.listIndexTypeKey
////            )
//        sourceFilePathList.forEach {
//            sourceFilePath ->
//            val sourceFilePathObj = File(sourceFilePath)
//            if(
//                !sourceFilePathObj.isFile
//            ) return@forEach
//            val srcFileName = sourceFilePathObj.name
//            val destiFilePath = "${parentDirPath}/${srcFileName}"
//            FileSystems.execCopyFileWithDir(
//                File(sourceFilePath),
//                File(destiFilePath),
//            )
//        }
//        BroadcastSender.normalSend(
//            editFragment.context,
//            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
//        )
//    }

//    fun execAddByCopyDirListHere(
//        editFragment: EditFragment,
//        sourceFilePathList: List<String>,
//    ){
//        val indexListMap = ListIndexAdapter.indexListMap
//        val parentDirPath =
//            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                editFragment,
//                indexListMap,
//                ListIndexAdapter.listIndexTypeKey
//            )
//        sourceFilePathList.forEach {
//                sourceFilePath ->
//            val sourceFilePathObj = File(sourceFilePath)
//            if(
//                !sourceFilePathObj.isDirectory
//            ) return@forEach
//            val srcDirName = sourceFilePathObj.name
//            val destiDirPath = "${parentDirPath}/${srcDirName}"
//            FileSystems.copyDirectory(
//                sourceFilePath,
//                destiDirPath,
//            )
//        }
//        BroadcastSender.normalSend(
//            editFragment.context,
//            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
//        )
//    }

    fun execAddListForTsv(
        editFragment: EditFragment,
        insertLineListSrc: List<String>
    ){
        val editComponentAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val tsvPath =
            FilePrefixGetter.get(
                editFragment.fannelInfoMap,
                editFragment.setReplaceVariableMap,
                editComponentAdapter.editListMap,
                ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
            )  ?: String()
        if(
            tsvPath.trim().isEmpty()
        ) {
            ToastUtils.showShort("Retry unexpected err")
            return
        }
        val currentTsvConList = ReadText(
            tsvPath
        ).textToList()
        val insertLineList = insertLineListSrc.filter {
            !currentTsvConList.contains(it)
        }
        val updateTsvConList = insertLineList + currentTsvConList
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "getfileList.txt").absolutePath,
//            listOf(
//                "indexListMap ${ListIndexForEditAdapter.indexListMap}",
//                "insertLineListSrc: ${insertLineListSrc}",
//                "tsvPath: ${tsvPath}",
//                "insertLineList: ${insertLineList}",
//                "updateTsvConList: ${updateTsvConList}",
//            ).joinToString("\n\n\n")
//        )
        TsvTool.updateTsv(
            tsvPath,
            updateTsvConList
        )
    }


    fun execAddForEditList(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editListRecyclerView: RecyclerView,
//        editComponentListAdapter: EditComponentListAdapter,
        insertLineMap: Map<String, String>,
    ){
//        val context = editFragment.context
        val editComponentListAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
        val mapListPath =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                editComponentListAdapter.editListMap,
                ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
            )  ?: String()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "getFile.txt").absolutePath,
//            listOf(
//                "indexListMap ${ListIndexForEditAdapter.indexListMap}",
//                "tsvPath: ${tsvPath}",
//                "currentTsvConList: ${currentTsvConList}",
//            ).joinToString("\n\n\n")
//        )
        if(
            mapListPath.trim().isEmpty()
        ) {
            ToastUtils.showShort("Retry unexpected err")
            return
        }
        if(
            mapListPath.trim().isEmpty()
        ) {
            ToastUtils.showShort("Already exist")
            return
        }
//        val title = insertLineMap.get(
//            ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
//        ) ?: String()
//        val con = insertLineMap.get(
//            ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
//        ) ?: String()
        editComponentListAdapter.lineMapList.contains(insertLineMap).let {
                isDuplidate ->
            if(
                isDuplidate
            ) return
        }
//        ListIndexDuplicate.isTsvDetect(
//            mapListPath,
//            title,
//            con
//        ).let {
//                isDetect ->
//            if(
//                isDetect
//            ) return
//        }
//        val editComponentAdapter =
//            editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        val sortType = ListSettingsForEditList.getSortType(
            editComponentListAdapter.fannelInfoMap,
            editComponentListAdapter.setReplaceVariableMap,
            editComponentListAdapter.editListMap
        )
        val insertIndex = getInsertIndex(
            sortType,
            editComponentListAdapter,
            insertLineMap,
        )
        MapListFileTool.insertByLastUpdate(
            mapListPath,
            insertLineMap,
        )
//        TsvTool.insertByLastUpdate(
//            mapListPath,
//            insertLine
//        )
        when(sortType){
            ListSettingsForEditList.SortByKey.LAST_UPDATE ->
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(200)
                    }
                    withContext(Dispatchers.IO) {
                        BroadcastSender.normalSend(
                            context,
                            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
                        )
                    }
                }
            ListSettingsForEditList.SortByKey.SORT,
            ListSettingsForEditList.SortByKey.REVERSE ->
                listUpdateByInsertItem(
                    editListRecyclerView,
                    insertLineMap,
                    insertIndex
                )
        }
    }

//    fun sortInAddFile(
//        editFragment: EditFragment,
//        insertFilePath: String,
//    ){
//        val sortType = ListSettingsForListIndex.getSortType(
//            editFragment,
//            ListIndexAdapter.indexListMap
//        )
//        when(sortType){
//            ListSettingsForListIndex.SortByKey.LAST_UPDATE ->
//                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
//                    editFragment,
//                    ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
//                        editFragment,
//                        ListIndexAdapter.indexListMap,
//                        ListIndexAdapter.listIndexTypeKey
//                    )
//                )
//            ListSettingsForListIndex.SortByKey.SORT,
//            ListSettingsForListIndex.SortByKey.REVERSE -> {
//                addFileNameLineForSort(
//                    editFragment,
//                    insertFilePath,
//                )
//            }
//        }
//    }

//    private fun addFileNameLineForSort(
//        editFragment: EditFragment,
//        insertFilePath: String,
//    ){
//        val indexListMap = ListIndexAdapter.indexListMap
//        val parentDirPath =
//            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                editFragment,
//                indexListMap,
//                ListIndexAdapter.listIndexTypeKey
//            )
//        val filterPrefix = FilePrefixGetter.get(
//            editFragment,
//            indexListMap,
//            ListSettingsForListIndex.ListSettingKey.PREFIX.key
//        ) ?: String()
//        val filterSuffix = FilePrefixGetter.get(
//            editFragment,
//            indexListMap,
//            ListSettingsForListIndex.ListSettingKey.SUFFIX.key
//        ) ?: String()
//        val filterShellCon = ListSettingsForListIndex.ListIndexListMaker.getFilterShellCon(
//            editFragment,
//            indexListMap,
//        )
//        val insertFileName = File(insertFilePath)
//        val fileNameElement = ListSettingsForListIndex.ListIndexListMaker.makeFileListElement(
//            listOf(insertFileName.name),
//            editFragment.busyboxExecutor,
//            parentDirPath,
//            filterPrefix,
//            filterSuffix,
//            filterShellCon,
//        ).firstOrNull()
//        if(
//            fileNameElement.isNullOrEmpty()
//        ) return
//        val listIndexForEditAdapter =
//            editFragment.binding.editListRecyclerView.adapter as ListIndexAdapter
//        val sortType = ListSettingsForListIndex.getSortType(
//            editFragment,
//            ListIndexAdapter.indexListMap
//        )
//        val insertIndex = getInsertIndex(
//            sortType,
//            listIndexForEditAdapter,
//            fileNameElement,
//        )
//        listUpdateByInsertItem(
//            editFragment,
//            fileNameElement,
//            insertIndex
//        )
//    }
}