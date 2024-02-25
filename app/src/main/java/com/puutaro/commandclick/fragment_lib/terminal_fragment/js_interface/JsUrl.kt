package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForUrl
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.qr.QrUriHandler
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap

class JsUrl(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    @JavascriptInterface
    fun makeJsUrl(
        jsPath: String
    ): String {
        val jsConList =
            ReadText(jsPath).textToList()
        return JavaScriptLoadUrl.make(
            terminalFragment.context,
            jsPath,
            jsConList,
        ) ?: String()
    }

    @JavascriptInterface
    fun makeJsRawCon(
        jsPath: String
    ): String {
        return JavaScriptLoadUrl.makeRawJsConFromContents(
            terminalFragment,
            ReadText(jsPath).readText()
        )
    }

    @JavascriptInterface
    fun loadJsPath(
        jsPath: String,
        replaceMapCon: String,
    ) {
        val replaceMap = CmdClickMap.createMap(
            replaceMapCon,
            '|'
        ).toMap()
        val jsConList =
            ReadText(jsPath).textToList()
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            jsPath,
            jsConList,
            extraMapCon = replaceMap
        )
//        val jsCon = JavaScriptLoadUrl.make(
//            terminalFragment.context,
//            jsPath,
//            jsConList
//        )?.let {
//            CmdClickMap.replace(
//                it,
//                replaceMap
//            )
//        } ?: String()
//        BroadCastIntent.sendUrlCon(
//            terminalFragment,
//            jsCon
//        )
    }
    @JavascriptInterface
    fun makeJsUrlFromCon(
        execCode: String,
    ): String {
        return JavaScriptLoadUrl.makeFromContents(
            execCode.split("\n")
        ) ?: String()
    }

    @JavascriptInterface
    fun loadQrUrl(
        loadConSrc: String
    ) {
        QrUriHandler.load(
            terminalFragment,
            terminalFragment.currentAppDirPath,
            loadConSrc
        )
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

    @JavascriptInterface
    fun loadUrlWithPageFinishedLoadCon(
        urlCon: String,
        pageFinishedLoadCon: String,
        beforeDelayMiliSec: String
    ){
        val urlBroadcastExtra = listOf(
            Pair(
                BroadCastIntentSchemeTerm.ULR_LAUNCH.scheme,
                urlCon
            ),
            Pair(
                BroadCastIntentExtraForUrl.BEFORE_DELAY_MILI_SEC.scheme,
                beforeDelayMiliSec
            ),
            Pair(
                BroadCastIntentExtraForUrl.PAGE_FINISHED_LOAD_CON.scheme,
                pageFinishedLoadCon
            )
        )
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeTerm.ULR_LAUNCH.action,
            urlBroadcastExtra
        )
    }
}