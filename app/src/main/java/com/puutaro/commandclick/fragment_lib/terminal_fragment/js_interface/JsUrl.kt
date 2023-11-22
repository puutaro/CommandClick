package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.ScriptPreWordReplacer

class JsUrl(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun makeJsUrl(
        jsPath: String
    ): String {
        return JavaScriptLoadUrl.make(
            terminalFragment.context,
            jsPath
        ) ?: String()
    }

    @JavascriptInterface
    fun loadQrUrl(
        loadConSrc: String
    ) {
        val jsDesc = QrLaunchType.JsDesc.prefix
        val jsDescSeparator = "&&&"
        val replaceLoadUrlSrc =
            ScriptPreWordReplacer.replaceForQr(
                loadConSrc,
                terminalFragment.currentAppDirPath
            )
        val loadUrl =
            if(
                replaceLoadUrlSrc.trim().startsWith(jsDesc)
            ) replaceLoadUrlSrc.split(jsDescSeparator).filterIndexed {
                        index, _ -> index > 0
                }.joinToString(jsDescSeparator)
            else replaceLoadUrlSrc
        BroadCastIntent.sendUrlCon(
            terminalFragment,
            loadUrl.trim()
        )
    }

    @JavascriptInterface
    fun loadUrl(
        urlStr: String
    ) {
        BroadCastIntent.sendUrlCon(
            terminalFragment,
            urlStr
        )
    }
}