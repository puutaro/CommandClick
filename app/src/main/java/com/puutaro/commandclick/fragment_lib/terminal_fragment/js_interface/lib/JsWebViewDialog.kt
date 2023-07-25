package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AbsListView
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.MultiSelectOnlyImageAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class jsWebViewDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val webView = makeJsWebView()
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private var alertDialog: AlertDialog? = null

    fun create(
        title: String,
        urlStr: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    urlStr
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
        return returnValue

    }

    private fun createLinearLayoutForGridView(
        urlStr: String
    ): LinearLayout {

        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val linearLayoutForListView = NestLinearLayout.make(
            context,
            1F
        )
        linearLayoutForListView.addView(webView)
        linearLayoutForTotal.addView(linearLayoutForListView)
        webView.loadUrl(urlStr)
        return linearLayoutForTotal
    }

    private fun execCreate(
        title: String,
        urlStr: String
    ) {
        val context = context ?: return

        val titleString = if(
            title.isNotEmpty()
        ){
            title
        } else "Select bellow list"
        terminalFragment.dialogInstance =
            AlertDialog.Builder(
                context,
            )
                .setTitle(titleString)
                .setView(createLinearLayoutForGridView(
                    urlStr
                ))
                .setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.dialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                    returnValue = String()
                })
                .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                    terminalFragment.dialogInstance?.dismiss()
                    terminalViewModel.onDialog = false
                })
                .show()
        alertDialog = terminalFragment.dialogInstance
        alertDialog?.window?.setGravity(Gravity.BOTTOM)
        alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            context.getColor(android.R.color.black)
        )
        alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
                returnValue = String()
            }
        })
    }

    private fun makeJsWebView(
    ): WebView {
        val webView = WebView(context as Context)
        val settings = webView.settings
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(
                webview: WebView?,
                url: String?
            ) {
//                pageLoaded = true
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return view?.url?.startsWith("http") == true
            }
        }
        return webView
    }
}
