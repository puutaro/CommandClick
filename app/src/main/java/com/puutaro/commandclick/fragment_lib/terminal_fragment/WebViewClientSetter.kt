package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FdialogToolForTerm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.ImplicitIntentStarter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.UrlTermLongProcess
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.WebViewRequestValidation
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File


object WebViewClientSetter {

    fun set(
        terminalFragment: TerminalFragment
    ){
        val binding = terminalFragment.binding
        val context = terminalFragment.context
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val validation = WebViewRequestValidation()
        val implicitIntentStarter = terminalFragment.context?.let {
            ImplicitIntentStarter(
                it
            )
        }

        binding.terminalWebView.webViewClient = object : WebViewClient() {


            val allowedRequest = false
            val notAllowedRequest = true
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                UrlCaptureWatcher.exit()
                val url = request?.url
                    ?: return notAllowedRequest

                validation.isTransitionToOtherApp(url).let {
                    when (it) {
                        is WebViewRequestValidation.TransitionToOtherApp.Yes -> {
                            implicitIntentStarter?.startActivity(it.intent, url)
                            return notAllowedRequest
                        }
                        is WebViewRequestValidation.TransitionToOtherApp.No -> {}
                    }
                }
                return allowedRequest
            }
            var previousUrl: String? = null
            override fun doUpdateVisitedHistory(webView: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(webView, url, isReload)
                terminalFragment.currentUrl = url
                WrapWebHistoryUpdater.update(
                    terminalFragment,
                    webView,
                    url,
                    previousUrl
                )
                if(
                    previousUrl?.length == url?.length
                ) return
                val listener =
                    context as? TerminalFragment.OnPageLoadPageSearchDisableListener
                listener?.onPageLoadPageSearchDisable()
//                SearchViewAndAutoCompUpdater.update(
//                    terminalFragment,
//                    webView,
//                    url,
//                )
                UrlTermLongProcess.trigger(
                    terminalFragment,
                    terminalViewModel,
                    webView,
                    url,
                )
                previousUrl = url

            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if(
                    terminalFragment.onAdBlock !=
                    SettingVariableSelects.OnAdblockSelects.ON.name
                ) return super.shouldInterceptRequest(view, request)
                val empty3 = ByteArrayInputStream("".toByteArray())
                val blockListCon = terminalViewModel.blockListCon
                val isBlock = AdBlocker.judgeBlock(
                    request?.url?.host,
                    blockListCon
                )
                if (isBlock) {
                    return WebResourceResponse("text/plain", "utf-8", empty3)
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                UrlCaptureWatcher.exit()
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main) {
                        val listenerForSelectionBar =
                            context as TerminalFragment.OnSelectionSearchBarSwitchListenerForTerm
                        listenerForSelectionBar.onSelectionSearchBarSwitchForTerm(false)
                    }
                }
            }

            var previousUrlForPageFinished: String? = null
            override fun onPageFinished(
                webview: WebView?,
                url: String?
            ) {
                val goBackFlag = terminalFragment.goBackFlag
                if (
                    terminalFragment.goBackFlag
                ) terminalFragment.goBackFlag = false
                if(
                    goBackFlag
                    && (
                            url?.startsWith(WebUrlVariables.httpPrefix) != true
                                    && url?.startsWith(WebUrlVariables.httpsPrefix) != true
                                    && webview?.title != WebUrlVariables.escapeStr
                            )
                    && LoadUrlPrefixSuffix.judgeTextFile(url)
                ) {
                    webview?.loadUrl(url.toString())
                    return
                }
                if(
                    goBackFlag
                    && webview?.title?.trim() == WebUrlVariables.escapeStr
                ) {
                    terminalViewModel.onDisplayUpdate = true
                    return
                }
                super.onPageFinished(webview, url)
                terminalFragment.onScrollPosiSaveJob = CoroutineScope(Dispatchers.Main).launch{
                    if(
                        FdialogToolForTerm.howExitExecThisProcess(terminalFragment)
                    ) return@launch
                    webview?.url?.let {
                        ScrollPosition.execScroll(
                            terminalFragment,
                            webview,
                            it,
                        )
                    }
                }
                val isMap =  WebUrlVariables.isMapUrl(
                    previousUrlForPageFinished,
                    url,
                )
                previousUrlForPageFinished = url
                if(!isMap){
                    UrlCaptureWatcher.watch(terminalFragment)
                }
                val appUrlSystemDirPath = "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
                terminalFragment.onPageFinishedCoroutineJob = CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        if(
                            FdialogToolForTerm.howExitExecThisProcess(terminalFragment)
                        ) return@withContext
                        FileSystems.writeFile(
                            File(
                                appUrlSystemDirPath,
                                UsePath.urlLoadFinished,
                            ).absolutePath,
                            System.currentTimeMillis().toString()
                        )
                    }
                }
            }
        }
    }
}

