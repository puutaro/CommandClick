package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.ImageAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object GridDialogForButton {

    private var gridDialogObj: Dialog? = null

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
            ?: return
        gridDialogObj = Dialog(
            context
        )
        gridDialogObj?.setContentView(
            com.puutaro.commandclick.R.layout.grid_dialog_layout
        )
        val listDialogSearchEditText = gridDialogObj?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.grid_dialog_search_edit_text
        ) ?: return
        listDialogSearchEditText.hint = "search selectable fannel"
        setListView(
            editFragment,
            listCon,
            listDialogSearchEditText,
            terminalViewModel
        )

        gridDialogObj?.setOnCancelListener {
            gridDialogObj?.dismiss()
            gridDialogObj = null
            terminalViewModel.onDialog = false
        }
        gridDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        gridDialogObj?.window?.setGravity(Gravity.BOTTOM)
        gridDialogObj?.show()
    }

    private fun setListView(
        editFragment: EditFragment,
        listCon: String,
        listDialogSearchEditText: AppCompatEditText,
        terminalViewModel: TerminalViewModel,
    ) {
        val context = editFragment.context
            ?: return

        val fannelList =
            makeGridList(listCon)
        val imageAdapter = ImageAdapter(
            context,
        )
        imageAdapter.addAll(
            fannelList.toMutableList()
        )

        val fannelListGridView =
            gridDialogObj?.findViewById<GridView>(
                com.puutaro.commandclick.R.id.grid_dialog_grid_view
            ) ?: return
        val subMenuAdapter = ImageAdapter(
            context,
        )
        subMenuAdapter.clear()
        subMenuAdapter.addAll(
            fannelList.toMutableList()
        )
        fannelListGridView.adapter = subMenuAdapter
        makeSearchEditText(
            fannelListGridView,
            listDialogSearchEditText,
            listCon,
        )
        invokeListItemSetClickListenerForListDialog(
            editFragment,
            fannelListGridView,
            listCon,
            listDialogSearchEditText,
            terminalViewModel,
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        editFragment: EditFragment,
        gridView: GridView,
        listCon: String,
        searchText: EditText,
        terminalViewModel: TerminalViewModel,
    ) {
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            Keyboard.hiddenKeyboardForFragment(
                editFragment
            )
            gridDialogObj?.dismiss()
            gridDialogObj = null
            terminalViewModel.onDialog = false
            val selectedElement =
                makeGridList(listCon).filter {
                    searchText.text.toString()
                        .lowercase()
                        .replace("\n", "")
                        .contains(
                            it.lowercase()
                        )
            }.get(pos)
            terminalViewModel.onDialog = false
            terminalViewModel.dialogReturnValue = selectedElement
            return@setOnItemClickListener
        }
    }

    private fun makeSearchEditText(
        gridView: GridView,
        searchText: AppCompatEditText,
        listCon: String,
    ) {
        val imageAdapter = gridView.adapter as ImageAdapter
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!searchText.hasFocus()) return
                val filteredList =
                    makeGridList(listCon).filter {
                            searchText.text.toString()
                                .lowercase()
                                .replace("\n", "")
                                .contains(
                                    it.lowercase()
                                )
                    }

                imageAdapter.clear()
                imageAdapter.addAll(filteredList.toMutableList())
                imageAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun makeGridList(
        listCon: String
    ): List<String> {
        return listCon
            .split("\n")
            .reversed()
    }
}

