package com.puutaro.commandclick.util

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R

object DialogObject {

    private var simpleTextDialogObj: Dialog? = null

    fun simpleTextShow(
        contextSrc: Context?,
        title: String,
        contents: String
    ) {
        val context = contextSrc
            ?: return
        simpleTextDialogObj = Dialog(
                context
            )
        simpleTextDialogObj?.setContentView(
                R.layout.text_simple_dialog_layout
            )
        val titleTextView =
            simpleTextDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_title
            )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else titleTextView?.isVisible = false
        val descriptionTextView =
            simpleTextDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_text_view
            )
        if(
            contents.isNotEmpty()
        ) descriptionTextView?.text = contents
        else descriptionTextView?.isVisible = false

        val cancelImageButton =
            simpleTextDialogObj?.findViewById<AppCompatImageButton>(
                R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            simpleTextDialogObj?.dismiss()
        }
        simpleTextDialogObj?.setOnCancelListener {
            simpleTextDialogObj?.dismiss()
            }
        simpleTextDialogObj?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        simpleTextDialogObj?.window?.setGravity(
                Gravity.BOTTOM
            )
        simpleTextDialogObj?.show()
    }
}