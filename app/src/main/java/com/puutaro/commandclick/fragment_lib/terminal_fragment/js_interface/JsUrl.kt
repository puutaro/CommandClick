package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForUrl
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.qr.QrUriHandler
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool

class JsUrl(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    @JavascriptInterface
    fun makeJsUrl(
        jsPath: String
    ): String {
        /*
        Make load js contents from js path
       */
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
        /*
        Make raw js contents before load
       */
        val fannelInfoMap = FannelInfoTool.getFannelInfoMap(
            terminalFragment,
            jsPath
        )
        val setReplaceVariableMap = FannelInfoTool.getReplaceVariableMap(
            terminalFragment,
            jsPath,
        )
        val jsRawCon = JavaScriptLoadUrl.makeRawJsConFromContents(
            terminalFragment,
            fannelInfoMap,
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
        /*
        Load js path

        ### replaceMapCon arg

        -> [replace variable](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_replace_variables.md)
        */
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
        /*
        [Deprecated] Load QR code contents about js or url
        */
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
        /*
        Load QR code contents about Url or javascript
        */
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
        /*
        Load Url
        This is one of the most used js interface.
        */
        BroadCastIntent.sendUrlCon(
            context,
            urlStr
        )
    }

    @JavascriptInterface
    fun loadUrlWithPageFinishedLoadCon(
        urlCon: String,
        pageFinishedLoadCon: String,
        beforeDelayMiliSec: String
    ){
        /*
        Load js contents when page load finish
        */
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
        /*
        Exit javascript loading
        */
        terminalFragment.binding.terminalWebView.loadUrl(
            "about:blank"
        )
    }

    @JavascriptInterface
    fun loadFromJsCon(
        jsCon: String,
    ){
        /*
        Load js contents
        */
        loadUrl(
            makeJsUrlFromCon(jsCon)
        )
    }
}