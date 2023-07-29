package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ExecDownLoadManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.DialogJsInterfaceVariant
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.JsInterfaceVariant
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream


class WebViewJsDialog(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

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
            ExecDownLoadManager.set(
                terminalFragment,
                webView
            )
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
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
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
            ScrollPosition.save(
                terminalFragment,
                currentUrl,
                webView.scrollY,
                100f,
                0f,
            )
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

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if(
                    terminalFragment.onAdBlock != SettingVariableSelects.OnAdblockSelects.ON.name
                ) return super.shouldInterceptRequest(view, request)
                val empty3 = ByteArrayInputStream("".toByteArray())
                val blocklist = terminalViewModel.blocklist
                if (blocklist.contains(":::::" + request?.url?.host)) {
                    return WebResourceResponse("text/plain", "utf-8", empty3)
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
    }
}