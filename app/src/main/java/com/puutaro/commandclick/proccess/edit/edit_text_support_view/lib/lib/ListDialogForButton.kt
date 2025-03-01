package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ListDialogForButton {

    private val searchSwitchThreshold = 5
    private var alertDialog: AlertDialog? = null


    fun create(
        editFragment: EditFragment,
        listCon: String,
    ) {
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        terminalViewModel.onDialog = true
        terminalViewModel.dialogReturnValue = String()
        execCreate(
            editFragment,
            listCon
        )
    }


    private fun execCreate(
        editFragment: EditFragment,
        listCon: String
    ) {
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val context = editFragment.context
        val fannelList = listCon.split("\n")
        val dialogListView = ListView(context)
        val dialogListAdapter = ArrayAdapter(
            context as Context,
            android.R.layout.simple_list_item_1,
            fannelList,
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
            listCon,
        )

        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val searchTextWeight = SearchTextLinearWeight.calculate(editFragment.activity)
        val listWeight = 1F - searchTextWeight
        val linearLayoutForListView = NestLinearLayout.make(
            context,
            listWeight
        )
        val linearLayoutForSearch = NestLinearLayout.make(
            context,
            searchTextWeight
        )
        linearLayoutForListView.addView(dialogListView)
        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)
        alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle("Select bellow list")
            .setView(linearLayoutForTotal)
            .create()
        alertDialog?.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.show()

        alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                alertDialog?.dismiss()
                alertDialog = null
                terminalViewModel.onDialog = false
            }
        })
        invokeListItemSetClickListenerForListDialog(
            dialogListView,
            listCon,
            searchText,
            terminalViewModel,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView,
        listCon: String,
        searchText: EditText,
        terminalViewModel: TerminalViewModel,
    ) {
        dialogListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog?.dismiss()
            alertDialog = null
            val selectedElement = listCon.split("\n").filter {
                    searchText.text.toString()
                        .lowercase()
                        .replace("\n", "")
                        .contains(
                            it.lowercase()
                        )
            }

                .get(pos)
                .split("\n")
                .firstOrNull()
                ?: return@setOnItemClickListener
            terminalViewModel.onDialog = false
            terminalViewModel.dialogReturnValue = selectedElement
            return@setOnItemClickListener
        }
    }

    private fun makeSearchEditText(
        dialogListView: ListView,
        dialogListAdapter: ArrayAdapter<String>,
        searchText: EditText,
        listCon: String,
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
                val filteredList = listCon.split("\n").filter {
                    searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                            .contains(
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
