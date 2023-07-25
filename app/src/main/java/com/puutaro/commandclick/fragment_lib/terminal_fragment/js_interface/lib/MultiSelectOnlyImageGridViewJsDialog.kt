package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.AbsListView
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.MultiSelectOnlyImageAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MultiSelectOnlyImageGridViewJsDialog(
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
        gridView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE

        val myImageAdapter = MultiSelectOnlyImageAdapter(context)
        myImageAdapter.addAll(
            imagePathList.toMutableList()
        )
        gridView.adapter = myImageAdapter
        invokeListItemSetClickListenerForListDialog(
            gridView,
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
                .setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.alertDialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                    returnValue = String()
                })
                .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.alertDialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                })
                .show()
        } else {
            AlertDialog.Builder(
                context,
            )
                .setTitle(titleString)
                .setView(linearLayoutForGridView)
                .setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.alertDialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                    returnValue = String()
                })
                .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.alertDialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                })
                .show()
        }
        alertDialog = terminalFragment.alertDialogInstance
        alertDialog?.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
                returnValue = String()
            }
        })
    }

    private fun invokeListItemSetClickListenerForListDialog(
        gridView: GridView,
    ) {
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val multiSelectOnlyImageAdapter =
                gridView.adapter as MultiSelectOnlyImageAdapter
            multiSelectOnlyImageAdapter.onItemSelect(
                View,
                pos
            )
            returnValue =
                multiSelectOnlyImageAdapter.selectedItemList.joinToString("\t")
            return@setOnItemClickListener
        }
    }
}
