package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*

class ListJsDialog(
    terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()

    fun create(
        listSource: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    listSource
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        listSource: String,
    ) {
        val context = context ?: return
        val dialogListView = ListView(context)
        val dialogList = listSource.split("\t")
        val dialogListAdapter = ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            dialogList
        )
        dialogListView.adapter = dialogListAdapter
        dialogListView.setSelection(dialogListAdapter.count);

        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle("Select bellow list")
            .setView(dialogListView)
            .create()
        alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        alertDialog.show()

        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })

        invokeListItemSetClickListenerForListDialog(
            dialogListView,
            dialogList,
            alertDialog,
        )

    }


    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        dialogList: List<String>,
        alertDialog: AlertDialog,
    ) {

        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog.dismiss()
            val selectedElement = dialogList
                .get(pos)
                .split("\n")
                .firstOrNull()
                ?: return@setOnItemClickListener
            terminalViewModel.onDialog = false
            returnValue = selectedElement
            return@setOnItemClickListener
        }
    }
}
