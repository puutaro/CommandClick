package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.history_button

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.component.adapter.FannelHistoryAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.AppHistoryAdminEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.AppHistoryManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.UrlTitleTrimmer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File


class CmdClickHistoryButtonEvent (
    historyButtonInnerView: View,
    private val fragment: Fragment,
    private val sharedPref: SharedPreferences?,
    )
{

    private val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
    private val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    private val context = fragment.context
    private val currentViewContext = historyButtonInnerView.context
    private val historyListView = RecyclerView(
        ContextThemeWrapper(
            currentViewContext,
            com.puutaro.commandclick.R.style.ScrollbarRecyclerView
        )
    )
    private val searchText = makeSearchEditTextView()
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private val linearLayoutForTotal = LinearLayoutForTotal.make(
        currentViewContext
    )
    private val linearLayoutForListView = NestLinearLayout.make(
        currentViewContext,
        listLinearWeight
    )
    private val linearLayoutForSearch = NestLinearLayout.make(
        currentViewContext,
        searchTextLinearWeight
    )

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

    fun invoke() {
        deleteOverHistory(
            cmdclickAppHistoryDirAdminPath
        )
        FileSystems.createFiles(
            cmdclickAppHistoryDirAdminPath,
            AppHistoryManager.makeAppHistoryFileNameForInit(
                UsePath.cmdclickDefaultAppDirName,
            )
        )
        val historyList =  makeUpdateHistoryList()
        val historyListAdapter = FannelHistoryAdapter(
            historyList.toMutableList()
        )
        historyListView.layoutManager = LinearLayoutManager(
            currentViewContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        historyListView.adapter = historyListAdapter
        historyListView.layoutManager?.scrollToPosition(
            historyListAdapter.itemCount - 1
        )

        linearLayoutForListView.addView(historyListView)

        makeSearchEditText(historyListAdapter)

        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)


        val alertDialog = AlertDialog.Builder(
            context
        )
//            .setTitle("Select app history")
            .setView(linearLayoutForTotal)
            .create()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.show()
        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
        terminalViewModel.onDialog = true

        invokeItemSetLongTimeClickListenerForHistory(
            historyListAdapter,
            cmdclickAppHistoryDirAdminPath
        )
        invokeItemSetClickListenerForHistory(
            alertDialog,
            historyListAdapter
        )
    }

    private fun makeSearchEditText(
        historyListAdapter: FannelHistoryAdapter
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
        return historyListSource.map {
            makeHistoryListRow(it)
        }
    }

    private fun invokeItemSetClickListenerForHistory(
        alertDialog: AlertDialog,
        historyListAdapter: FannelHistoryAdapter,
    ) {
        historyListAdapter.itemClickListener = object: FannelHistoryAdapter.OnItemClickListener {
            override fun onItemClick(holder: FannelHistoryAdapter.HistoryViewHolder) {
                alertDialog.dismiss()
                terminalViewModel.onDialog = false
                val appDirName = holder.appDirNameTextView.text.toString()
                val fannelName = holder.fannelNameTextView.text.toString()
                val selectedHistoryFile = AppHistoryManager.makeAppHistoryFileNameForInit(
                    appDirName,
                    fannelName
                )
                val resultBool = AppHistoryAdminEvent.invoke(
                    fragment,
                    sharedPref,
                    selectedHistoryFile,
                )
                if(
                    !resultBool
                ) return
                if(fragment is CommandIndexFragment) {
                    val listener = context
                            as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                    listener?.onLongClickMenuItemsforCmdIndex(
                        LongClickMenuItemsforCmdIndex.EXEC_HISTORY
                    )
                } else {
                    val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        ToolbarMenuCategoriesVariantForCmdIndex.HISTORY
                    )
                }
            }
        }
    }

    private fun invokeItemSetLongTimeClickListenerForHistory(
        historyListAdapter: FannelHistoryAdapter,
        cmdclickAppHistoryDirAdminPath: String,
    ){
        historyListAdapter.itemLongClickListener = object : FannelHistoryAdapter.OnItemLongClickListener {
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
                    currentViewContext,
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
            cmdclickAppHistoryDirAdminPath,
            selectedDeleteFile,
        )
        historyListAdapter.historyList.remove(selectedHistoryFile)
        historyListAdapter.notifyDataSetChanged()
    }

    private fun makeSearchEditTextView(
    ): EditText {
        val searchEditText = EditText(currentViewContext)
        searchEditText.textSize = 20f
        searchEditText.hint = "search"
        searchEditText.setPadding(40, 20, 20, 35)
        searchEditText.setBackgroundResource(android.R.color.transparent)
        searchEditText.inputType = InputType.TYPE_CLASS_TEXT
        return searchEditText
    }



    private fun makeHistoryListRow(
        historyRow: String
    ): String {
//        homeFannelList
        val selectedAppShellFileName = AppHistoryManager
            .getScriptFileNameFromAppHistoryFileName(
                historyRow
            )
        if(selectedAppShellFileName != CommandClickScriptVariable.EMPTY_STRING) {
            return historyRow
        }
        val appDirName =
            AppHistoryManager.getAppDirNameFromAppHistoryFileName(
                historyRow
            )
        val appDirPath = "${UsePath.cmdclickAppDirPath}/${appDirName}"
        val appUrlSystemDirPath = "${appDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val currentUrlHistoryPath =
            "${appUrlSystemDirPath}/${UsePath.cmdclickUrlHistoryFileName}"
        if(
            !File(currentUrlHistoryPath).isFile
        ) return historyRow
        val urlTitleSource = ReadText(
            appUrlSystemDirPath,
            UsePath.cmdclickFirstHistoryTitle
        ).textToList()
            .firstOrNull()
            ?.split("\t")
            ?.firstOrNull() ?: return historyRow
        val urlTitle = UrlTitleTrimmer.trim(
            urlTitleSource
        )
        return "${historyRow}\n\t- ${urlTitle}"

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

