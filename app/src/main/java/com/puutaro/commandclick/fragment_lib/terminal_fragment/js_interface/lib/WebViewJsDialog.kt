package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.DialogJsInterfaceVariant
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.JsInterfaceVariant
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WebViewJsDialog(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val activity = terminalFragment.activity

    fun create(
        urlStrSrc: String
    ) {
        val urlStr = if(
            urlStrSrc.startsWith(WebUrlVariables.slashPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.filePrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpsPrefix)
        ) urlStrSrc
        else "${WebUrlVariables.queryUrl}${urlStrSrc}"
        CoroutineScope(Dispatchers.Main).launch{
            terminalFragment.dialogInstance = Dialog(
                context as Context
            )
            terminalFragment.dialogInstance?.setContentView(
                com.puutaro.commandclick.R.layout.dialog_webview_layout
            )
            val webView = terminalFragment.dialogInstance?.findViewById<WebView>(
                com.puutaro.commandclick.R.id.webview_dialog_webview
            ) ?: return@launch
            webViewSetting(webView)
            webView.loadUrl(
                urlStr
            )
            val progressBar = terminalFragment.dialogInstance?.findViewById<ProgressBar>(
                com.puutaro.commandclick.R.id.dialog_webview_progressBar
            )
            val webViewSearchBtn = terminalFragment.dialogInstance?.findViewById<ImageButton>(
                com.puutaro.commandclick.R.id.webview_dialog_search
            )
            val webViewLaunchLocalBtn = terminalFragment.dialogInstance?.findViewById<ImageButton>(
                com.puutaro.commandclick.R.id.webview_dialog_launch_local
            )
            val webViewBackBtn = terminalFragment.dialogInstance?.findViewById<ImageButton>(
                com.puutaro.commandclick.R.id.webview_dialog_back
            )
            terminalFragment.dialogInstance?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            terminalFragment.dialogInstance?.show()

            terminalFragment.dialogInstance?.setOnCancelListener {
                terminalFragment.dialogInstance?.dismiss()
            }
            webViewSearchBtnSetClickListener(
                webView,
                webViewSearchBtn
            )
            webViewLaunchLocalBtnSetClickListener(
                webView,
                webViewLaunchLocalBtn
            )
            webViewBackBtnSetClickListner(
                webView,
                webViewBackBtn,
            )
            webViewClientSetter(webView)
            WebChromeClientSetter.set(
                terminalFragment,
                webView,
                progressBar as ProgressBar
            )
        }
    }

    private fun webViewSetting(
        webView: WebView
    ){
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.textZoom = (terminalFragment.fontZoomPercent * 95 ) / 100
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.addJavascriptInterface(
            JsToast(terminalFragment),
            JsInterfaceVariant.jsToast.name
        )
        webView.addJavascriptInterface(
            JsFileSystem(terminalFragment),
            JsInterfaceVariant.jsFileSystem.name
        )
        webView.addJavascriptInterface(
            JsUrl(terminalFragment),
            JsInterfaceVariant.jsUrl.name
        )
        webView.addJavascriptInterface(
            JsWebViewDialogManager(terminalFragment),
            DialogJsInterfaceVariant.jsWebViewDialogManager.name
        )
    }

    private fun webViewSearchBtnSetClickListener(
        webView: WebView,
        webViewSearchBtn: ImageButton?
    ){
        webViewSearchBtn?.setOnClickListener{
            val jsContents = AssetsFileManager.readFromAssets(
                it.context,
                AssetsFileManager.assetsHighlightSchForDialogWebViewPath
            ).split("\n")
            val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(jsContents)
                ?: return@setOnClickListener
            webView.loadUrl(jsScriptUrl)
        }
    }

    private fun webViewLaunchLocalBtnSetClickListener(
        webView: WebView,
        webViewLaunchLocalBtn: ImageButton?
    ){
        webViewLaunchLocalBtn?.setOnClickListener {
            val currentUrl = webView.url
                ?: return@setOnClickListener
            terminalFragment.dialogInstance?.dismiss()
            terminalFragment.binding.terminalWebView.loadUrl(currentUrl)
        }
    }

    private fun webViewBackBtnSetClickListner(
        webView: WebView,
        webViewBackBtn: ImageButton?,
    ){
        webViewBackBtn?.setOnClickListener{
            if(
                !webView.canGoBack()
            ) return@setOnClickListener
            webView.goBack()
        }
        webViewBackBtn?.setOnLongClickListener{
            terminalFragment.dialogInstance?.dismiss()
            true
        }
    }

    private fun webViewClientSetter(
        webView: WebView,
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
                    val packageManager = activity?.packageManager
                    if (
                        packageManager != null && intent?.resolveActivity(packageManager
                        ) != null)
                    {
                        activity?.startActivity(intent)
                        return true
                    }

                }
                return false
            }
        }
    }

//    private fun webChromeClientSetter(
//        webView: WebView,
//        progressBar: ProgressBar?
//    ){
//        webView.webChromeClient = object : WebChromeClient() {
//            override fun onProgressChanged(view: WebView, newProgress: Int) {
//                super.onProgressChanged(view, newProgress)
//                if (newProgress == 100) {
//                    progressBar?.visibility = View.GONE
//                } else {
//                    progressBar?.visibility = View.VISIBLE
//                    progressBar?.progress = newProgress
//                }
//            }
//            override fun getDefaultVideoPoster(): Bitmap? {
//                return Bitmap.createBitmap(
//                    50,
//                    50,
//                    Bitmap.Config.ARGB_8888
//                )
//            }
//        }
//    }

}