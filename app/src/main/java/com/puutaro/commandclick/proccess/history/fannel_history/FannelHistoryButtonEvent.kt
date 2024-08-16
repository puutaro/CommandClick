package com.puutaro.commandclick.proccess.history.fannel_history

import android.app.Dialog
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.FannelHistoryAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.history.libs.HistoryShareImage
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FannelStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


//private val mainMenuGroupId = 30000

//private enum class HistoryMenuEnums(
//    val groupId: Int,
//    val itemId: Int,
//    val order: Int,
//    val itemName: String
//) {
//    DELETE(mainMenuGroupId, 30100, 1, "delete"),
//}
class FannelHistoryButtonEvent (
    private val fragment: Fragment,
    private val sharedPref: FannelInfoTool.FannelInfoSharePref?,
    )
 {
    private val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
    private val context = fragment.context
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private var fannelHistoryDialog: Dialog? = null
    private var updateRecyclerJob: Job? = null

    private val homeFannelList = when(
        fragment
    ) {
        is CommandIndexFragment -> {
            fragment.homeFannelHistoryNameList
        }
        is EditFragment -> {
            fragment.homeFannelHistoryNameList
        }
        else -> emptyList()
    } ?: emptyList()

    fun invoke(
    ) {
        if(
            context == null
        ) return
        deleteOverHistory(
            cmdclickAppHistoryDirAdminPath
        )
        FileSystems.createFiles(
            File(
                cmdclickAppHistoryDirAdminPath,
                FannelHistoryManager.makeAppHistoryFileNameForInit(
                    UsePath.cmdclickDefaultAppDirName,
                )
            ).absolutePath
        )
        val historyList =  makeUpdateHistoryList()
        val fannelInfoMap = when(fragment){
            is CommandIndexFragment -> {
                fragment.fannelInfoMap
            }
            is EditFragment -> {
                fragment.fannelInfoMap
            }
            else -> emptyMap()
        }
        val fannelHistoryListAdapter = FannelHistoryAdapter(
            context,
            fannelInfoMap,
            historyList.toMutableList()
        )
        fannelHistoryDialog = Dialog(
            context
        )
        fannelHistoryDialog?.setContentView(
                com.puutaro.commandclick.R.layout.fannel_history_recycler_view
            )
        val searchText =
            fannelHistoryDialog?.findViewById<AppCompatEditText>(
                com.puutaro.commandclick.R.id.fannel_history_search_edit_text
            )
        val searchTextLinearParams =
            searchText?.layoutParams as LinearLayout.LayoutParams
        searchTextLinearParams.weight = searchTextLinearWeight
        val fannelHistoryListView =
            fannelHistoryDialog?.findViewById<RecyclerView>(
                com.puutaro.commandclick.R.id.fannel_history_recycler_view
            )
        val historyListViewLinearParams =
            fannelHistoryListView?.layoutParams as LinearLayout.LayoutParams
        historyListViewLinearParams.weight = listLinearWeight
        fannelHistoryListView.layoutManager = GridLayoutManager(
            context,
            2,
            LinearLayoutManager.VERTICAL,
            false
        )
        fannelHistoryListView.adapter = fannelHistoryListAdapter
        fannelHistoryListView.layoutManager?.scrollToPosition(
            fannelHistoryListAdapter.itemCount - 1
        )
        makeSearchEditText(
            fannelHistoryListAdapter,
            fannelHistoryListView,
            searchText
        )
        setItemTouchHelper(
            fannelHistoryListView,
            fannelHistoryListAdapter,
            searchText,
        )
        fannelHistoryDialog?.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        fannelHistoryDialog?.window
            ?.setGravity(Gravity.BOTTOM)
        fannelHistoryDialog?.show()
        fannelHistoryDialog?.setOnCancelListener(
            object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
//                    terminalViewModel.onDialog = false
                    exitDialog(
                        fannelHistoryListView
                    )
                }
            })
//        terminalViewModel.onDialog = true

