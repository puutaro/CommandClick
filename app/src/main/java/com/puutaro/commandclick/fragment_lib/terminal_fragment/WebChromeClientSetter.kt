package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.webkit.*
import android.widget.EditText
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog


object WebChromeClientSetter {

    fun set(
        terminalFragment: TerminalFragment
    ){

        val binding = terminalFragment.binding
        val progressBar = binding.progressBar

        binding.terminalWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE)
                } else {
                    progressBar.setVisibility(View.VISIBLE)
                    progressBar.setProgress(newProgress)
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

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {
                if(message.isNullOrEmpty()) return true
                val context = terminalFragment.context
                val linearLayoutForDialog = LinearLayoutAdderForDialog.add(
                    context,
                    message
                )
                val alertDialog = AlertDialog.Builder(terminalFragment.context)
                    .setTitle(
                        makeTitle(
                            view,
                            url
                        )
                    )
                    .setView(linearLayoutForDialog)
                    .setPositiveButton(
                        R.string.ok
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
                alertDialog.window?.setGravity(Gravity.BOTTOM)
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
                val displayContents = message
                val linearLayoutForDialog = LinearLayoutAdderForDialog.add(
                    context,
                    displayContents
                )
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle(
                        makeTitle(
                            view,
                            url
                        )
                    )
                    .setView(linearLayoutForDialog)
                    .setPositiveButton(
                        R.string.ok
                    ) { dialog, which -> result.confirm() }
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