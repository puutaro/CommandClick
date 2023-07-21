package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.MultiSelectSpannableAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MultiSelectSpannableJsDialog(
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

        val myImageAdapter = MultiSelectSpannableAdapter(
            terminalFragment,
            context
        )
        myImageAdapter.addAll(
            imagePathList.toMutableList()
        )
        gridView.adapter = myImageAdapter
//        val searchText = EditText(context)
//        makeSearchEditText(
//            myImageAdapter,
//            searchText,
//            imagePathList.joinToString("\n"),
//        )
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
//        linearLayoutForSearch.addView(searchText)
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
        terminalFragment.dialogInstance = if(
            message.isNotEmpty()
        ) {
            AlertDialog.Builder(
                context
            )
                .setTitle(titleString)
                .setMessage(message)
                .setView(linearLayoutForGridView)
                .setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.dialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                    returnValue = String()
                })
                .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.dialogInstance?.dismiss()
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
                    terminalFragment.dialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                    returnValue = String()
                })
                .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.dialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                })
                .show()
        }
        alertDialog = terminalFragment.dialogInstance
        alertDialog?.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
//        alertDialog?.show()
        alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
                returnValue = String()
            }
        })
    }

    private fun makeSearchEditText(
        imageAdapter: MultiSelectSpannableAdapter,
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

    private fun invokeListItemSetClickListenerForListDialog(
        gridView: GridView,
    ) {
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val multiSelectSpannableAdapter = gridView.adapter as MultiSelectSpannableAdapter
            multiSelectSpannableAdapter.onItemSelect(
                View,
                pos
            )
            returnValue = multiSelectSpannableAdapter.selectedItemList.joinToString("\t")
            return@setOnItemClickListener
        }
    }
}
