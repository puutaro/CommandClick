package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.webkit.WebView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WebViewJsDialog
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryRegister
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.url.WebUrlVariables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object PocketWebViewUrlLoader {

    private var registerUrlHistoryLine = String()

    fun load(
        terminalFragment: TerminalFragment,
        url: String?,
        isHistorySave: Boolean
    ){
        if (
            url.isNullOrEmpty()
        ) return
        terminalFragment.onRegisterPocketWebViewUrl?.cancel()
        terminalFragment.onRegisterPocketWebViewUrl = CoroutineScope(Dispatchers.Main).launch {
            val webView = terminalFragment.webViewDialogInstance?.findViewById<WebView>(
                R.id.webview_dialog_webview
            )
            when(webView == null) {
                true -> {
                    val webSearcherName = UrlFileSystems.Companion.FirstCreateFannels.WebSearcher.str +
                            UsePath.JS_FILE_SUFFIX
                    val systemExecRepTextList = listOf(url)

                    ExecJsLoad.execExternalJs(
                        terminalFragment,
                        terminalFragment.currentAppDirPath,
                        webSearcherName,
                        systemExecRepTextList
                    )
                }
                else -> WebViewJsDialog.loadUrlHandler(
                    terminalFragment,
                    webView,
                    url,
                )
            }
            withContext(Dispatchers.IO){
                if(
                    !isHistorySave
                ) {
                    return@withContext
                }
                val gglQueryUrl = WebUrlVariables.queryUrl
                val title = when(
                    url.startsWith(gglQueryUrl)
                ){
                    true -> "${url.removePrefix(gglQueryUrl)} - Google search"
                    else -> url
                }
                val currentAppDirPath = terminalFragment.currentAppDirPath
                val appUrlSystemPath = "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
                val cmdclickUrlHistoryFilePath = File(appUrlSystemPath, UsePath.cmdclickUrlHistoryFileName).absolutePath
                val beforeChecksum = FileSystems.checkSum(
                    cmdclickUrlHistoryFilePath
                )
                for (i in 1..6) {
                    delay(1000)
                    val curCheckSum = FileSystems.checkSum(
                        cmdclickUrlHistoryFilePath
                    )
                    if(
                        curCheckSum != beforeChecksum
                    ) break
                }
                UrlHistoryRegister.insertByUnique(
                    terminalFragment.currentAppDirPath,
                    title,
                    url,
                )
            }
        }

    }
}