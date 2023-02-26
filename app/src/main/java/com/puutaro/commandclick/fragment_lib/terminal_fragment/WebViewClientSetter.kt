package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.webkit.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.ImplicitIntentStarter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.UrlTermLongProcess
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.WebHistoryUpdater
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.WebViewRequestValidation
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
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
                    if(previousUrl?.length == url?.length) return
                    val listener =
                        context as? TerminalFragment.OnPageLoadPageSearchDisableListener
                    listener?.onPageLoadPageSearchDisable()
                    WebHistoryUpdater.webHistoryUpdater(
                        terminalFragment,
                        terminalViewModel,
                        webView,
                        url,
                    )

                    FileSystems.writeFile(
                        terminalFragment.currentAppDirPath,
                        UsePath.urlLoadFinished,
                        System.currentTimeMillis().toString()
                    )

                    UrlTermLongProcess.torigger(
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

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    terminalFragment.onPageFinishedCoroutineJob = terminalFragment.lifecycleScope.launch {
                        terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            withContext(Dispatchers.IO) {
                                FileSystems.writeFile(
                                    terminalFragment.currentAppDirPath,
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
}
