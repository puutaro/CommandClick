package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.queryUrlToText
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WrapWebHistoryUpdater {
    companion object {
        fun update(
            terminalFragment: TerminalFragment,
            webView: WebView?,
            webViewUrl: String?,
            previousUrl: String?
        ){
            if(
                (previousUrl?.length == webViewUrl?.length
                        && webViewUrl?.contains("/maps/") == true) && webViewUrl.contains("google")
            ) return
            if(webView == null) return
            terminalFragment.onWebHistoryUpdaterJob?.cancel()
            var urlTitleString: String? = null
            terminalFragment.onWebHistoryUpdaterJob = terminalFragment.lifecycleScope.launch {
                terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    if(webViewUrl.isNullOrEmpty()) return@repeatOnLifecycle
                    delay(1500)
                    val webViewUrlLast = withContext(Dispatchers.Main) {
                        webView.url
                    }
                    if(webViewUrlLast != webViewUrl) return@repeatOnLifecycle
                    withContext(Dispatchers.Main) {
                        webView.evaluateJavascript("(function() {  return document.title;})()",
                            ValueCallback<String?> { siteTitle ->
                                urlTitleString = siteTitle
                            })
                    }
                    delay(500)
                    if(urlTitleString.isNullOrEmpty()) return@repeatOnLifecycle
                    withContext(Dispatchers.IO) {
                        execUpdate(
                            terminalFragment,
                            urlTitleString,
                            webViewUrl,
                        )
                    }
                    withContext(Dispatchers.IO) {
                        registerUrlHistoryTitle(
                            terminalFragment,
                            terminalFragment.currentAppDirPath,
                            webViewUrl
                        )
                    }
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
            val cmdVariableEditFragmentTag = context?.getString(
                R.string.cmd_variable_edit_fragment
            )
            val commandIndexFragment =
                TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                    activity,
                    cmdIndexFragmentTag
                )
            val cmdVariableEditFragment =
                TargetFragmentInstance().getFromFragment<EditFragment>(
                    activity,
                    cmdVariableEditFragmentTag
                )
            if(
                commandIndexFragment?.isVisible != true
                && cmdVariableEditFragment?.isVisible != true
            ) return

            val currentAppDirPath = terminalFragment.currentAppDirPath
            val ulrTitle = BothEdgeQuote.trim(webViewTitle)
            val escapeStr = WebUrlVariables.escapeStr
            if (ulrTitle.endsWith("\t${escapeStr}")) return

            val urlCheckResult = EnableUrlPrefix.check(webViewUrl)
            if(!urlCheckResult) return
            val searchViewTextSource = if(
                webViewUrl?.startsWith(WebUrlVariables.queryUrl) == true
            ) {
                queryUrlToText(webViewUrl)
            } else webViewUrl ?:return
            val searchViewText = if(
                searchViewTextSource.startsWith(escapeStr)
            ) {
                String()
            } else searchViewTextSource
            if(
                searchViewText.isEmpty()
            ) return
            val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
            val takeHistoryNum = 500
            val updatingHistory = "${ulrTitle}\t${webViewUrl}\n" + ReadText(
                currentAppDirPath,
                cmdclickUrlHistoryFileName
            ).textToList().take(takeHistoryNum).joinToString("\n")
            FileSystems.writeFile(
                currentAppDirPath,
                cmdclickUrlHistoryFileName,
                updatingHistory
            )

        }

        private fun registerUrlHistoryTitle(
            terminalFragment: TerminalFragment,
            currentAppDirPath: String,
            ulrTitle: String?
        ){
            if(ulrTitle.isNullOrEmpty()) return
            val registerUrlTitle = if(
                terminalFragment.onHistoryUrlTitle !=
                CommandClickShellScript.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
            ) ulrTitle
            else String()
            if(
                registerUrlTitle.endsWith(
                    CommandClickShellScript.JS_FILE_SUFFIX
                )
                || registerUrlTitle.endsWith(
                    CommandClickShellScript.JSX_FILE_SUFFIX
                )
            ) return
            FileSystems.writeFile(
                currentAppDirPath,
                UsePath.cmdclickFirstHistoryTitle,
                registerUrlTitle
            )
        }
    }
}