//        invokeItemSetLongTimeClickListenerForHistory(
//            historyListAdapter,
//            searchText,
//            cmdclickAppHistoryDirAdminPath
//        )
        invokeItemSetClickListenerForHistory(
            fannelHistoryListView,
            fannelHistoryListAdapter
        )
        setFannelHistoryListViewOnDeleteItemClickListener (
            fannelHistoryListAdapter,
            searchText,
        )
        setFannelHistoryListViewOnLogoItemClickListener (
            fannelHistoryListAdapter,
        )
    }

    private fun makeSearchEditText(
        historyListAdapter: FannelHistoryAdapter,
        fannelHistoryListView: RecyclerView?,
        searchText: AppCompatEditText,
    ){
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return

                val updateHistoryList = makeUpdateHistoryList()
                val filteredCmdStrList = updateHistoryList.filter {
                    Regex(
                        s.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                historyListAdapter.historyList.clear()
                historyListAdapter.historyList.addAll(filteredCmdStrList)
                historyListAdapter.notifyDataSetChanged()
                updateRecyclerJob?.cancel()
                updateRecyclerJob = CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(200)
                    }
                    fannelHistoryListView?.layoutManager?.scrollToPosition(
                        historyListAdapter.itemCount - 1
                    )
                }

            }
        })
    }

    private fun makeUpdateHistoryList(): List<String> {
        val fannelList = FileSystems.filterSuffixShellOrJsFiles(
            cmdclickAppHistoryDirAdminPath
        ).filter {
            !homeFannelList.contains(it)
        }
        val historyListSource =  fannelList + homeFannelList.reversed()
        val historyListSourceSize = historyListSource.size
        return when(
            historyListSourceSize % 2 == 1
                    &&  historyListSourceSize > 3
        ){
            true -> historyListSource + homeFannelList.last()
            else -> historyListSource
        }
    }

    private fun invokeItemSetClickListenerForHistory(
        historyListView: RecyclerView,
        historyListAdapter: FannelHistoryAdapter,
    ) {
        historyListAdapter.itemClickListener = object: FannelHistoryAdapter.OnItemClickListener {
            override fun onItemClick(holder: FannelHistoryAdapter.FannelHistoryViewHolder) {
//                terminalViewModel.onDialog = false
                val position = holder.bindingAdapterPosition
                val historyLine = historyListAdapter.historyList[position]
                val appDirName =
                    FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                        historyLine
                    )
                val fannelName =
                    FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                        historyLine
                    )
                val selectedHistoryFile = FannelHistoryManager.makeAppHistoryFileNameForInit(
                    appDirName,
                    fannelName
                )
                val selectedAppDirPath =
                    File(
                        UsePath.cmdclickAppDirPath,
                        appDirName
                    ).absolutePath
                val selectedHistoryFilePath = File(
                    UsePath.cmdclickAppHistoryDirAdminPath,
                    selectedHistoryFile
                ).absolutePath
                if(
                    !File(selectedAppDirPath).isDirectory
                ) {
                    ToastUtils.showLong("No exist: ${selectedAppDirPath}")
                    FileSystems.removeFiles(
                        selectedHistoryFilePath
                    )
                    exitDialog(
                        historyListView
                    )
                    return
                }
                updateLastModifyForHistoryAndAppDir(
                    selectedHistoryFilePath,
                    appDirName
                )
                val mainFannelConList = ReadText(
                    File(selectedAppDirPath, fannelName).absolutePath
                ).textToList()
                val setReplaceVariableMap = when(
                    fannelName.isEmpty()
                ) {
                    true -> null
                    else -> JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                        context,
                        mainFannelConList,
                        appDirName,
                        fannelName,
                    )
                }

                val mainFannelSettingConList = CommandClickVariables.extractSettingValListByFannelName(
                    mainFannelConList,
                    fannelName
                ).let {
                    SetReplaceVariabler.execReplaceByReplaceVariables(
                        it?.joinToString("\n") ?: String(),
                        setReplaceVariableMap,
                        appDirName,
                        fannelName,
                    ).split("\n")
                }
                FannelHistoryAdminEvent.register(
                    sharedPref,
                    selectedAppDirPath,
                    fannelName,
                    mainFannelSettingConList,
                    setReplaceVariableMap,
                )
                launchHandler(
                    selectedHistoryFile,
                    selectedAppDirPath,
                    fannelName,
                    mainFannelSettingConList,
                    setReplaceVariableMap
                )
                exitDialog(
                    historyListView
                )
            }
        }
    }

    private fun launchHandler(
        selectedHistoryFile: String,
        selectedAppDirPath: String,
        fannelName: String,
        mainFannelSettingConList: List<String>?,
        setReplaceVariableMap: Map<String, String>?
    ){
        val isJsExec = FannelHistoryJsEvent.run(
            fragment,
            selectedHistoryFile,
        )
        val fannelState = FannelStateManager.getState(
            selectedAppDirPath,
            fannelName,
            mainFannelSettingConList,
            setReplaceVariableMap
        )
        val fannelInfoMap = EditFragmentArgs.createFannelInfoMap(
            selectedAppDirPath,
            fannelName,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
            fannelState
        )
        val cmdValEdit =
            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        val jsExecWaitTime =
            if(isJsExec) 200L
            else 200L
        CoroutineScope(Dispatchers.Main).launch {
            delay(jsExecWaitTime)
            if(fragment is CommandIndexFragment) {
                FileSystems
                val listener = context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                listener?.onLongClickMenuItemsforCmdIndex(
                    LongClickMenuItemsforCmdIndex.EXEC_HISTORY,
                    EditFragmentArgs(
                        fannelInfoMap,
                        cmdValEdit,
                    ),
                    String(),
                    String()

                )
            } else {
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    ToolbarMenuCategoriesVariantForCmdIndex.HISTORY,
                    EditFragmentArgs(
                        fannelInfoMap,
                        cmdValEdit,
                    )
                )
            }
        }
    }

    private fun setFannelHistoryListViewOnDeleteItemClickListener (
        fannelHistoryAdapter: FannelHistoryAdapter,
        searchText: AppCompatEditText,
    ){
        fannelHistoryAdapter.deleteItemClickListener = object: FannelHistoryAdapter.OnDeleteItemClickListener {
            override fun onItemClick(holder: FannelHistoryAdapter.FannelHistoryViewHolder) {
                val bindingAdapterPosition = holder.bindingAdapterPosition
                val historyLine = fannelHistoryAdapter.historyList[bindingAdapterPosition]
                val appDirName =
                    FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                        historyLine
                    )
                val fannelName =
                    FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                        historyLine
                    )
                val selectedHistoryFile = FannelHistoryManager.makeAppHistoryFileNameForInit(
                    appDirName,
                    fannelName
                )
                execDeleteHistoryFile(
                    selectedHistoryFile,
                    cmdclickAppHistoryDirAdminPath,
                    fannelHistoryAdapter,
                    bindingAdapterPosition,
                    searchText,
                )
//                fannelHistoryAdapter.notifyDataSetChanged()
//                updateRecyclerJob?.cancel()
//                updateRecyclerJob = CoroutineScope(Dispatchers.Main).launch {
//                    withContext(Dispatchers.IO){
//                        delay(200)
//                    }
//                    urlHistoryListView?.layoutManager?.scrollToPosition(
//                        fannelHistoryAdapter.itemCount - 1
//                    )
//                }
            }
        }
    }

    private fun setFannelHistoryListViewOnLogoItemClickListener (
        fannelHistoryAdapter: FannelHistoryAdapter,
    ){
        fannelHistoryAdapter.shareItemClickListener = object: FannelHistoryAdapter.OnShareItemClickListener {
            override fun onItemClick(holder: FannelHistoryAdapter.FannelHistoryViewHolder) {
                val urlHistoryAdapterRelativeLayout = holder.fannelHistoryAdapterRelativeLayout
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main){
                        ToastUtils.showShort("share")
                    }
                    val pngImagePathObj = HistoryShareImage.makePngImageFromView(
                        context,
                        urlHistoryAdapterRelativeLayout
                    ) ?: return@launch
                    withContext(Dispatchers.Main) {
                        IntentVariant.sharePngImage(
                            pngImagePathObj,
                            context,
                        )
                    }
                }
            }
        }
    }

    private fun setItemTouchHelper(
        recyclerView: RecyclerView,
        fannelHistoryAdapter: FannelHistoryAdapter,
        searchText: AppCompatEditText?,
    ){
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if(
                        direction != ItemTouchHelper.LEFT
                        && direction != ItemTouchHelper.RIGHT
                    ) return
                    val position = viewHolder.layoutPosition
                    val historyLine = fannelHistoryAdapter.historyList[position]
                    val appDirName =
                        FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                            historyLine
                        )
                    val fannelName =
                        FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                            historyLine
                        )
                    val selectedHistoryFile = FannelHistoryManager.makeAppHistoryFileNameForInit(
                        appDirName,
                        fannelName
                    )
                    execDeleteHistoryFile(
                        selectedHistoryFile,
                        cmdclickAppHistoryDirAdminPath,
                        fannelHistoryAdapter,
                        position,
                        searchText,
                    )
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }

