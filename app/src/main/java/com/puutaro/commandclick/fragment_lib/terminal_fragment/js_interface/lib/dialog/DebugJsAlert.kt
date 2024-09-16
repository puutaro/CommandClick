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

class DebugJsAlert(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var alertDialogObj: Dialog? = null

    fun create(
        title: String,
        body: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context
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
        return body
    }

    private fun execCreate(
        title: String,
        body: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
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
        alertDialogObj = Dialog(
            context
        )
        alertDialogObj?.setContentView(
            com.puutaro.commandclick.R.layout.text_simple_dialog_layout
        )
        val titleTextView =
            alertDialogObj?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.text_simple_dialog_title
            )
        when(title.isEmpty()){
            true -> titleTextView?.isVisible = false
            else -> titleTextView?.text = title
        }
        val descriptionTextView =
            alertDialogObj?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.text_simple_dialog_text_view
            )
        descriptionTextView?.text = body

        val cancelImageButton =
            alertDialogObj?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            dismissProcess()
        }
        alertDialogObj?.setOnCancelListener {
            dismissProcess()
        }
        alertDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        alertDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        alertDialogObj?.show()
    }

    private fun dismissProcess(){
        terminalFragmentRef.get()?.let {
            val terminalViewModel: TerminalViewModel by it.activityViewModels()
            terminalViewModel.onDialog = false
        }
        alertDialogObj?.dismiss()
        alertDialogObj = null
    }
}