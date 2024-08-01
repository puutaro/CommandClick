package com.puutaro.commandclick.proccess.history

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
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
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FannelStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class CmdClickHistoryButtonEvent (
//    historyButtonInnerView: View,
    private val fragment: Fragment,
    private val sharedPref: FannelInfoTool.FannelInfoSharePref?,
    )
{
    private val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
//    private val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    private val context = fragment.context
//    private val currentViewContext = historyButtonInnerView.context
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private var fannelHistoryDialog: Dialog? = null

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
                AppHistoryManager.makeAppHistoryFileNameForInit(
                    UsePath.cmdclickDefaultAppDirName,
                )
            ).absolutePath
        )
        val historyList =  makeUpdateHistoryList()
        val historyListAdapter = FannelHistoryAdapter(
            historyList.toMutableList()
        )
        fannelHistoryDialog = Dialog(
            context
        )
        fannelHistoryDialog?.setContentView(
                com.puutaro.commandclick.R.layout.fannel_history_recycler_view
            )
        val searchText =
            fannelHistoryDialog?.findViewById<EditText>(
                com.puutaro.commandclick.R.id.fannel_history_search_edit_text
            )
        val searchTextLinearParams =
            searchText?.layoutParams as LinearLayout.LayoutParams
        searchTextLinearParams.weight = searchTextLinearWeight
        val historyListView =
            fannelHistoryDialog?.findViewById<RecyclerView>(
                com.puutaro.commandclick.R.id.fannel_history_recycler_view
            )
        val historyListViewLinearParams =
            historyListView?.layoutParams as LinearLayout.LayoutParams
        historyListViewLinearParams.weight = listLinearWeight
        historyListView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        historyListView.adapter = historyListAdapter
        historyListView.layoutManager?.scrollToPosition(
            historyListAdapter.itemCount - 1
        )
        makeSearchEditText(
            historyListAdapter,
            searchText
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
                    fannelHistoryDialog?.dismiss()
                    fannelHistoryDialog = null
                }
            })
//        terminalViewModel.onDialog = true

        invokeItemSetLongTimeClickListenerForHistory(
            historyListAdapter,
            searchText,
            cmdclickAppHistoryDirAdminPath
        )
        invokeItemSetClickListenerForHistory(
            historyListAdapter
        )
    }

    private fun makeSearchEditText(
        historyListAdapter: FannelHistoryAdapter,
        searchText: EditText,
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
            }
        })
    }

    private fun makeUpdateHistoryList(): List<String> {
        val historyListSource =  FileSystems.filterSuffixShellOrJsFiles(
            cmdclickAppHistoryDirAdminPath
        ).filter {
            !homeFannelList.contains(it)
        } + homeFannelList.reversed()
        return historyListSource
    }

    private fun invokeItemSetClickListenerForHistory(
        historyListAdapter: FannelHistoryAdapter,
    ) {
        historyListAdapter.itemClickListener = object: FannelHistoryAdapter.OnItemClickListener {
            override fun onItemClick(holder: FannelHistoryAdapter.HistoryViewHolder) {
                fannelHistoryDialog?.dismiss()
                fannelHistoryDialog = null
//                terminalViewModel.onDialog = false
                val appDirName = holder.appDirNameTextView.text.toString()
                val fannelName = holder.fannelNameTextView.text.toString()
                val selectedHistoryFile = AppHistoryManager.makeAppHistoryFileNameForInit(
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
                AppHistoryAdminEvent.register(
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
        val isJsExec = AppHistoryJsEvent.run(
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

    private fun invokeItemSetLongTimeClickListenerForHistory(
        historyListAdapter: FannelHistoryAdapter,
        searchText: EditText,
        cmdclickAppHistoryDirAdminPath: String,
    ){
        historyListAdapter.itemLongClickListener = object :
            FannelHistoryAdapter.OnItemLongClickListener {
            override fun onItemLongClick(
                itemView: View,
                holder: FannelHistoryAdapter.HistoryViewHolder
            ) {
                val appDirName = holder.appDirNameTextView.text.toString()
                val fannelName = holder.fannelNameTextView.text.toString()
                val selectedHistoryFile = AppHistoryManager.makeAppHistoryFileNameForInit(
                    appDirName,
                    fannelName
                )
                val popup = PopupMenu(
                    context,
                    itemView
                )
                val inflater = popup.menuInflater
                inflater.inflate(
                    com.puutaro.commandclick.R.menu.history_admin_menu,
                    popup.menu
                )
                popup.menu.add(
                    HistoryMenuEnums.DELETE.groupId,
                    HistoryMenuEnums.DELETE.itemId,
                    HistoryMenuEnums.DELETE.order,
                    HistoryMenuEnums.DELETE.itemName,
                )
                popup.setOnMenuItemClickListener {
                        menuItem ->
                    execDeleteHistoryFile(
                        selectedHistoryFile,
                        cmdclickAppHistoryDirAdminPath,
                        historyListAdapter,
                    )
                    searchText.text.clear()
                    true
                }
                popup.show()
            }
        }
    }


    fun execDeleteHistoryFile(
        selectedHistoryFile: String,
        cmdclickAppHistoryDirAdminPath: String,
        historyListAdapter: FannelHistoryAdapter,
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
        historyListAdapter.historyList.remove(selectedHistoryFile)
        historyListAdapter.notifyDataSetChanged()
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
}

private val mainMenuGroupId = 30000

private enum class HistoryMenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    DELETE(mainMenuGroupId, 30100, 1, "delete"),
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
