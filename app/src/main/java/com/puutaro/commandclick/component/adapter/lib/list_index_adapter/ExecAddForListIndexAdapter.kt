package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecAddForListIndexAdapter {

    fun getInsertIndex(
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

    fun listUpdateByInsertItem(
        editFragment: EditFragment,
        addLine: String,
        insertIndex: Int,
    ){
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexAdapter =
            binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexAdapter.listIndexList.add(insertIndex, addLine)
        listIndexAdapter.notifyItemInserted(insertIndex)
        val listInsertWaitTime = 200L
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                delay(listInsertWaitTime)
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

    fun execAddForTsv(
        editFragment: EditFragment,
        insertLine: String,
    ){
        val context = editFragment.context
            ?: return
        val listIndexForEditAdapter =
            editFragment.binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val tsvPath =
            ListSettingsForListIndex.getListSettingKeyHandler(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
            )
        val tsvPathObj = File(tsvPath)
        val tsvParentDirPath = tsvPathObj.parent ?: return
        val tsvName = tsvPathObj.name
        val currentTsvConList = ReadText(
            tsvParentDirPath,
            tsvName
        ).textToList()
        if(
            currentTsvConList.contains(insertLine)
        ) {
            Toast.makeText(
                context,
                "Already exist",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val sortType = ListSettingsForListIndex.getSortType(ListIndexForEditAdapter.indexListMap)
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
        val sortType = ListSettingsForListIndex.getSortType(ListIndexForEditAdapter.indexListMap)
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
        val filterPrefix = ListSettingsForListIndex.getListSettingKeyHandler(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.PREFIX.key
        )
        val filterSuffix = ListSettingsForListIndex.getListSettingKeyHandler(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.SUFFIX.key
        )
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
        val sortType = ListSettingsForListIndex.getSortType(ListIndexForEditAdapter.indexListMap)
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