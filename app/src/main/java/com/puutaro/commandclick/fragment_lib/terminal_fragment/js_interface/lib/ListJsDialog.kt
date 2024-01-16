package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*

class ListJsDialog(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private val searchSwitchThreshold = 5
    private var listDialog: Dialog? = null

    companion object {
        val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel
        val iconNameIndex = 1
        val nameIconNameSeparator = "???"
    }

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
        listDialog = Dialog(
            context
        )
        listDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val dialogListView =  listDialog?.findViewById<ListView>(
            com.puutaro.commandclick.R.id.list_dialog_list_view
        )
        val dialogList = makeNameToIconList(listSource)
        val dialogListAdapter = SubMenuAdapter(
            terminalFragment.context as Context,
            dialogList.toMutableList()
        )
        dialogListView?.adapter = dialogListAdapter
        dialogListView?.setSelection(
            dialogListAdapter.count
        )

        val searchText = listDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        makeSearchEditText(
            dialogListView,
            dialogListAdapter,
            searchText,
            listSource,
        )

        if(
            dialogList.size <= searchSwitchThreshold
        ) searchText?.isVisible = false

        val listDialogTitleLinearlayout = listDialog?.findViewById<LinearLayoutCompat>(
            com.puutaro.commandclick.R.id.list_dialog_title_linearlayout
        )
        val titleTextView = listDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        val messageTextView = listDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else listDialogTitleLinearlayout?.isVisible = false
        if(
            message.isNotEmpty()
        ) messageTextView?.text = message
        else messageTextView?.isVisible = false
        val cancelButton =  listDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            listDialog?.dismiss()
            terminalViewModel.onDialog = false
        }
        listDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        listDialog?.window?.setGravity(Gravity.BOTTOM)
        listDialog?.show()

        listDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                listDialog?.dismiss()
                terminalViewModel.onDialog = false
            }
        })

        invokeListItemSetClickListenerForListDialog(
            dialogListView,
        )

    }


    private fun invokeListItemSetClickListenerForListDialog(
        dialogListView: ListView?,
    ) {

        dialogListView?.setOnItemClickListener {
                parent, View, pos, id
            ->
            listDialog?.dismiss()
            val menuListAdapter = dialogListView.adapter as SubMenuAdapter
            val selectedElement =
                menuListAdapter.getItem(pos)
                    ?: return@setOnItemClickListener
            terminalViewModel.onDialog = false
            returnValue = selectedElement
            return@setOnItemClickListener
        }
    }

    private fun makeSearchEditText(
        dialogListViewSrc: ListView?,
        dialogListAdapter: ArrayAdapter<String>,
        searchText: AppCompatEditText?,
        listSource: String,
    ) {
        val dialogListView = dialogListViewSrc
            ?: return
        searchText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (
                    !searchText.hasFocus()
                ) return
                val filterTargetList = makeNameToIconList(listSource)
                val filteredList = filterTargetList.filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.first.lowercase()
                    )
                }
                updateList(
                    dialogListView,
                    filteredList
                )
                dialogListView.setSelection(
                    dialogListAdapter.count
                )
            }
        })
    }
    private fun updateList(
        dialogListView: ListView?,
        filteredList: List<Pair<String, Int>>
    ){
        val menuListAdapter = dialogListView?.adapter as SubMenuAdapter
        menuListAdapter.clear()
        menuListAdapter.addAll(filteredList)
        menuListAdapter.notifyDataSetChanged()
    }

    private fun makeNameToIconList(
        listSource: String
    ): List<Pair<String, Int>> {
        return listSource.split("\t").filter{
            it.trim().isNotEmpty()
        }.map {
            val nameIconNameList = it.split(nameIconNameSeparator)
            val itemName = nameIconNameList.first().trim()
            val iconId = nameIconNameList.getOrNull(iconNameIndex).let {
                    getIconNameSrc ->
                val getIconName = getIconNameSrc?.trim()
                if(
                    getIconName.isNullOrEmpty()
                ) return@let icons8Wheel
                CmdClickIcons.values().filter {
                    it.str == getIconName
                }.firstOrNull()?.id ?: icons8Wheel
            }
            itemName to iconId
        }
    }
}
