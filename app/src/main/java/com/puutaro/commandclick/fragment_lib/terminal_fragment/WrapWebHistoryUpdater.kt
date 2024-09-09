package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.webkit.ValueCallback
import android.webkit.WebView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FdialogToolForTerm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.queryUrlToText
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryRegister
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.*


object WrapWebHistoryUpdater {

    private val autoFocusGgleSearchUrl = WebUrlVariables.autoFocusGgleSearchUrl
    fun update(
        terminalFragment: TerminalFragment,
        webView: WebView?,
        webViewUrl: String?,
        previousUrl: String?
    ){
        if(
            FdialogToolForTerm.howExitExecThisProcess(terminalFragment)
        ) return
        if(
            WebUrlVariables.isMapUrl(
                previousUrl,
                webViewUrl,
            )
        ) return
        if(webView == null) return
        if(
            terminalFragment.onUrlHistoryRegister
            != CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE
        ) return
        if(
            !terminalFragment.ignoreHistoryPathList
                ?.joinToString("")
                .isNullOrEmpty()
            && terminalFragment.ignoreHistoryPathList?.any {
                webViewUrl?.contains(it) == true
            } == true
        ) return
        terminalFragment.onWebHistoryUpdaterJob?.cancel()
        var urlTitleString: String? = null
        terminalFragment.onWebHistoryUpdaterJob = CoroutineScope(Dispatchers.IO).launch {
            if(
                webViewUrl.isNullOrEmpty()
            ) return@launch
            delay(1500)
            val webViewUrlLast = withContext(Dispatchers.Main) {
                webView.url
            }
            if(webViewUrlLast != webViewUrl) return@launch
            withContext(Dispatchers.Main) {
                webView.evaluateJavascript("(function() {  return document.title;})()",
                    ValueCallback<String?> { siteTitle ->
                        urlTitleString = if(
                            siteTitle.isEmpty()
                            && EnableUrlPrefix.isHttpPrefix(webViewUrl)
                        ) webViewUrl
                        else siteTitle
                    })
            }
            delay(500)
            if(
                urlTitleString.isNullOrEmpty()
            ) return@launch
//            UrlCaptureWatcher.watch(terminalFragment)
            withContext(Dispatchers.IO) {
                execUpdate(
                    terminalFragment,
                    urlTitleString,
                    webViewUrl,
                )
            }
        }
    }

    fun updateForPocketWebview(
        terminalFragment: TerminalFragment,
        webView: WebView?,
        webViewUrl: String?,
        previousUrl: String?
    ){
        if(
            WebUrlVariables.isMapUrl(
                previousUrl,
                webViewUrl
            )
        ) return
        if(webView == null) return
        if(
            terminalFragment.onUrlHistoryRegister
            != CommandClickScriptVariable.ON_URL_HISTORY_REGISTER_DEFAULT_VALUE
        ) return
        if(
            !terminalFragment.ignoreHistoryPathList
                ?.joinToString("")
                .isNullOrEmpty()
            && terminalFragment.ignoreHistoryPathList?.any {
                webViewUrl?.contains(it) == true
            } == true
        ) return
        terminalFragment.onPocketWebHistoryUpdaterJob?.cancel()
        var urlTitleString: String? = null
        terminalFragment.onPocketWebHistoryUpdaterJob = CoroutineScope(Dispatchers.IO).launch {
            if(
                webViewUrl.isNullOrEmpty()
            ) return@launch
            delay(1500)
            val webViewUrlLast = withContext(Dispatchers.Main) {
                webView.url
            }
            if(
                webViewUrlLast != webViewUrl
            ) return@launch
            withContext(Dispatchers.Main) {
                webView.evaluateJavascript("(function() {  return document.title;})()",
                    ValueCallback<String?> { siteTitle ->
                        urlTitleString = if(
                            siteTitle.isEmpty()
                            && EnableUrlPrefix.isHttpPrefix(webViewUrl)
                        ) webViewUrl
                        else siteTitle
                    })
            }
            delay(500)
            if(
                urlTitleString.isNullOrEmpty()
            ) return@launch
            withContext(Dispatchers.IO) {
                execUpdate(
                    terminalFragment,
                    urlTitleString,
                    webViewUrl,
                )
            }
        }
    }


    private fun execUpdate(
        terminalFragment: TerminalFragment,
        webViewTitle: String?,
        webViewUrl: String?,
    ){
        val activity = terminalFragment.activity
        val context = terminalFragment.context
        val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
        val fannelInfoMap = terminalFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val fannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val cmdVariableEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
//            currentAppDirPath,
            currentFannelName,
            fannelState,
        )
        val commandIndexFragment =
            TargetFragmentInstance.getFromFragment<CommandIndexFragment>(
                activity,
                cmdIndexFragmentTag
            )
        val cmdVariableEditFragment =
            TargetFragmentInstance.getFromFragment<EditFragment>(
                activity,
                cmdVariableEditFragmentTag
            )
        if(
            commandIndexFragment?.isVisible != true
            && cmdVariableEditFragment?.isVisible != true
        ) return

        val ulrTitle = QuoteTool.trimBothEdgeQuote(webViewTitle)
        val escapeStr = WebUrlVariables.escapeStr
        if (
            ulrTitle.endsWith("\t${escapeStr}")
        ) return

        val urlCheckResult = EnableUrlPrefix.isHttpOrFilePrefix(webViewUrl)
        if(!urlCheckResult) return
        val searchViewTextSource = if(
            webViewUrl?.startsWith(WebUrlVariables.queryUrl) == true
        ) {
            queryUrlToText(webViewUrl)
        } else webViewUrl ?:return
        if(
            webViewUrl.contains(autoFocusGgleSearchUrl)
        ) return
        val searchViewText = if(
            searchViewTextSource.startsWith(escapeStr)
        ) {
            String()
        } else searchViewTextSource
        if(
            searchViewText.isEmpty()
        ) return
        UrlHistoryRegister.insert(
//            currentAppDirPath,
            ulrTitle,
            webViewUrl,
        )
    }
}