package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PromptJsDialog(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private var promptDialogObj: Dialog? = null

    fun create(
        title: String,
        message: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    message,
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !terminalViewModel.onDialog
                    ) break
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        title: String,
        message: String,
    ) {
        val context = context
            ?: return

        promptDialogObj = Dialog(
            context
        )
        promptDialogObj?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialogObj?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        if(
            title.isNotEmpty()
        ) promptTitleTextView?.text = title
        else promptTitleTextView?.isVisible = false

        val promptMessageTextView =
            promptDialogObj?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        if(
            message.isNotEmpty()
        ) promptMessageTextView?.text = message
        else promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialogObj?.findViewById<AppCompatEditText>(
                R.id.prompt_dialog_input
            )
        val promptCancelButton =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            terminalViewModel.onDialog = false

        }
        val promptOkButtonView =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                returnValue = String()
                promptDialogObj?.dismiss()
                terminalViewModel.onDialog = false
                return@setOnClickListener
            }
            else returnValue = inputEditable.toString()
            promptDialogObj?.dismiss()
            terminalViewModel.onDialog = false
        }
        promptDialogObj?.setOnCancelListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            terminalViewModel.onDialog = false
        }
        promptDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialogObj?.show()
    }
}
