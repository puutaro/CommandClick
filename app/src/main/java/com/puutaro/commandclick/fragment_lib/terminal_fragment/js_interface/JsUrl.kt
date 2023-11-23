package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.qr.QrUri
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.ScriptPreWordReplacer

class JsUrl(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val jsDescSeparator = QrSeparator.sepalator.str
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
        QrUri.load(
            terminalFragment,
            terminalFragment.currentAppDirPath,
            loadConSrc
        )
//        val jsDesc = QrLaunchType.JsDesc.prefix
//        val replaceLoadUrlSrc =
//            ScriptPreWordReplacer.replaceForQr(
//                loadConSrc,
//                terminalFragment.currentAppDirPath
//            )
//        val loadUrl =
//            if(
//                replaceLoadUrlSrc.trim().startsWith(jsDesc)
//            ) replaceLoadUrlSrc.split(jsDescSeparator).filterIndexed {
//                        index, _ -> index > 0
//                }.joinToString(jsDescSeparator)
//            else replaceLoadUrlSrc
//        BroadCastIntent.sendUrlCon(
//            terminalFragment,
//            loadUrl.trim()
//        )
    }

    @JavascriptInterface
    fun loadScanCon(
        loadConSrc: String,
    ){
        val loadCon = loadConSrc.trim()
        when(true) {
            loadCon.startsWith(QrLaunchType.Http.prefix),
            loadCon.startsWith(QrLaunchType.Https.prefix),
            loadCon.startsWith(QrLaunchType.Javascript.prefix),
            loadCon.startsWith(QrLaunchType.JsDesc.prefix),
                -> loadQrUrl(
                loadCon
                )
            else -> Toast.makeText(
                context,
                "unKnown: $loadCon",
                Toast.LENGTH_SHORT
            ).show()
        }

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