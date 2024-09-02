package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.webkit.WebView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WebViewJsDialog
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryRegister
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.url.WebUrlVariables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

object PocketWebViewUrlLoader {

    fun load(
        terminalFragment: TerminalFragment,
        url: String?,
//        isHistorySave: Boolean
    ){
        if (
            url.isNullOrEmpty()
        ) return
        terminalFragment.onRegisterPocketWebViewUrl?.cancel()
        terminalFragment.onRegisterPocketWebViewUrl = CoroutineScope(Dispatchers.Main).launch {
            when(
                terminalFragment.pocketWebViewManager?.pocketWebView?.isVisible == true
            ) {
                false -> {
                    val webSearcherName = SystemFannel.webSearcher
                    val systemExecRepTextList = listOf(url)

                    ExecJsLoad.execExternalJs(
                        terminalFragment,
//                        terminalFragment.currentAppDirPath,
                        webSearcherName,
                        systemExecRepTextList
                    )
                }
                else ->
                    terminalFragment.pocketWebViewManager?.loadUrlHandler(
                        terminalFragment,
                        url,
                    )
            }
        }
    }
}