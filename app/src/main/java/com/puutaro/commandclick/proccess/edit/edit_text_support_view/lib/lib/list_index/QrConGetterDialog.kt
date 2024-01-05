package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrScanner
import java.io.File

object QrConGetterDialog {

    private var qrconGetterDialog: Dialog? = null
    fun launch(
        editFragment: EditFragment,
        filterDir: String,
    ){
        val context = editFragment.context
            ?: return
        val activity = editFragment.activity
            ?: return
        qrconGetterDialog = Dialog(
            activity
        )
        qrconGetterDialog?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            qrconGetterDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input contents name"
        val promptMessageTextView =
            qrconGetterDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            qrconGetterDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        val promptCancelButton =
            qrconGetterDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            qrconGetterDialog?.dismiss()
        }
        val promptOkButtonView =
            qrconGetterDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            qrconGetterDialog?.dismiss()
            val shortcutNameEditable = promptEditText?.text
            if(
                shortcutNameEditable.isNullOrEmpty()
            ) return@setOnClickListener
            val fileName = UsePath.compExtend(
                shortcutNameEditable.toString(),
                ".txt",
            )
            val filePath = "${filterDir}/${fileName}"
            if(File(filePath).isFile) {
                Toast.makeText(
                    context,
                    "Already exist: ${filePath}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            getQrHandler(
                editFragment,
                filterDir,
                fileName,
            )
        }
        qrconGetterDialog?.setOnCancelListener {
            qrconGetterDialog?.dismiss()
        }
        qrconGetterDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        qrconGetterDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        qrconGetterDialog?.show()
    }

    private fun getQrHandler(
        editFragment: EditFragment,
        filterDir: String,
        fileName: String,
    ){
        QrScanner(
            editFragment,
            filterDir
        ).saveFromCamera(fileName)
    }
}