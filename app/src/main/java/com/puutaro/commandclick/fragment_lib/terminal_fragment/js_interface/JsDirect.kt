package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.qr.CopyFannelServer
import com.puutaro.commandclick.proccess.qr.SaveFromFannelServer

class JsDirect (
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun launchCopyFannelServer(){
        CopyFannelServer.launch(
            terminalFragment
        )
    }

    @JavascriptInterface
    fun exitCopyFannelServer(){
        CopyFannelServer.exit(
            terminalFragment
        )
    }

    @JavascriptInterface
    fun get(
        mainUrl: String,
        fannelRawName: String,
    ){
        SaveFromFannelServer.save(
            terminalFragment,
            mainUrl,
            fannelRawName,
        )
    }
}