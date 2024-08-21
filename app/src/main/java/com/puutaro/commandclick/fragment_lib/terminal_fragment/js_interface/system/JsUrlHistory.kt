package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryRegister
import com.puutaro.commandclick.util.state.FannelInfoTool

class JsUrlHistory(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
//    private val fannelInfoMap = terminalFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )

    @JavascriptInterface
    fun save(
        title: String,
        url: String,
    ){
        UrlHistoryRegister.insert(
//            currentAppDirPath,
            title,
            url,
        )
    }
}