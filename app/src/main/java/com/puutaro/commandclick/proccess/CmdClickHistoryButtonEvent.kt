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
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
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
    private val historyListView = ListView(currentViewContext)
    private val searchText = EditText(currentViewContext)
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
        val historyListAdapter = ArrayAdapter(
            currentViewContext,
            R.layout.simple_list_item_1,
            historyList
        )
        historyListView.adapter = historyListAdapter
        historyListView.setSelection(
            historyListAdapter.count
        )

        linearLayoutForListView.addView(historyListView)

        makeSearchEditText(historyListAdapter)

        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)


        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle("Select app history")
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
            historyList,
            cmdclickAppHistoryDirAdminPath
        )

        invokeItemSetClickListenerForHistory(
            alertDialog,
        )
    }

    private fun makeSearchEditText(
        historyListAdapter: ArrayAdapter<String>
    ){
        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParamForSearchText.topMargin = 20
        linearLayoutParamForSearchText.bottomMargin = 20
        searchText.layoutParams = linearLayoutParamForSearchText
        searchText.background = null
        searchText.inputType = InputType.TYPE_CLASS_TEXT
        searchText.hint = "search"
        searchText.setPadding(30, 10, 20, 10)
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
                CommandListManager.execListUpdateByEditText(
                    filteredCmdStrList,
                    historyListAdapter,
                    historyListView
                )
                historyListView.setSelection(
                    historyListAdapter.count
                )
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
    ) {
        historyListView.setOnItemClickListener {
                parent, View, pos, id
            ->

            alertDialog.dismiss()
            terminalViewModel.onDialog = false
            val updateHistoryList = makeUpdateHistoryList()
            val selectedHistoryFile = updateHistoryList.filter {
                Regex(
                    searchText.text
                        .toString()
                        .lowercase()
                ).containsMatchIn(
                    it.lowercase()
                )
            }
                .getOrNull(pos)
                ?.split("\n")
                ?.firstOrNull()
                ?: return@setOnItemClickListener
            val resultBool = AppHistoryAdminEvent.invoke(
                fragment,
                sharedPref,
                selectedHistoryFile,
            )
            if(!resultBool) return@setOnItemClickListener
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
            return@setOnItemClickListener
        }
    }

    private fun invokeItemSetLongTimeClickListenerForHistory(
        historyListAdapter: ArrayAdapter<String>,
        historyList: List<String>,
        cmdclickAppHistoryDirAdminPath: String,
    ){
        historyListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener {
                    parent, listSelectedView, pos, id ->
                val popup = PopupMenu(currentViewContext, listSelectedView)
                val selectedHistoryFile = historyList.filter {
                    Regex(
                        searchText.text
                            .toString()
                            .lowercase()
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }.get(pos)
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
                true
            }
    }


    fun execDeleteHistoryFile(
        selectedHistoryFile: String,
        cmdclickAppHistoryDirAdminPath: String,
        historyListAdapter: ArrayAdapter<String>,
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
        CommandListManager.execListUpdate(
            cmdclickAppHistoryDirAdminPath,
            historyListAdapter,
            historyListView
        )
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

