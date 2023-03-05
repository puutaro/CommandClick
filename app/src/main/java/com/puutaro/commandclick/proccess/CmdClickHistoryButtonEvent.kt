package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.history_button

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.AppHistoryAdminEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.util.AppHistoryManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.UrlTitleTrimmer
import java.io.File


class CmdClickHistoryButtonEvent (
    private val historyButtonInnerView: View,
    private val fragment: Fragment,
    private val sharedPref: SharedPreferences?,
    )
{
    val context = fragment.context
    val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath

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
        val historyList = FileSystems.filterSuffixShellFiles(
            cmdclickAppHistoryDirAdminPath
        ).map { makeHistoryListRow(it) }

        val currentViewContext = historyButtonInnerView.context
        val historyListView = ListView(currentViewContext)
        val historyListAdapter = ArrayAdapter(
            historyButtonInnerView.context,
            R.layout.simple_list_item_1,
            historyList
        )
        historyListView.adapter = historyListAdapter
        historyListView.setSelection(historyListAdapter.count);

        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle("Select app history")
            .setView(historyListView)
            .create()
        alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        alertDialog.show()

        invokeItemSetLongTimeClickListnerForHistory(
            currentViewContext,
            historyListView,
            historyListAdapter,
            historyList,
            historyButtonInnerView,
            cmdclickAppHistoryDirAdminPath
        )

        invokeItemSetClickListenerForHistory(
            fragment,
            historyListView,
            historyList,
            alertDialog,
        )
    }

    private fun invokeItemSetClickListenerForHistory(
        fragment: Fragment,
        historyListView: ListView,
        historyList: List<String>,
        alertDialog: AlertDialog,
    ) {
        historyListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog.dismiss()
            val selectedHistoryFile = historyList
                .get(pos)
                .split("\n")
                .firstOrNull()
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
}


internal val mainMenuGroupId = 30000

internal enum class HistoryMenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    DELETE(mainMenuGroupId, 30100, 1, "delete"),
}

internal fun invokeItemSetLongTimeClickListnerForHistory(
    context: Context,
    historyListView: ListView,
    historyListAdapter: ArrayAdapter<String>,
    historyList: List<String>,
    currentView: View,
    cmdclickAppHistoryDirAdminPath: String,
){
    historyListView.onItemLongClickListener =
        AdapterView.OnItemLongClickListener { parent, listSelectedView, pos, id ->
            val popup = PopupMenu(context, listSelectedView)
            val selectedHistoryFile = historyList.get(pos)
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
                    historyListView,
                    currentView,
                    selectedHistoryFile,
                    cmdclickAppHistoryDirAdminPath,
                    historyListAdapter,
                )
                true
            }
            popup.show()
            true
        }
}


fun execDeleteHistoryFile(
    historyListView: ListView,
    currentView: View,
    selectedHistoryFile: String,
    cmdclickAppHistoryDirAdminPath: String,
    historyListAdapter: ArrayAdapter<String>,
) {
    val selectedDeleteFile =
        selectedHistoryFile
            .split("\n")
            .firstOrNull()
            ?: return
    Toast.makeText(
        currentView.context,
        "${selectedDeleteFile}, ${HistoryMenuEnums.DELETE.itemName}",
        Toast.LENGTH_SHORT
    ).show()

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



internal fun makeHistoryListRow(
    historyRow: String
): String {
    val selectedAppShellFileName = AppHistoryManager
        .getShellFileNameFromAppHistoryFileName(
            historyRow
        )
    if(selectedAppShellFileName != CommandClickShellScript.EMPTY_STRING) {
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


internal fun deleteOverHistory(
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
