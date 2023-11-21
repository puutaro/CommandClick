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
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object DialogObject {

    private var simpleTextDialogObj: Dialog? = null
    private var descWebDialog: Dialog? = null
    private val defaultFontPercentage = 140
    private val positionHashMap = hashMapOf<String, Int>()

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
        positionHashMap.clear()
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
        val descDialogUrl = "${WebUrlVariables.filePrefix}///descMd.txt"
        webView.loadDataWithBaseURL(
            descDialogUrl,
            MarkDownTool.convertMdToHtml(
                scriptName,
                contents
            ),
            "text/html",
            "utf-8",
            descDialogUrl
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
    private fun webViewSetting(
        fragment: androidx.fragment.app.Fragment,
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
    }

    private fun getFontZoomPercentage(
        fragment: androidx.fragment.app.Fragment
    ): Int {
        val context = fragment.context
            ?: return defaultFontPercentage
        val cmdIndexTerminal = TargetFragmentInstance(
        ).getFromFragment<com.puutaro.commandclick.fragment.TerminalFragment>(
            fragment.activity,
            context.getString(R.string.index_terminal_fragment)
        )
        if(
            cmdIndexTerminal != null
            && cmdIndexTerminal.isVisible
        ) return cmdIndexTerminal.fontZoomPercent

        val editExecuteTerminal = TargetFragmentInstance(
        ).getFromFragment<com.puutaro.commandclick.fragment.TerminalFragment>(
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
                positionHashMap.put(
                    "${webView.url}",
                    view?.scrollY ?: 0
                )
                return false
            }

            override fun onPageFinished(
                webview: WebView?,
                url: String?
            ) {
                positionHashMap.get(url)?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO){
                            delay(300)
                        }
                        webView.scrollY = it
                    }
                }
                super.onPageFinished(webview, url)
            }

        }
    }
}