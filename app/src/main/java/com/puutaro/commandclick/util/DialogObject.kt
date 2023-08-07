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

    private var simpleTextDialog: Dialog? = null

    fun simpleTextShow(
            contextSrc: Context?,
            title: String,
            content: String
    ) {
        val context = contextSrc
            ?: return
        simpleTextDialog = Dialog(
                context
            )
        simpleTextDialog?.setContentView(
                R.layout.text_simple_dialog_layout
            )
        val titleTextView =
            simpleTextDialog?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_title
            )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else titleTextView?.isVisible = false
        val descriptionTextView =
            simpleTextDialog?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_text_view
            )
        descriptionTextView?.text = content

        val cancelImageButton =
            simpleTextDialog?.findViewById<AppCompatImageButton>(
                R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            simpleTextDialog?.dismiss()
        }
        simpleTextDialog?.setOnCancelListener {
            simpleTextDialog?.dismiss()
            }
        simpleTextDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        simpleTextDialog?.window?.setGravity(
                Gravity.BOTTOM
            )
        simpleTextDialog?.show()

    }
}