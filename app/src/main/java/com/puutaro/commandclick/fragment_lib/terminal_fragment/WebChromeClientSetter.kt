package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ConfirmDialogForDelete
import com.puutaro.commandclick.util.DialogObject
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog


object WebChromeClientSetter {

    private var alertDialogObj: Dialog? = null
    private var confirmDialogObj: Dialog? = null

    fun set(
        terminalFragment: TerminalFragment,
        webView: WebView,
        progressBar: ProgressBar
    ){

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }

            override fun onShowFileChooser(
                mWebView:WebView,
                filePathCallback:ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ):Boolean {

                if(!terminalFragment.isVisible) return false
                val listener =
                    terminalFragment.context as? TerminalFragment.OnFileChooseListener
                listener?.onFileCoose(
                    filePathCallback,
                    fileChooserParams
                )
                return true
            }


            override fun getDefaultVideoPoster(): Bitmap? {
                return Bitmap.createBitmap(
                    50,
                    50,
                    Bitmap.Config.ARGB_8888
                )
            }

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {
                if(
                    message.isNullOrEmpty()
                ) return true
                val context = terminalFragment.context
                    ?: return true
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
                titleTextView?.text =
                    makeTitle(
                        view,
                        url
                    )
                val descriptionTextView =
                    alertDialogObj?.findViewById<AppCompatTextView>(
                        com.puutaro.commandclick.R.id.text_simple_dialog_text_view
                    )
                descriptionTextView?.text = message

                val cancelImageButton =
                    alertDialogObj?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.text_simple_dialog_cancel
                    )
                cancelImageButton?.setOnClickListener {
                    result.cancel()
                    alertDialogObj?.dismiss()
                }
                alertDialogObj?.setOnCancelListener {
                    result.cancel()
                    alertDialogObj?.dismiss()
                }
                alertDialogObj?.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                alertDialogObj?.window?.setGravity(
                    Gravity.BOTTOM
                )
                alertDialogObj?.show()
                return true
            }


            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {
                if(message.isNullOrEmpty()) return true
                val context = terminalFragment.context
                    ?: return false
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
                confirmTitleTextView?.text =
                    makeTitle(
                        view,
                        url
                    )
                val confirmContentTextView =
                    confirmDialogObj?.findViewById<AppCompatTextView>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
                    )
                confirmContentTextView?.text = message
                val confirmCancelButton =
                    confirmDialogObj?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
                    )
                confirmCancelButton?.setOnClickListener {
                    result.cancel()
                    confirmDialogObj?.dismiss()
                }
                val confirmOkButton =
                    confirmDialogObj?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_ok
                    )
                confirmOkButton?.setOnClickListener {
                    result.confirm()
                    confirmDialogObj?.dismiss()
                }
                confirmDialogObj?.setOnCancelListener {
                    result.cancel()
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
                return true
            }


            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult
            ): Boolean {
                val context = terminalFragment.context
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.setText(defaultValue)
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle(
                        makeTitle(
                            view,
                            url
                        )
                    )
                    .setView(input)
                    .setMessage(message)
                    .setPositiveButton(
                        R.string.ok
                    ) { dialog, which -> result.confirm(input.text.toString()) }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog, which -> result.cancel() }
                    .setOnCancelListener(object : DialogInterface.OnCancelListener {
                        override fun onCancel(dialog: DialogInterface?) {
                            result.cancel()
                        }
                    })
                    .show()
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                    context?.getColor(R.color.black) as Int
                )
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                    context.getColor(R.color.black)
                )
                alertDialog.window?.setGravity(Gravity.BOTTOM)
                return true
            }
        }
    }
}


private fun makeTitle(
    view: WebView?,
    url: String?
): String? {
    val titleEntry = view?.title
    if(
        titleEntry.isNullOrEmpty()
    ) return url
    return titleEntry
}