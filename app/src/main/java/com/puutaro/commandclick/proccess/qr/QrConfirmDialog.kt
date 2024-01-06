package com.puutaro.commandclick.proccess.qr

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.puutaro.commandclick.R


class QrConfirmDialog(
    private val fragment: Fragment,
    private val qrScanDialogObj: Dialog?,
    private val codeScanner: CodeScanner?,
    private val currentAppDirPath: String,
    private val title: String,
    private val body: String,
){
    private val context = fragment.context
    private var confirmDialogObj: Dialog? = null
    private val displayUriTextLimit = 200

    fun launch(
        isMoveCurrentDir: String? = null
    ){
        if(
            context == null
        ) return
        if(
            body.isEmpty()
        ) return

        confirmDialogObj = Dialog(
            context
        )
        confirmDialogObj?.setContentView(
            R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            confirmDialogObj?.findViewById<AppCompatTextView>(
                R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = title
        val confirmContentTextView =
            confirmDialogObj?.findViewById<AppCompatTextView>(
                R.id.confirm_text_dialog_text_view
            )
            confirmContentTextView?.text = body.take(displayUriTextLimit)
        val confirmCancelButton =
            confirmDialogObj?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            codeScanner?.startPreview()
            confirmDialogObj?.dismiss()
        }
        val confirmOkButton =
            confirmDialogObj?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            qrScanDialogObj?.dismiss()
            confirmDialogObj?.dismiss()
            QrUri.handler(
                fragment,
                currentAppDirPath,
                body,
                isMoveCurrentDir
            )
            QrHistoryManager.registerQrUriToHistory(
                currentAppDirPath,
                title,
                body,
            )

        }
        confirmDialogObj?.setOnCancelListener {
            codeScanner?.startPreview()
            confirmDialogObj?.dismiss()
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
}

