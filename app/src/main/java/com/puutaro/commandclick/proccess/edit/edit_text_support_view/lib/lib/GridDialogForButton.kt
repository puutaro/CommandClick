package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.ImageAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object GridDialogForButton {


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
        val gridListView = GridView(context)
        gridListView.numColumns = 2
        val imageAdapter = ImageAdapter(
            context,
        )
        imageAdapter.addAll(
            fannelList.toMutableList()
        )
        gridListView.adapter = imageAdapter

        val searchText = EditText(context)
        makeSearchEditText(
            imageAdapter,
            searchText,
            listCon,
        )

        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val searchTextWeight = SearchTextLinearWeight.calculate(editFragment)
        val listWeight = 1F - searchTextWeight
        val linearLayoutForListView = NestLinearLayout.make(
            context,
            listWeight
        )
        val linearLayoutForSearch = NestLinearLayout.make(
            context,
            searchTextWeight
        )


        linearLayoutForListView.addView(gridListView)
        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)
        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle("Select bellow list")
            .setView(linearLayoutForTotal)
            .create()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(
            context?.getColor(android.R.color.black) as Int
        )
        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            context?.getColor(android.R.color.black) as Int
        )
        alertDialog.show()

        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
        invokeListItemSetClickListenerForListDialog(
            editFragment,
            gridListView,
            listCon,
            searchText,
            alertDialog,
            terminalViewModel,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        editFragment: EditFragment,
        gridView: GridView,
        listCon: String,
        searchText: EditText,
        alertDialog: AlertDialog,
        terminalViewModel: TerminalViewModel,
    ) {
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            Keyboard.hiddenKeyboardForFragment(
                editFragment
            )
            alertDialog.dismiss()
            val selectedElement = listCon.split("\n").filter {
                Regex(
                    searchText.text.toString()
                        .lowercase()
                        .replace("\n", "")
                ).containsMatchIn(
                    it.lowercase()
                )
            }.get(pos)
            terminalViewModel.onDialog = false
            terminalViewModel.dialogReturnValue = selectedElement
            return@setOnItemClickListener
        }
    }

    private fun makeSearchEditText(
        imageAdapter:  ImageAdapter,
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
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }

                imageAdapter.clear()
                imageAdapter.addAll(filteredList.toMutableList())
                imageAdapter.notifyDataSetChanged()
            }
        })
    }
}
