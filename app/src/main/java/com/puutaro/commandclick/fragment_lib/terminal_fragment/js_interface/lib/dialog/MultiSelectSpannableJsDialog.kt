package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.component.adapter.MultiSelectSpannableAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
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
    private var gridDialogObj: Dialog? = null

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

    private fun execCreate(
        title: String,
        message: String,
        imagePathListTabSepaStr: String,
    ) {

        if(
            context == null
        ) return
        gridDialogObj = Dialog(
            context
        )
        gridDialogObj?.setContentView(
            com.puutaro.commandclick.R.layout.multi_select_spannable_grid_dialog_layout
        )
        val titleTextView = gridDialogObj?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_title
        ) ?: return
        if(
            title.isNotEmpty()
        ) titleTextView.text = title
        else titleTextView.isVisible = false
        val messageTextView = gridDialogObj?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_message
        ) ?: return
        if(
            message.isNotEmpty()
        ) messageTextView.text = message
        else messageTextView.isVisible = false
        setGridView(
            imagePathListTabSepaStr
        )
        val cancelButton = gridDialogObj?.findViewById<ImageButton>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_cancel
        ) ?: return
        cancelButton.setOnClickListener {
            gridDialogObj?.dismiss()
            terminalViewModel.onDialog = false
            returnValue = String()
        }
        val okButton = gridDialogObj?.findViewById<ImageButton>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_ok
        ) ?: return
        okButton.setOnClickListener {
            gridDialogObj?.dismiss()
            terminalViewModel.onDialog = false
        }
        gridDialogObj?.setOnCancelListener {
            gridDialogObj?.dismiss()
            terminalViewModel.onDialog = false
            returnValue = String()
        }
        gridDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        gridDialogObj?.window?.setGravity(Gravity.BOTTOM)
        gridDialogObj?.show()
    }

    private fun setGridView(
        imagePathListTabSepaStr: String,
    ){
        val imagePathList = makeImagePathList(
            imagePathListTabSepaStr
        )
        val multiSelectGridView = gridDialogObj?.findViewById<GridView>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_grid_view
        ) ?: return
        val multiSelectSpannableAdapter = MultiSelectSpannableAdapter(
            terminalFragment,
            context
        )
        multiSelectSpannableAdapter.clear()
        multiSelectSpannableAdapter.addAll(
            imagePathList.toMutableList()
        )
        multiSelectGridView.adapter = multiSelectSpannableAdapter
        invokeListItemSetClickListenerForListDialog(
            multiSelectGridView
        )
    }

    private fun invokeListItemSetClickListenerForListDialog(
        gridView: GridView,
    ) {
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val multiSelectSpannableAdapter =
                gridView.adapter as MultiSelectSpannableAdapter
            multiSelectSpannableAdapter.onItemSelect(
                View,
                pos
            )
            returnValue =
                multiSelectSpannableAdapter.selectedItemList.joinToString("\t")
            return@setOnItemClickListener
        }
    }

    private fun makeImagePathList(
        imagePathListTabSepaStr: String,
    ): List<String> {
        return imagePathListTabSepaStr
            .split("\t")
    }
}
