package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.OnlyImageAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class OnlyImageGridJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var onlyImageGridDialog: AlertDialog? = null

    fun create(
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    terminalFragment,
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
        terminalFragment: TerminalFragment,
        imagePathList: List<String>,
    ): LinearLayout {
        val context = terminalFragment.context
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val gridView = GridView(context)
        gridView.numColumns = 2

        val onlyImageAdapter = OnlyImageAdapter(context)
        onlyImageAdapter.addAll(
            imagePathList.toMutableList()
        )
        gridView.adapter = onlyImageAdapter
        invokeListItemSetClickListenerForListDialog(
            gridView,
            imagePathList.joinToString("\n"),
            terminalViewModel,
        )

        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val searchTextWeight = SearchTextLinearWeight.calculate(terminalFragment.activity)
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
        terminalFragment: TerminalFragment,
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ) {
        val imagePathList =
            imagePathListTabSepaStr
                .split("\n")
                .toMutableList()
        val context = terminalFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val linearLayoutForGridView = createLinearLayoutForGridView(
            terminalFragment,
            imagePathList,
        )

        val titleString = if(
            title.isNotEmpty()
        ){
            title
        } else "Select bellow list"
        onlyImageGridDialog = if(
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
        onlyImageGridDialog?.getButton(
            DialogInterface.BUTTON_POSITIVE
        )?.setTextColor(
            context.getColor(
                android.R.color.black
            ) as Int
        )
        onlyImageGridDialog?.getButton(
            DialogInterface.BUTTON_NEGATIVE
        )?.setTextColor(
            context.getColor(
                android.R.color.black
            ) as Int
        )
        onlyImageGridDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        onlyImageGridDialog?.window?.setGravity(Gravity.BOTTOM)
        onlyImageGridDialog?.show()
        onlyImageGridDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
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
            onlyImageGridDialog?.dismiss()
            onlyImageGridDialog = null
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
