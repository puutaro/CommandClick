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
import java.lang.ref.WeakReference

class MultiSelectSpannableJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var gridDialogObj: Dialog? = null

    fun create(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context ?: return String()
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    terminalFragment,
                    title,
                    message,
                    imagePathListNewlineSepaStr
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
        terminalFragment: TerminalFragment,
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String,
    ) {
        val context = terminalFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
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
            terminalFragment,
            imagePathListNewlineSepaStr
        )
        val cancelButton = gridDialogObj?.findViewById<ImageButton>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_cancel
        ) ?: return
        cancelButton.setOnClickListener {
            gridDialogObj?.dismiss()
            gridDialogObj = null
            terminalViewModel.onDialog = false
            returnValue = String()
        }
        val okButton = gridDialogObj?.findViewById<ImageButton>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_ok
        ) ?: return
        okButton.setOnClickListener {
            gridDialogObj?.dismiss()
            gridDialogObj = null
            terminalViewModel.onDialog = false
        }
        gridDialogObj?.setOnCancelListener {
            gridDialogObj?.dismiss()
            gridDialogObj = null
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
        terminalFragment: TerminalFragment,
        imagePathListNewlineSepaStr: String,
    ){
        val imagePathList = makeImagePathList(
            imagePathListNewlineSepaStr
        )
        val multiSelectGridView = gridDialogObj?.findViewById<GridView>(
            com.puutaro.commandclick.R.id.multi_select_spannable_grid_dialog_grid_view
        ) ?: return
        val multiSelectSpannableAdapter = MultiSelectSpannableAdapter(
            WeakReference(terminalFragment.activity),
            terminalFragment.context,
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
                multiSelectSpannableAdapter.selectedItemList.joinToString("\n")
            return@setOnItemClickListener
        }
    }

    private fun makeImagePathList(
        imagePathListNewlineSepaStr: String,
    ): List<String> {
        return imagePathListNewlineSepaStr
            .split("\n")
    }
}
