package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.DpHeightCalculator
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*

class ListJsDialog(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private val searchSwitchThreshold = 5

    fun create(
        title: String,
        message: String,
        listSource: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    message,
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
        title: String,
        message: String,
        listSource: String,
    ) {
        val context = context ?: return

        val dialogListView = ListView(context)
        val dialogList = listSource.split("\t")
        val dialogListAdapter = ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            dialogList,
        )
        dialogListView.adapter = dialogListAdapter
        dialogListView.setSelection(
            dialogListAdapter.count
        )

        val searchText = EditText(context)
        makeSearchEditText(
            dialogListView,
            dialogListAdapter,
            searchText,
            listSource,
        )

        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val dpHeight = DpHeightCalculator.calculate(
            terminalFragment
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

        val addView = if(
            dialogList.size > searchSwitchThreshold
        ) {
            linearLayoutForListView.addView(dialogListView)
            linearLayoutForSearch.addView(searchText)
            linearLayoutForTotal.addView(linearLayoutForListView)
            linearLayoutForTotal.addView(linearLayoutForSearch)
            linearLayoutForTotal
        } else dialogListView

        val titleString = if(title.isNotEmpty()){
            title
        } else "Select bellow list"
        val alertDialog = if(
            message.isNotEmpty()
        ) {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setMessage(message)
                .setView(addView)
                .create()
        } else {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setView(addView)
                .create()
        }
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

    private fun makeSearchEditText(
        dialogListView: ListView,
        dialogListAdapter: ArrayAdapter<String>,
        searchText: EditText,
        listSource: String,
    ) {
        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParamForSearchText.topMargin = 20
        linearLayoutParamForSearchText.bottomMargin = 20
        searchText.layoutParams = linearLayoutParamForSearchText
        searchText.inputType = InputType.TYPE_CLASS_TEXT
        searchText.background = null
        searchText.hint = "search"
        searchText.setPadding(30, 10, 20, 10)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!searchText.hasFocus()) return
                val filterTargetList = listSource.split("\t")
                val filteredList = filterTargetList.filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }

                CommandListManager.execListUpdateByEditText(
                    filteredList,
                    dialogListAdapter,
                    dialogListView
                )
                dialogListView.setSelection(
                    dialogListAdapter.count
                )
            }
        })
    }
}
