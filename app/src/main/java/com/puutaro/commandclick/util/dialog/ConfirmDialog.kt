package com.puutaro.commandclick.util.dialog

import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object ConfirmWaitDialog {

    private var getPermissionConfirmDialog: Dialog? = null
    private var onDialog = false
    private var isOk = false

    fun launch(
        fragment: Fragment,
        title: String,
        message: String,
    ): Boolean {
        isOk = false
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execLaunch(
                        fragment,
                        title,
                        message
                    )
                } catch (e: Exception){
                    Log.e(this.javaClass.name, e.toString())
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return isOk
    }

    fun execLaunch(
        fragment: Fragment,
        title: String,
        message: String,
    ){
        val context = fragment.context
            ?: return
        getPermissionConfirmDialog = Dialog(
            context
        )
        getPermissionConfirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            getPermissionConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = title
        val confirmContentTextView =
            getPermissionConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        when(message.isEmpty()) {
            true -> confirmContentTextView?.isVisible = false
            else -> confirmContentTextView?.text = message
        }
        val confirmCancelButton =
            getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            getPermissionConfirmDialog?.dismiss()
            getPermissionConfirmDialog = null
        }
        val confirmOkButton =
            getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            getPermissionConfirmDialog?.dismiss()
            getPermissionConfirmDialog = null
            isOk = true
        }
        getPermissionConfirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        getPermissionConfirmDialog?.window?.setGravity(
            Gravity.CENTER
        )
        getPermissionConfirmDialog?.show()
    }
}

