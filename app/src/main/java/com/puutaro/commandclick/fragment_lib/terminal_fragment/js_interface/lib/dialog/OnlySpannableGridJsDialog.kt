package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.OnlySpannableAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class OnlySpannableGridJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private var alertDialog: AlertDialog? = null

    fun create(
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    message,
                    imagePathListTabSepaStr
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

    private fun createLinearLayoutForGridView(
        imagePathList: List<String>,
    ): LinearLayout {

        val gridView = GridView(context)
        gridView.numColumns = 2

        val onlySpannableAdapter = OnlySpannableAdapter(
            terminalFragment,
            context
        )
        onlySpannableAdapter.addAll(
            imagePathList.toMutableList()
        )
        gridView.adapter = onlySpannableAdapter
        invokeListItemSetClickListenerForListDialog(
            gridView,
            imagePathList.joinToString("\n"),
            terminalViewModel,
        )

        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val searchTextWeight = SearchTextLinearWeight.calculate(terminalFragment)
        val listWeight = 1F - searchTextWeight
        val linearLayoutForListView = NestLinearLayout.make(
            context,
            listWeight
        )
        val linearLayoutForSearch = NestLinearLayout.make(
            context,
            searchTextWeight
        )
        linearLayoutForListView.addView(gridView)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)
        return linearLayoutForTotal
    }

    private fun execCreate(
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ) {
        val imagePathList =
            imagePathListTabSepaStr
                .split("\t")
                .toMutableList()
        val context = context ?: return
        val linearLayoutForGridView = createLinearLayoutForGridView(
            imagePathList,
        )

        val titleString = if(
            title.isNotEmpty()
        ){
            title
        } else "Select bellow list"
        terminalFragment.alertDialogInstance = if(
            message.isNotEmpty()
        ) {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setMessage(message)
                .setView(linearLayoutForGridView)
                .create()
        } else {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setView(linearLayoutForGridView)
                .create()
        }
        alertDialog = terminalFragment.alertDialogInstance
        alertDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        alertDialog?.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.show()
        alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
    }

    private fun invokeListItemSetClickListenerForListDialog(
        gridView: GridView,
        listCon: String,
        terminalViewModel: TerminalViewModel,
    ) {
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog?.dismiss()
            val selectedElement = listCon.split("\n")
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
