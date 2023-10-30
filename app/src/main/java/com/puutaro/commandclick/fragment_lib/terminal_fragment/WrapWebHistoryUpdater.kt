package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.webkit.ValueCallback
import android.webkit.WebView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FirstUrlHistoryFile
import com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib.queryUrlToText
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance
import kotlinx.coroutines.*


object WrapWebHistoryUpdater {
    fun update(
        terminalFragment: TerminalFragment,
        webView: WebView?,
        webViewUrl: String?,
        previousUrl: String?
    ){
        if(
            (previousUrl?.length == webViewUrl?.length
                    && webViewUrl?.contains("/maps/") == true
                    )
            && webViewUrl.contains("google")
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
            if(urlTitleString.isNullOrEmpty()) return@launch
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
                    webViewUrl
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
        val cmdVariableEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            terminalFragment.currentAppDirPath,
            terminalFragment.currentScriptName,
            FragmentTagManager.Suffix.ON.name
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
        val ulrTitle = QuoteTool.trimBothEdgeQuote(webViewTitle)
        val escapeStr = WebUrlVariables.escapeStr
        if (ulrTitle.endsWith("\t${escapeStr}")) return

        val urlCheckResult = EnableUrlPrefix.isHttpOrFilePrefix(webViewUrl)
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

        val appUrlSystemDirPath = "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val takeHistoryNum = 500
        val updatingHistory = "${ulrTitle}\t${webViewUrl}\n" + ReadText(
            appUrlSystemDirPath,
            cmdclickUrlHistoryFileName
        ).textToList().take(takeHistoryNum).joinToString("\n")
        FileSystems.writeFile(
            appUrlSystemDirPath,
            cmdclickUrlHistoryFileName,
            updatingHistory
        )

    }

    private fun registerUrlHistoryTitle(
        terminalFragment: TerminalFragment,
        ulrTitle: String?
    ){
        if(ulrTitle.isNullOrEmpty()) return
        val registerUrlTitle = if(
            terminalFragment.onHistoryUrlTitle !=
            CommandClickScriptVariable.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
        ) ulrTitle
        else String()
        if(
            registerUrlTitle.endsWith(
                UsePath.JS_FILE_SUFFIX
            )
            || registerUrlTitle.endsWith(
                UsePath.JSX_FILE_SUFFIX
            )
        ) return
        FirstUrlHistoryFile.update(
            terminalFragment
        )
    }
}