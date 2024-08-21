package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.state.FannelInfoTool

class JsFannelExecer(
    private val terminalFragment: TerminalFragment
) {

    val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )

    @JavascriptInterface
    fun exec_S(){
        ExecJsOrSellHandler.handle(
            terminalFragment,
//            currentAppDirPath,
            currentFannelName,
        )
    }
}