//    private fun invokeItemSetLongTimeClickListenerForHistory(
//        historyListAdapter: FannelHistoryAdapter,
//        searchText: EditText,
//        cmdclickAppHistoryDirAdminPath: String,
//    ){
//        historyListAdapter.itemLongClickListener = object :
//            FannelHistoryAdapter.OnItemLongClickListener {
//            override fun onItemLongClick(
//                itemView: View,
//                holder: FannelHistoryAdapter.FannelHistoryViewHolder
//            ) {
//                val appDirName = holder.appDirNameTextView.text.toString()
//                val fannelName = holder.fannelNameTextView.text.toString()
//                val selectedHistoryFile = AppHistoryManager.makeAppHistoryFileNameForInit(
//                    appDirName,
//                    fannelName
//                )
//                val popup = PopupMenu(
//                    context,
//                    itemView
//                )
//                val inflater = popup.menuInflater
//                inflater.inflate(
//                    com.puutaro.commandclick.R.menu.history_admin_menu,
//                    popup.menu
//                )
//                popup.menu.add(
//                    HistoryMenuEnums.DELETE.groupId,
//                    HistoryMenuEnums.DELETE.itemId,
//                    HistoryMenuEnums.DELETE.order,
//                    HistoryMenuEnums.DELETE.itemName,
//                )
//                popup.setOnMenuItemClickListener {
//                        menuItem ->
//                    execDeleteHistoryFile(
//                        selectedHistoryFile,
//                        cmdclickAppHistoryDirAdminPath,
//                        historyListAdapter,
//                    )
//                    searchText.text.clear()
//                    true
//                }
//                popup.show()
//            }
//        }
//    }


    fun execDeleteHistoryFile(
        selectedHistoryFile: String,
        cmdclickAppHistoryDirAdminPath: String,
        historyListAdapter: FannelHistoryAdapter,
        position: Int,
        searchText: AppCompatEditText?,
    ) {
        val selectedDeleteFile =
            selectedHistoryFile
                .split("\n")
                .firstOrNull()
                ?: return
        FileSystems.removeFiles(
            File(
                cmdclickAppHistoryDirAdminPath,
                selectedDeleteFile,
            ).absolutePath
        )
        historyListAdapter.historyList.removeAt(position)
        historyListAdapter.notifyItemRemoved(position)
        searchText?.text?.clear()
    }

    fun updateLastModifyForHistoryAndAppDir(
        selectedHistoryFilePath: String,
        selectedAppDirName: String,
    ){
        FileSystems.updateLastModified(
            selectedHistoryFilePath
        )
        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppDirAdminPath,
                selectedAppDirName + UsePath.JS_FILE_SUFFIX
            ).absolutePath
        )
    }

    private fun exitDialog(
        historyListView: RecyclerView
    ){
        historyListView.layoutManager = null
        historyListView.adapter = null
        historyListView.recycledViewPool.clear()
        historyListView.removeAllViews()
        fannelHistoryDialog?.dismiss()
        fannelHistoryDialog = null
    }
}

private fun deleteOverHistory(
    cmdclickAppHistoryDirAdminPath: String
){
    val leavesHistoryNum = 50
    val dirFiles = FileSystems.sortedFiles(
        cmdclickAppHistoryDirAdminPath,
    )
    if(dirFiles.isEmpty()) return
    val deleteFileNum = dirFiles.size - leavesHistoryNum
    if(deleteFileNum <= 0) return
    val deletingFiles = dirFiles.take(deleteFileNum)
    deletingFiles.forEach {
        try {
            val fileEntry = File(
                cmdclickAppHistoryDirAdminPath,
                it
            )
            if(!fileEntry.isFile) return@forEach
            fileEntry.delete()
        } catch (e: Exception) {
            println("pass")
        }
    }
}