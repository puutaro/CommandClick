package com.puutaro.commandclick.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.JsInterfaceVariant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object DialogObject {

    private var simpleTextDialogObj: Dialog? = null
    private var descWebDialog: Dialog? = null
    private var termCopyWebDialog: Dialog? = null
    private val defaultFontPercentage = 140
    private val initScrollTermRange = 1000000000

    fun simpleTextShow(
        contextSrc: Context?,
        title: String,
        contents: String,
        scrollBottom: Boolean = false
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
        if(scrollBottom){
            val scrollView =
                simpleTextDialogObj?.findViewById<ScrollView>(
                    R.id.text_simple_dialog_scroll
                )
            scrollView?.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }
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

    fun descDialog(
        fragment: Fragment,
        scriptName: String,
        contents: String
    ){
        val context = fragment.context
            ?: return
        val activity = fragment.activity
            ?: return
        descWebDialog = Dialog(
            context
        )
        descWebDialog?.setContentView(
            R.layout.markdown_simple_dialog_layout
        )
        val textView = descWebDialog?.findViewById<AppCompatTextView>(
            R.id.desc_dialog_title
        ) ?: return
        textView.isVisible = false
        val webView = descWebDialog?.findViewById<WebView>(
            R.id.desc_dialog_webview
        ) ?: return
        webViewSetting(
            fragment,
            webView
        )
        webView.loadDataWithBaseURL(
            "",
            MarkDownTool.convertMdToHtml(
                scriptName,
                contents
            ),
            "text/html",
            "utf-8",
            null
        )
        val progressBar = descWebDialog?.findViewById<ProgressBar>(
            R.id.desc_dialog_webview_progressBar
        )
        val webViewCancelBtn = descWebDialog?.findViewById<ImageButton>(
            R.id.desc_webview_dialog_cancel
        ) ?: return
        webViewCancelBtn.setOnClickListener {
            descWebDialog?.dismiss()
        }
        setProgressChanged(
            webView,
            progressBar,
        )
        setWebViewClient(
            webView,
            activity,
        )
        val webViewBackBtn = descWebDialog?.findViewById<ImageButton>(
            R.id.desc_webview_dialog_back
        ) ?: return
        webViewBackBtn.setOnClickListener {
            if(
                !webView.canGoBack()
            ) return@setOnClickListener
            webView.goBack()
        }
        webViewBackBtn.setOnLongClickListener {
            descWebDialog?.dismiss()
            true
        }
        descWebDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        descWebDialog?.show()

        descWebDialog?.setOnCancelListener {
            descWebDialog?.dismiss()
        }
    }

    fun termStrCopyDialog(
        fragment: Fragment,
        contents: String,
//        srcFilePath: String,
    ){
//        val srcFileObj = File(srcFilePath)
//        if(!srcFileObj.isFile) return
//        val srcDirPath = srcFileObj.parent
//            ?: return
//        val srcName = srcFileObj.name
//        val contents = ReadText(
//            srcDirPath,
//            srcName,
//        ).readText()
        val context = fragment.context
            ?: return
        val activity = fragment.activity
            ?: return
        termCopyWebDialog?.dismiss()
        termCopyWebDialog = Dialog(
            context
        )
        termCopyWebDialog?.setContentView(
            R.layout.term_copy_dialog_layout
        )
        val textView = termCopyWebDialog?.findViewById<AppCompatTextView>(
            R.id.term_copy_dialog_title
        ) ?: return
        textView.isVisible = false
        val webView = termCopyWebDialog?.findViewById<WebView>(
            R.id.term_copy_dialog_webview
        ) ?: return
        webViewSetting(
            fragment,
            webView
        )
        webView.loadDataWithBaseURL(
            "",
            AsciiTool.convertTermStrToHtml(contents),
            "text/html",
            "utf-8",
            null
        )
        val progressBar = termCopyWebDialog?.findViewById<ProgressBar>(
            R.id.term_copy_dialog_webview_progressBar
        )

        setProgressChanged(
            webView,
            progressBar,
        )

        setWebViewClient(
            webView,
            activity,
        )
        val termCopyBtn = termCopyWebDialog?.findViewById<ImageButton>(
            R.id.term_copy_webview_dialog_copy
        ) ?: return
        termCopyBtn.setOnClickListener {
            val jsContents = AssetsFileManager.readFromAssets(
                context,
                AssetsFileManager.assetsHighlightCopy
            ).split("\n")
            val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(jsContents)
                ?: return@setOnClickListener
            webView.loadUrl(jsScriptUrl)
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO){
                    delay(200)
                }
                withContext(Dispatchers.Main){
                    termCopyWebDialog?.dismiss()
                }
            }
        }
        termCopyBtn.setOnLongClickListener {
            termCopyWebDialog?.dismiss()
            true
        }
        termCopyWebDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        termCopyWebDialog?.show()

        termCopyWebDialog?.setOnCancelListener {
            it.dismiss()
            termCopyWebDialog?.dismiss()
        }
    }

    private fun webViewSetting(
        fragment: Fragment,
        webView: WebView
    ){
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        val terminalFontPercentage = getFontZoomPercentage(
            fragment
        )
        settings.textZoom = (terminalFontPercentage * 95 ) / 100
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.addJavascriptInterface(
            JsToast(fragment),
            JsInterfaceVariant.jsToast.name
        )
        webView.addJavascriptInterface(
            JsUtil(fragment),
            JsInterfaceVariant.jsUtil.name
        )
    }

    private fun getFontZoomPercentage(
        fragment: Fragment
    ): Int {
        val context = fragment.context
            ?: return defaultFontPercentage
        val cmdIndexTerminal = TargetFragmentInstance(
        ).getFromFragment<TerminalFragment>(
            fragment.activity,
            context.getString(R.string.index_terminal_fragment)
        )
        if(
            cmdIndexTerminal != null
            && cmdIndexTerminal.isVisible
        ) return cmdIndexTerminal.fontZoomPercent

        val editExecuteTerminal = TargetFragmentInstance(
        ).getFromFragment<TerminalFragment>(
            fragment.activity,
            context.getString(R.string.edit_execute_terminal_fragment)
        )
        if(
            editExecuteTerminal != null
            && editExecuteTerminal.isVisible
        ) return editExecuteTerminal.fontZoomPercent

        return defaultFontPercentage
    }

    private fun setProgressChanged(
        webView: WebView,
        progressBar: ProgressBar?,
    ){
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar?.visibility = View.GONE
                } else {
                    progressBar?.visibility = View.VISIBLE
                    progressBar?.progress = newProgress
                }
            }
        }
    }

    private fun setWebViewClient(
        webView: WebView,
        activity: FragmentActivity,
    ){
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (
                    request?.url?.scheme.equals("intent")
                    || request?.url?.scheme.equals("android-app")
                ) {
                    val intent = Intent.parseUri(request?.url.toString(), Intent.URI_INTENT_SCHEME)
                    val packageManager = activity.packageManager
                    if (
                        packageManager != null && intent?.resolveActivity(
                            packageManager
                        ) != null
                    ) {
                        activity.startActivity(intent)
                        return true
                    }

                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String?) {
                //use the param "view", and call getContentHeight in scrollTo
                view.scrollTo(0, initScrollTermRange)
            }
        }
    }
}