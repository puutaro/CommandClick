package com.puutaro.commandclick.proccess.qr

import android.app.Dialog
import android.webkit.ValueCallback
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object QrConfirmDialog {
//    private val context = fragment.context
    private var confirmDialogObj: Dialog? = null
    private val displayUriTextLimit = 200

    fun launch(
        fragment: Fragment,
        codeScanner: CodeScanner?,
//    private val currentAppDirPath: String,
        title: String,
        body: String,
        isMoveCurrentDir: String? = null
    ){
        val context = fragment.context
            ?: return
        if(
            body.isEmpty()
        ) return

        val terminalFragment = when(fragment){
            is TerminalFragment -> fragment
            else -> TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment.activity,
            )
        } ?: return
        val confirmScript = """
                jsDialog.confirm(
                    "$title",
                    "${body.take(displayUriTextLimit)}",
                );
            """.trimIndent()
        terminalFragment.binding.terminalWebView.evaluateJavascript(
            confirmScript,
            ValueCallback<String> { isDelete ->
                when(isDelete){
                    true.toString() -> {
                        QrScanner.qrDialogDismiss()
                        confirmDialogObj?.dismiss()
                        confirmDialogObj = null
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.Main) {
                                QrUriHandler.handle(
                                    fragment,
//                        currentAppDirPath,
                                    body,
                                    isMoveCurrentDir
                                )
                            }
                        }
                        QrHistoryManager.registerQrUriToHistory(
//                currentAppDirPath,
                            title,
                            body,
                        )
                    }
                    else -> {
                        codeScanner?.startPreview()
                        confirmDialogObj?.dismiss()
                        confirmDialogObj = null
                    }
                }
            })

//        confirmDialogObj = Dialog(
//            context
//        )
//        confirmDialogObj?.setContentView(
//            R.layout.confirm_text_dialog
//        )
//        val confirmTitleTextView =
//            confirmDialogObj?.findViewById<AppCompatTextView>(
//                R.id.confirm_text_dialog_title
//            )
//        confirmTitleTextView?.text = title
//        val confirmContentTextView =
//            confirmDialogObj?.findViewById<AppCompatTextView>(
//                R.id.confirm_text_dialog_text_view
//            )
//            confirmContentTextView?.text = body.take(displayUriTextLimit)
//        val confirmCancelButton =
//            confirmDialogObj?.findViewById<AppCompatImageButton>(
//                R.id.confirm_text_dialog_cancel
//            )
//        confirmCancelButton?.setOnClickListener {
//            codeScanner?.startPreview()
//            confirmDialogObj?.dismiss()
//            confirmDialogObj = null
//        }
//        val confirmOkButton =
//            confirmDialogObj?.findViewById<AppCompatImageButton>(
//                R.id.confirm_text_dialog_ok
//            )
//        confirmOkButton?.setOnClickListener {
//            QrScanner.qrDialogDismiss()
//            confirmDialogObj?.dismiss()
//            confirmDialogObj = null
//            CoroutineScope(Dispatchers.Main).launch {
//                withContext(Dispatchers.Main) {
//                    QrUriHandler.handle(
//                        fragment,
////                        currentAppDirPath,
//                        body,
//                        isMoveCurrentDir
//                    )
//                }
//            }
//            QrHistoryManager.registerQrUriToHistory(
////                currentAppDirPath,
//                title,
//                body,
//            )
//
//        }
//        confirmDialogObj?.setOnCancelListener {
//            codeScanner?.startPreview()
//            confirmDialogObj?.dismiss()
//            confirmDialogObj = null
//        }
//        confirmDialogObj?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        confirmDialogObj?.window?.setGravity(
//            Gravity.BOTTOM
//        )
//        confirmDialogObj?.show()
    }
}

