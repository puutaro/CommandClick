package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import android.webkit.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.*
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.util.SharePreffrenceMethod
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
        val startUpPref = terminalFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val validation = WebViewRequestValidation()
        val implicitIntentStarter = terminalFragment.context?.let {
            ImplicitIntentStarter(
                it
            )
        }
        val systemExecJsDirName = UsePath.systemExecJsDirName
        val pageFinishdJsName = UsePath.pageFinishdJsName
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
                val listener =
                    context as? TerminalFragment.OnPageLoadPageSearchDisableListener
                listener?.onPageLoadPageSearchDisable()
                SearchViewAndAutoCompUpdater.update(
                    terminalFragment,
                    webView,
                    url,
                )
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
                    terminalFragment.onAdBlock != SettingVariableSelects.OnAdblockSelects.ON.name
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
                CoroutineScope(Dispatchers.Main).launch{
                    webview?.url?.let {
                        ScrollPosition.execScroll(
                            terminalFragment,
                            webview,
                            it,
                        )
                    }
                }
                val appUrlSystemDirPath = "${terminalFragment.currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
                terminalFragment.onPageFinishedCoroutineJob = CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        FileSystems.writeFile(
                            appUrlSystemDirPath,
                            UsePath.urlLoadFinished,
                            System.currentTimeMillis().toString()
                        )
                    }
                    withContext(Dispatchers.IO){
                        val currentAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
                            startUpPref,
                            SharePrefferenceSetting.current_app_dir
                        )
                        val curFannelName = SharePreffrenceMethod.getStringFromSharePreffrence(
                            startUpPref,
                            SharePrefferenceSetting.current_script_file_name
                        )
                        val fannelDirName = CcPathTool.makeFannelDirName(curFannelName)
                        val systemExecJsDirPath = "${currentAppDirPath}/${fannelDirName}/${systemExecJsDirName}"
                        val pageFinishedJsPath = "${systemExecJsDirPath}/${pageFinishdJsName}"
                        if(
                            !File(pageFinishedJsPath).isFile
                        ) return@withContext
                        val jsCon = JavaScriptLoadUrl.make(
                                context,
                                pageFinishedJsPath
                            ) ?: return@withContext
                        BroadCastIntent.sendUrlCon(
                            terminalFragment,
                            jsCon
                        )
                    }
                }
            }
        }
    }
}

