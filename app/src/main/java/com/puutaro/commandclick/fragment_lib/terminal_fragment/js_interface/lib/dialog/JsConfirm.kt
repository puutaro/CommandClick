package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class JsConfirm(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var confirmDialogObj: Dialog? = null
    private var returnBool = false

    fun create(
        title: String,
        body: String,
    ): Boolean {
        val terminalFragment = terminalFragmentRef.get()
            ?: return false
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        body
                    )
                } catch (e: Exception){
                    Log.e(this.javaClass.name, e.toString())
                }
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
        return returnBool
    }

    private fun execCreate(
        title: String,
        body: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        if(
            context == null
        ) {
            dismissProcess()
            return
        }
        if(
            body.isEmpty()
        ) {
            dismissProcess()
            return
        }
        confirmDialogObj = Dialog(
            context
        )
        confirmDialogObj?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            confirmDialogObj?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        if(
            title.isEmpty()
        ) confirmTitleTextView?.isVisible = false
        else confirmTitleTextView?.text = title
        val confirmContentTextView =
            confirmDialogObj?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = body
        val confirmCancelButton =
            confirmDialogObj?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            dismissProcess()
        }
        val confirmOkButton =
            confirmDialogObj?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            returnBool = true
            terminalViewModel.onDialog = false
            confirmDialogObj?.dismiss()
        }
        confirmDialogObj?.setOnCancelListener {
            dismissProcess()
        }
        confirmDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        confirmDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        confirmDialogObj?.show()
    }

    private fun dismissProcess(){
        returnBool = false
        confirmDialogObj?.dismiss()
        confirmDialogObj = null
        terminalFragmentRef.get()?.let {
            val terminalViewModel: TerminalViewModel by it.activityViewModels()
            terminalViewModel.onDialog = false
        }
    }
}