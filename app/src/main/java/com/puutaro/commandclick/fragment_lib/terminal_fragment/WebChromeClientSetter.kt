package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object WebChromeClientSetter {

    private var alertDialogObj: Dialog? = null
    private var confirmDialogObj: Dialog? = null
    private var promptDialogObj: Dialog? = null

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

            override fun onPermissionRequest(request: PermissionRequest) {
                // UIスレッドから別スレッドに切り離して処理をしてその結果をViewで表示
                CoroutineScope(Dispatchers.Main).launch {
                    // Android 5.0 以上のバージョンの処理
                    // WebViewに必要なアクセス権限を設定
                    val PERMISSIONS = arrayOf(
                        // マイクなどのオーディオキャプチャデバイス
                        PermissionRequest.RESOURCE_AUDIO_CAPTURE,
                        // カメラなどのビデオキャプチャデバイス
                        PermissionRequest.RESOURCE_VIDEO_CAPTURE
                    )

                    /**
                     * 指定されたリソースにアクセスする許可を origin に与えます。
                     * 付与されたアクセス許可は、この WebView に対してのみ有効
                     */
                    request.grant(PERMISSIONS)
                }
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
                    ?: return true

                promptDialogObj = Dialog(
                    context
                )
                promptDialogObj?.setContentView(
                    com.puutaro.commandclick.R.layout.prompt_dialog_layout
                )
                val promptTitleTextView =
                    promptDialogObj?.findViewById<AppCompatTextView>(
                        com.puutaro.commandclick.R.id.prompt_dialog_title
                    )
                promptTitleTextView?.text =
                    makeTitle(
                        view,
                        url
                    )
                val promptMessageTextView =
                    promptDialogObj?.findViewById<AppCompatTextView>(
                        com.puutaro.commandclick.R.id.prompt_dialog_message
                    )
                promptMessageTextView?.text = message
                val promptEditText =
                    promptDialogObj?.findViewById<AutoCompleteTextView>(
                        com.puutaro.commandclick.R.id.prompt_dialog_input
                    )
                val promptCancelButton =
                    promptDialogObj?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.prompt_dialog_cancel
                    )
                promptCancelButton?.setOnClickListener {
                    promptDialogObj?.dismiss()
                    result.cancel()
                }
                val promptOkButtonView =
                    promptDialogObj?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.prompt_dialog_ok
                    )
                promptOkButtonView?.setOnClickListener {
                    promptDialogObj?.dismiss()
                    val inputEditable = promptEditText?.text
                    if(
                        inputEditable.isNullOrEmpty()
                    ) result.cancel()
                    else result.confirm(
                        inputEditable.toString()
                    )
                }
                promptDialogObj?.setOnCancelListener {
                    promptDialogObj?.dismiss()
                    result.cancel()
                }
                promptDialogObj?.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                promptDialogObj?.window?.setGravity(
                    Gravity.BOTTOM
                )
                promptDialogObj?.show()
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