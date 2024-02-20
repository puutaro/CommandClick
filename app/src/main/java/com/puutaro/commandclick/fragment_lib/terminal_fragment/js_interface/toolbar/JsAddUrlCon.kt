package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

class JsAddUrlCon(
    private val terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun add(
        urlString: String,
        onSearchBtn: String,
        urlConSaveParentDirPath: String,
        compSuffix: String,
    ){
        ExecJsLoad.execExternalJs(
            terminalFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
                urlConSaveParentDirPath,
                compSuffix
            ),
        )
    }
}