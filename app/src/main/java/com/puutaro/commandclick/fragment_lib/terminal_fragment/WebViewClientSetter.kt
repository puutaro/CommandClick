package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.webkit.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.*
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream


class WebViewClientSetter {
    companion object {
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
            var previousUrl: String? = null

            binding.terminalWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val allowedRequest = false
                    val notAllowedRequest = true

                    val url = request?.url ?: return notAllowedRequest

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
                    SearchViewAndAutoCompUpdater.update(
                        terminalFragment,
                        webView,
                        url,
                    )
                    val listener =
                        context as? TerminalFragment.OnPageLoadPageSearchDisableListener
                    listener?.onPageLoadPageSearchDisable()
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
                        terminalFragment.onAdBlock != SettingVariableSelects.Companion.OnAdblockSelects.ON.name
                    ) return super.shouldInterceptRequest(view, request)
                    val EMPTY3 = ByteArrayInputStream("".toByteArray())
                    val blocklist = terminalFragment.blocklist
                    if (blocklist.contains(":::::" + request?.url?.host)) {
                        return WebResourceResponse("text/plain", "utf-8", EMPTY3)
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageFinished(
                    webview: WebView?,
                    url: String?
                ) {
                    val goBackFlag = terminalViewModel.goBackFlag
                    if (
                        terminalViewModel.goBackFlag
                    ) terminalViewModel.goBackFlag = false
                    if(
                        goBackFlag
                        && (
                                url?.startsWith(WebUrlVariables.httpPrefix) != true
                                        && url?.startsWith(WebUrlVariables.httpsPrefix) != true
                                        && webview?.title != WebUrlVariables.escapeStr
                                )
                    ) {
                        webview?.loadUrl(url.toString())
                        return
                    }
                    if(
                        goBackFlag
                        && webview?.title?.trim(' ') == WebUrlVariables.escapeStr
                    ) {
                        terminalViewModel.onDisplayUpdate = true
                        return
                    }
                    super.onPageFinished(webview, url)
                    val appUrlSystemDirPath = "${terminalFragment.currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
                    terminalFragment.onPageFinishedCoroutineJob = CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.IO) {
                            FileSystems.writeFile(
                                appUrlSystemDirPath,
                                UsePath.urlLoadFinished,
                                System.currentTimeMillis().toString()
                            )
                        }
                    }
                }
            }
        }
    }
}

