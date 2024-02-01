package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.PerformSettingForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrModeSettingKeysForQrDialog
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ListIndexForEditAdapter(
    private val editFragment: EditFragment,
    var listIndexList: MutableList<String>,
): RecyclerView.Adapter<ListIndexForEditAdapter.ListIndexListViewHolder>()
{
    private val readSharePreffernceMap = editFragment.readSharePreferenceMap
    private val context = editFragment.context
    private val activity = editFragment.activity
    private val maxTakeSize = 150
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val busyboxExecutor = BusyboxExecutor(
        context,
        UbuntuFiles(context as Context)
    )

    val qrDialogConfigMap =
        editFragment.qrDialogConfig ?: mapOf()

    val qrLogoConfigMap = QrDialogConfig.makeLogoConfigMap(
        qrDialogConfigMap
    )
    private val listIndexConfigMap = editFragment.listIndexConfigMap
    private var performMap: Map<String, String> = mapOf()

    val checkItemConfigMap = CmdClickMap.createMap(
        listIndexConfigMap?.get(
            ListIndexEditConfig.ListIndexConfigKey.CHECK_ITEM.key
        ),
        "|"
    ).toMap()
    private var recentAppDirPath = String()
    private var filterDir = String()
    private var filterPrefix = String()
    private var filterSuffix = String()


    init{
        setListProperty()
    }

    companion object {
        val className = this::class.java.name
        var indexListMap: Map<String, String> = mapOf()
        var onDeleteConFile = true
        var listIndexTypeKey = TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
        private var listIndexScrollToBottomJob: Job? = null

        fun clickUpdateFileList(
            editFragment: EditFragment,
            selectedItem: String,
        ){
            val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                indexListMap,
                listIndexTypeKey
            )
            FileSystems.updateLastModified(
                filterDir,
                selectedItem
            )
            listIndexListUpdateFileList(
                editFragment,
                ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                    editFragment,
                    indexListMap,
                    listIndexTypeKey
                )
            )
        }

        fun execCopyForFile(
            editFragment: EditFragment,
            sourceFilePath: String,
        ){
            val context = editFragment.context ?: return
            val indexListMap = ListIndexForEditAdapter.indexListMap
            val parentDirPath =
                ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    editFragment,
                    indexListMap,
                    listIndexTypeKey
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
            if(
                listIndexForEditAdapter.listIndexList.contains(insertLine)
            ) {
                Toast.makeText(
                    context,
                    "Already exist",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val sortType = ListSettingsForListIndex.getSortType(indexListMap)
            val insertIndex = getInsertIndex(
                sortType,
                listIndexForEditAdapter,
                insertLine,
            )
            val tsvPath =
                ListSettingsForListIndex.getListSettingKeyHandler(
                    editFragment,
                    indexListMap,
                    ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
                )
            TsvTool.insertByLastUpdate(
                tsvPath,
                insertLine
            )
            when(sortType){
                ListSettingsForListIndex.SortByKey.LAST_UPDATE ->
                    listIndexListUpdateFileList(
                        editFragment,
                        ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                            editFragment,
                            indexListMap,
                            listIndexTypeKey
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

        fun updateTsv(
            editFragment: EditFragment,
            listIndexList: MutableList<String>,
        ){
            val tsvPath = ListSettingsForListIndex.getListSettingKeyHandler(
                editFragment,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
            )
            if(
                tsvPath.isEmpty()
            ) return
            val sortType = ListSettingsForListIndex.getSortType(indexListMap)
            val sortListIndexListForTsvSave =
                ListSettingsForListIndex.ListIndexListMaker.sortListForTsvSave(
                    sortType,
                    listIndexList
                )
            TsvTool.updateTsv(
                tsvPath,
                sortListIndexListForTsvSave,
            )
        }

        fun updateTsvByRemove(
            editFragment: EditFragment,
            removeItemLineList: List<String>,
        ){
            val tsvPath = ListSettingsForListIndex.getListSettingKeyHandler(
                editFragment,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.LIST_DIR.key,
            )
            if(
                tsvPath.isEmpty()
            ) return
//            val sortType = ListSettingsForListIndex.getSortType(indexListMap)
//            val sortListIndexListForTsvSave =
//                ListSettingsForListIndex.ListIndexListMaker.sortListForTsvSave(
//                    sortType,
//                    listIndexList
//                )
            TsvTool.updateTsvByRemove(
                tsvPath,
                removeItemLineList,
            )
        }

        fun switchQrIndexNumTextView(
            listIndexForEditAdapter: ListIndexForEditAdapter,
            fromViewHolder: ListIndexListViewHolder,
            toViewHolder: ListIndexListViewHolder,
        ){
            val totalListSize = listIndexForEditAdapter.listIndexList.size
            val from = fromViewHolder.bindingAdapterPosition
            val to = toViewHolder.bindingAdapterPosition
            val displayFromNum = "${totalListSize - from}"
            val displayToNum = "${totalListSize - to}"
            fromViewHolder.fileContentsQrLogoIndexNumTextView.text = displayToNum
            toViewHolder.fileContentsQrLogoIndexNumTextView.text = displayFromNum
        }

        fun updateAllQrIndexNumTextView(
            recyclerView: RecyclerView,
            srcListTotalSize: Int,
            removePosiList: List<Int>,
        ){
            val indexNumTextViewListSrc = (0 until srcListTotalSize).map {
                if(
                    removePosiList.contains(it)
                ) return@map null
                val viewholder = recyclerView.findViewHolderForLayoutPosition(it)
                    ?: return@map null
                val listIndexListViewHolder = viewholder as ListIndexListViewHolder
                listIndexListViewHolder.fileContentsQrLogoIndexNumTextView
            }
            val indexNumTextViewList = indexNumTextViewListSrc.filterNotNull()
            val newTotalListSize = indexNumTextViewList.size
            (0 until newTotalListSize).forEach {
                val displayIndexNum = "${newTotalListSize - it}"
                val currentIndexNumTextView = indexNumTextViewList[it]
                currentIndexNumTextView.text = displayIndexNum
            }
        }

        fun listIndexListUpdateFileList(
            editFragment: EditFragment,
            updateList: List<String>,
        ){
            val editListRecyclerView = editFragment.binding.editListRecyclerView
            if(
                !editListRecyclerView.isVisible
            ) return
            val listIndexForEditAdapter =
                editListRecyclerView.adapter as? ListIndexForEditAdapter
                    ?: return
            if(
                listIndexForEditAdapter.listIndexList ==
                updateList
            ) return
            listIndexForEditAdapter.listIndexList.clear()
            listIndexForEditAdapter.listIndexList.addAll(updateList)
            listIndexForEditAdapter.notifyDataSetChanged()
            scrollToBottom(
                editListRecyclerView,
                listIndexForEditAdapter,
            )
        }

        fun scrollToBottom(
            editListRecyclerView: RecyclerView,
            listIndexForEditAdapter: ListIndexForEditAdapter,
        ){
            listIndexScrollToBottomJob?.cancel()
            listIndexScrollToBottomJob = CoroutineScope(Dispatchers.Main).launch {
                val layoutManager = editListRecyclerView.layoutManager as? PreLoadLayoutManager
                val scrollToPosi = listIndexForEditAdapter.itemCount - 1
                withContext(Dispatchers.Main){
                    for(i in 1..30){
                        val prePosi = layoutManager?.findLastCompletelyVisibleItemPosition()
                        if(
                            prePosi == scrollToPosi
                        ) break
                        execScroll(
                            layoutManager,
                            scrollToPosi,
                        )
                        delay(150)
                    }
                }
                withContext(Dispatchers.Main){
                    for(i in 1..3){
                        delay(150)
                        execScroll(
                            layoutManager,
                            scrollToPosi,
                        )
                    }
                }
            }
        }

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

        fun sortInAddFile(
            editFragment: EditFragment,
            insertFilePath: String,
        ){
            val sortType = ListSettingsForListIndex.getSortType(indexListMap)
            when(sortType){
                ListSettingsForListIndex.SortByKey.LAST_UPDATE ->
                    listIndexListUpdateFileList(
                        editFragment,
                        ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                            editFragment,
                            indexListMap,
                            listIndexTypeKey
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
                    updateAllQrIndexNumTextView(
                        editListRecyclerView,
                        listIndexAdapter.listIndexList.size,
                        emptyList()
                    )
                    editListRecyclerView.layoutManager?.scrollToPosition(
                        insertIndex
                    )
                }
            }
        }

        fun removeCon(
            removeItemLine: String,
        ){
            if(
                !onDeleteConFile
            ) return
            val removeTitleConList = removeItemLine.split("\t")
            if(removeTitleConList.size != 2) return
            val filePath = removeTitleConList.last()
            val filePathObj = File(filePath)
            val fileParentDirPath = filePathObj.parent
                ?: return
            val fileName = filePathObj.name
            FileSystems.removeFiles(
                fileParentDirPath,
                fileName
            )
        }
    }
    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val materialCardView =
            view.findViewById<MaterialCardView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_mterial_card_view
            )
        val fileContentsQrLogoLinearLayout =
            view.findViewById<LinearLayoutCompat>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_logo_linearlayout
            )
        val fileContentsQrLogoView =
            view.findViewById<AppCompatImageView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_contents
            )
        val fileContentsQrLogoIndexNumTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_logo_index_num_text_view
            )
        val fileNameTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_file_name
            )
        val fileDescTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_file_desc
            )
        var fileName = String()
    }

    override fun getItemId(position: Int): Long {
        setHasStableIds(true)
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListIndexListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            com.puutaro.commandclick.R.layout.list_index_edit_adapter_layout,
            parent,
            false
        )
        return ListIndexListViewHolder(
            activity,
            itemView
        )
    }

    override fun getItemCount(): Int = listIndexList.size

    override fun onBindViewHolder(
        holder: ListIndexListViewHolder,
        listIndexPosition: Int
    ) {
        if(
            listIndexPosition > listLimitSize
        ) return
        initListProperty(listIndexPosition)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                ListIndexEditConfig.setCheckToMaterialCardView(
                    holder.materialCardView,
                    checkItemConfigMap,
                    listIndexList,
                    listIndexPosition,
                )
            }

            val fileNameOrInstallFannelLine =
                listIndexList.getOrNull(listIndexPosition)
                    ?: return@launch
            holder.fileName = FannelListVariable.getFannelName(
                fileNameOrInstallFannelLine
            )
            withContext(Dispatchers.Main) {
                ListIndexEditConfig.setFileNameTextView(
                    listIndexTypeKey,
                    holder.fileNameTextView,
                    holder.fileName,
                    listIndexConfigMap,
                    busyboxExecutor,
                )
            }

            val fileConList = withContext(Dispatchers.IO) {
                if (
                    listIndexConfigMap.isNullOrEmpty()
                ) return@withContext emptyList()
                if(
                    PerformSettingForListIndex.howFastMode(performMap)
                ) return@withContext emptyList()
                ReadText(
                    filterDir,
                    holder.fileName
                ).textToList().take(maxTakeSize)
            }
            val descCon = withContext(Dispatchers.IO){
                val makeFileDescArgsMaker = ListIndexEditConfig.MakeFileDescArgsMaker(
                    filterDir,
                    fileNameOrInstallFannelLine,
                    fileConList.joinToString("\n"),
                    listIndexConfigMap,
                    busyboxExecutor,
                )
                ListIndexEditConfig.makeFileDesc(
                    makeFileDescArgsMaker,
                )
            }
            withContext(Dispatchers.Main) {
                when(descCon.isNullOrEmpty()){
                    true -> holder.fileDescTextView.isVisible = false
                    else -> holder.fileDescTextView.text = descCon
                }
            }

            val fileConBackGroundColorInt = withContext(Dispatchers.IO) {
                val editExecuteValueForInstallFannel = when(
                    listIndexTypeKey
                ){
                    TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
                    -> FannelListVariable.getEditExecute(
                        fileNameOrInstallFannelLine
                    )
                    else -> String()
                }
                setFileContentsBackColor(
                    fileConList,
                    fileNameOrInstallFannelLine,
                    editExecuteValueForInstallFannel,
                )
            }
            withContext(Dispatchers.Main) {
                context?.let {
                    holder.fileContentsQrLogoLinearLayout.backgroundTintList =
                        it.getColorStateList(fileConBackGroundColorInt)
                }
            }
            withContext(Dispatchers.Main) {
                QrDialogConfig.setOneSideLength(
                    holder.fileContentsQrLogoLinearLayout,
                    qrLogoConfigMap
                )
            }
            withContext(Dispatchers.Main) {
                val disableQrLogo =
                    QrDialogConfig.howDisableQrLogo(qrLogoConfigMap)
                if(disableQrLogo) return@withContext
                qrLogoSetHandler(
                    holder,
                    recentAppDirPath,
                    listIndexPosition,
                )
            }
            withContext(Dispatchers.Main) {
                val itemView = holder.itemView
                itemView.setOnLongClickListener {
                    itemLongClickListener?.onItemLongClick(
                        itemView,
                        holder,
                        listIndexPosition
                    )
                    true
                }
                itemView.setOnClickListener {
                    fileNameClickListener?.onFileNameClick(
                        itemView,
                        holder,
                        listIndexPosition,
                    )
                }
                val fileContentsQrLogoView = holder.fileContentsQrLogoView
                fileContentsQrLogoView.setOnClickListener {
                    fileQrLogoClickListener?.onFileQrLogoClick(
                        itemView,
                        holder,
                        listIndexPosition
                    )
                }
                fileContentsQrLogoView.setOnLongClickListener {
                    qrLongClickListener?.onQrLongClick(
                        fileContentsQrLogoView,
                        holder,
                        listIndexPosition
                    )
                    true
                }
            }
        }
    }

    var fileNameClickListener: OnFileNameItemClickListener? = null
    var qrLongClickListener: OnQrLogoLongClickListener? = null
    interface OnFileNameItemClickListener {
        fun onFileNameClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

    interface OnQrLogoLongClickListener {
        fun onQrLongClick(
            imageView: AppCompatImageView,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int
        )
    }

    var fileQrLogoClickListener: OnFileQrLogoItemClickListener? = null
    interface OnFileQrLogoItemClickListener {
        fun onFileQrLogoClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

    var itemLongClickListener: OnItemLongClickListener? = null
    interface OnItemLongClickListener {
        fun onItemLongClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int
        )
    }

    private fun setFileContentsBackColor(
        fileConList: List<String>,
        fileName: String,
        editExecuteValueForInstallFannel: String,
    ): Int {
        if(
            context == null
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
        if(
            editExecuteValueForInstallFannel == editExecuteAlways
        ) return com.puutaro.commandclick.R.color.terminal_color
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            fileConList,
            settingSectionStart,
            settingSectionEnd
        )
        val editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        if(
            editExecuteValue
            == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ) return com.puutaro.commandclick.R.color.terminal_color
        return com.puutaro.commandclick.R.color.fannel_icon_color
    }

    private suspend fun qrLogoSetHandler(
        holder: ListIndexListViewHolder,
        recentAppDirPath: String,
        listIndexPosition: Int,
    ){
        val qrMode = QrModeSettingKeysForQrDialog.getQrMode(qrDialogConfigMap)
        when(qrMode){
            QrModeSettingKeysForQrDialog.QrMode.TSV_EDIT -> {
                holder.fileContentsQrLogoView.isVisible = false
                val totalListSize = listIndexList.size
                val displayIndexNum = "${totalListSize - listIndexPosition}"
                val fileContentsQrLogoTextView = holder.fileContentsQrLogoIndexNumTextView
                fileContentsQrLogoTextView.text = displayIndexNum
                fileContentsQrLogoTextView.isVisible = true
                fileContentsQrLogoTextView.textSize =
                    QrDialogConfig.decideTextSize(qrLogoConfigMap)
            }
            QrModeSettingKeysForQrDialog.QrMode.NORMAL,
            QrModeSettingKeysForQrDialog.QrMode.FANNEL_REPO -> {
                val qrLogoHandlerArgsMaker = withContext(Dispatchers.IO) {
                    QrDialogConfig.QrLogoHandlerArgsMaker(
                        editFragment,
                        recentAppDirPath,
                        readSharePreffernceMap,
                        qrLogoConfigMap,
                        filterDir,
                        holder.fileName,
                        holder.fileContentsQrLogoView,
                    )
                }
                withContext(Dispatchers.Main) {
                    QrDialogConfig.setQrLogoHandler(
                        qrLogoHandlerArgsMaker
                    )
                }
            }
        }
    }

    private fun initListProperty(
        listIndexPosition: Int
    ){
        if(listIndexPosition != 0) return
        setListProperty()
    }

    private fun setListProperty(){
        recentAppDirPath = FileSystems.getRecentAppDirPath()
        performMap = PerformSettingForListIndex.makePerformMap(
            listIndexConfigMap
        )
        indexListMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LIST.key
        )
        listIndexTypeKey = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        onDeleteConFile = ListSettingsForListIndex.howOnDeleteConFileValue(
            indexListMap
        )
        filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            indexListMap,
            listIndexTypeKey
        )
        filterPrefix = ListSettingsForListIndex.getListSettingKeyHandler(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.PREFIX.key
        )
        filterSuffix = ListSettingsForListIndex.getListSettingKeyHandler(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.SUFFIX.key
        )
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
    val insertIndex = ListIndexForEditAdapter.getInsertIndex(
        sortType,
        listIndexForEditAdapter,
        fileNameElement,
    )
    ListIndexForEditAdapter.listUpdateByInsertItem(
        editFragment,
        fileNameElement,
        insertIndex
    )
}

private fun execScroll(
    layoutManager: PreLoadLayoutManager?,
    scrollToPosi: Int,
){
    try {
        layoutManager?.scrollToPosition(
            scrollToPosi
        )
    }catch (e: Exception){
        return
    }
}
