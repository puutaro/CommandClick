package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

class JsAddGmailCon(
    private val terminalFragment: TerminalFragment,
) {

    @JavascriptInterface
    fun add(
        gmailAd: String,
        urlConSaveParentDirPath: String,
        compSuffix: String,
    ){
        if(
            gmailAd.isEmpty()
        ) return
        ExecJsLoad.execExternalJs(
            terminalFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveGmailConDialogFannelName,
            listOf(
                gmailAd,
                urlConSaveParentDirPath,
                compSuffix
            ),
        )
    }
}