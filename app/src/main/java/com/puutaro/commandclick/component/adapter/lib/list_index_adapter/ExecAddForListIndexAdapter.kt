package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecAddForListIndexAdapter {

    private fun getInsertIndex(
        sortType: ListSettingsForListIndex.SortByKey,
        listIndexForEditAdapter: ListIndexForEditAdapter,
        addLine: String,
    ): Int {
        val virtualListIndexList = listIndexForEditAdapter.listIndexList + listOf(addLine)
        return ListSettingsForListIndex.ListIndexListMaker.sortList(
            sortType,
            virtualListIndexList,
        ).indexOf(addLine)
    }

    private fun listUpdateByInsertItem(
        editFragment: EditFragment,
        addLine: String,
        insertIndex: Int,
    ){
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexAdapter =
            binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexAdapter.listIndexList.add(insertIndex, addLine)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                listIndexAdapter.notifyItemInserted(insertIndex)
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

    fun execAddByCopyFileHere(
        editFragment: EditFragment,
        sourceFilePath: String,
    ){
        val context = editFragment.context ?: return
        val indexListMap = ListIndexForEditAdapter.indexListMap
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        val sourceFilePathObj = File(sourceFilePath)
        val srcParentDirPath = sourceFilePathObj.parent
            ?: return
        val srcFileName = sourceFilePathObj.name
        if(
            NoFileChecker.isNoFile(
                context,
                srcParentDirPath,
                srcFileName,
            )
        ) return
        val destiFilePath = "${parentDirPath}/${srcFileName}"
        val insertFilePath = FileSystems.execCopyFileWithDir(
            File(sourceFilePath),
            File(destiFilePath),
        )
        sortInAddFile(
            editFragment,
            insertFilePath,
        )
    }

    fun execAddByCopyFileListHere(
        editFragment: EditFragment,
        sourceFilePathList: List<String>,
    ){
        val indexListMap = ListIndexForEditAdapter.indexListMap
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        sourceFilePathList.forEach {
            sourceFilePath ->
            val sourceFilePathObj = File(sourceFilePath)
            if(
                !sourceFilePathObj.isFile
            ) return@forEach
            val srcFileName = sourceFilePathObj.name
            val destiFilePath = "${parentDirPath}/${srcFileName}"
            FileSystems.execCopyFileWithDir(
                File(sourceFilePath),
                File(destiFilePath),
            )
        }
        BroadcastSender.normalSend(
            editFragment.context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

    fun execAddByCopyDirListHere(
        editFragment: EditFragment,
        sourceFilePathList: List<String>,
    ){
        val indexListMap = ListIndexForEditAdapter.indexListMap
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        sourceFilePathList.forEach {
                sourceFilePath ->
            val sourceFilePathObj = File(sourceFilePath)
            if(
                !sourceFilePathObj.isDirectory
            ) return@forEach
            val srcDirName = sourceFilePathObj.name
            val destiDirPath = "${parentDirPath}/${srcDirName}"
            FileSystems.copyDirectory(
                sourceFilePath,
                destiDirPath,
            )
        }
        BroadcastSender.normalSend(
            editFragment.context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

    fun execAddListForTsv(
        editFragment: EditFragment,
        insertLineListSrc: List<String>
    ){
        val context = editFragment.context
        val tsvPath =
            FilePrefixGetter.get(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
            )  ?: String()
        if(
            tsvPath.trim().isEmpty()
        ) {
            Toast.makeText(
                context,
                "Retry unexpected err",
                Toast.LENGTH_SHORT
            ).show()
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


    fun execAddForTsv(
        editFragment: EditFragment,
        insertLine: String,
    ){
        val context = editFragment.context
            ?: return
        val listIndexForEditAdapter =
            editFragment.binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val tsvPath =
            FilePrefixGetter.get(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
            )  ?: String()
        val currentTsvConList = ReadText(
            tsvPath
        ).textToList()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "getFile.txt").absolutePath,
//            listOf(
//                "indexListMap ${ListIndexForEditAdapter.indexListMap}",
//                "tsvPath: ${tsvPath}",
//                "currentTsvConList: ${currentTsvConList}",
//            ).joinToString("\n\n\n")
//        )
        if(
            tsvPath.trim().isEmpty()
        ) {
            Toast.makeText(
                context,
                "Retry unexpected err",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if(
            tsvPath.trim().isEmpty()
            || currentTsvConList.contains(insertLine)
        ) {
            Toast.makeText(
                context,
                "Already exist",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val sortType = ListSettingsForListIndex.getSortType(
            editFragment,
            ListIndexForEditAdapter.indexListMap
        )
        val insertIndex = getInsertIndex(
            sortType,
            listIndexForEditAdapter,
            insertLine,
        )

        TsvTool.insertByLastUpdate(
            tsvPath,
            insertLine
        )
        when(sortType){
            ListSettingsForListIndex.SortByKey.LAST_UPDATE ->
                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                    editFragment,
                    ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                        editFragment,
                        ListIndexForEditAdapter.indexListMap,
                        ListIndexForEditAdapter.listIndexTypeKey
                    )
                )
            ListSettingsForListIndex.SortByKey.SORT,
            ListSettingsForListIndex.SortByKey.REVERSE ->
                listUpdateByInsertItem(
                    editFragment,
                    insertLine,
                    insertIndex
                )
        }
    }

    fun sortInAddFile(
        editFragment: EditFragment,
        insertFilePath: String,
    ){
        val sortType = ListSettingsForListIndex.getSortType(
            editFragment,
            ListIndexForEditAdapter.indexListMap
        )
        when(sortType){
            ListSettingsForListIndex.SortByKey.LAST_UPDATE ->
                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                    editFragment,
                    ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                        editFragment,
                        ListIndexForEditAdapter.indexListMap,
                        ListIndexForEditAdapter.listIndexTypeKey
                    )
                )
            ListSettingsForListIndex.SortByKey.SORT,
            ListSettingsForListIndex.SortByKey.REVERSE -> {
                addFileNameLineForSort(
                    editFragment,
                    insertFilePath,
                )
            }
        }
    }

    private fun addFileNameLineForSort(
        editFragment: EditFragment,
        insertFilePath: String,
    ){
        val indexListMap = ListIndexForEditAdapter.indexListMap
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        val filterPrefix = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.PREFIX.key
        ) ?: String()
        val filterSuffix = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.SUFFIX.key
        ) ?: String()
        val filterShellCon = ListSettingsForListIndex.ListIndexListMaker.getFilterShellCon(
            editFragment,
            indexListMap,
        )
        val insertFileName = File(insertFilePath)
        val fileNameElement = ListSettingsForListIndex.ListIndexListMaker.makeFileListElement(
            listOf(insertFileName.name),
            editFragment.busyboxExecutor,
            parentDirPath,
            filterPrefix,
            filterSuffix,
            filterShellCon,
        ).firstOrNull()
        if(
            fileNameElement.isNullOrEmpty()
        ) return
        val listIndexForEditAdapter =
            editFragment.binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val sortType = ListSettingsForListIndex.getSortType(
            editFragment,
            ListIndexForEditAdapter.indexListMap
        )
        val insertIndex = getInsertIndex(
            sortType,
            listIndexForEditAdapter,
            fileNameElement,
        )
        listUpdateByInsertItem(
            editFragment,
            fileNameElement,
            insertIndex
        )
    }
}