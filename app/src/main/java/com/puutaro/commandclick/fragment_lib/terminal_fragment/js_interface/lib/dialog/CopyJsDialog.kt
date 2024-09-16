package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import java.lang.ref.WeakReference


object CopyJsDialog {
    private var copyDialogObj: Dialog? = null
    
    
    fun create(
        title: String,
        terminalFragment: TerminalFragment,
        contents: String,
        scrollBottom: Boolean
    ){
        val context = terminalFragment.context
            ?: return
        copyDialogObj = Dialog(
            context
        )
        copyDialogObj?.setContentView(
            R.layout.text_simple_dialog_layout
        )
        val titleTextView =
            copyDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_title
            )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else titleTextView?.isVisible = false
        val descriptionTextView =
            copyDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_text_view
            )
        if(scrollBottom){
            val scrollView =
                copyDialogObj?.findViewById<ScrollView>(
                    R.id.text_simple_dialog_scroll
                )
            scrollView?.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }
        if(
            contents.isNotEmpty()
        ) descriptionTextView?.text = HtmlCompat.fromHtml(contents, FROM_HTML_MODE_COMPACT)
        else descriptionTextView?.isVisible = false

        val cancelImageButton =
            copyDialogObj?.findViewById<AppCompatImageButton>(
                R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setImageResource(androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha)
        cancelImageButton?.setOnClickListener {
            copyToClipboard(
                terminalFragment,
                descriptionTextView,
            )
            copyDialogObj?.dismiss()
            copyDialogObj = null
        }
        copyDialogObj?.setOnCancelListener {
            copyDialogObj?.dismiss()
            copyDialogObj = null
        }
        copyDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        copyDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        copyDialogObj?.show()
    }

    private fun copyToClipboard(
        terminalFragment: TerminalFragment,
        descriptionTextView: AppCompatTextView?,
    ){
        if(descriptionTextView == null) return
        val context = terminalFragment.context
            ?: return
        val selStart = descriptionTextView.selectionStart
        val selEnd: Int = descriptionTextView.selectionEnd
        val min = Math.max(0, Math.min(selStart, selEnd))
        val max = Math.max(0, Math.max(selStart, selEnd))
        val selectedText = descriptionTextView.text.subSequence(min, max).toString()
        if(selectedText.isEmpty()) return
        JsUtil(WeakReference(terminalFragment)).copyToClipboard(selectedText, 12)
        ToastUtils.showShort("copy: ${selectedText}")
    }

}