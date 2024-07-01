package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
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
import com.puutaro.commandclick.util.state.SharePrefTool

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
        val loadJsCon = JavaScriptLoadUrl.make(
            terminalFragment.context,
            jsPath,
            jsConList,
        ) ?: String()
        return loadJsCon
    }

    @JavascriptInterface
    fun makeJsRawCon(
        jsPath: String
    ): String {
        val readSharePreferenceMap = SharePrefTool.getReadSharePrefMap(
            terminalFragment,
            jsPath
        )
        val setReplaceVariableMap = SharePrefTool.getReplaceVariableMap(
            terminalFragment,
            jsPath,
        )
        val jsRawCon = JavaScriptLoadUrl.makeRawJsConFromContents(
            terminalFragment,
            readSharePreferenceMap,
            ReadText(jsPath).readText(),
            setReplaceVariableMap
        )
        return jsRawCon
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
    }
    @JavascriptInterface
    fun makeJsUrlFromCon(
        execCode: String,
    ): String {
        val loadJsCon = JavaScriptLoadUrl.makeFromContents(
            context,
            execCode.split("\n")
        ) ?: String()
        return loadJsCon
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
            else -> ToastUtils.showShort("unKnown: $loadCon")
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

    @JavascriptInterface
    fun exit_S(){
        terminalFragment.binding.terminalWebView.loadUrl(
            "about:blank"
        )
    }

    @JavascriptInterface
    fun loadFromJsCon(
        jsCon: String,
    ){
        loadUrl(
            makeJsUrlFromCon(jsCon)
        )
    }
}