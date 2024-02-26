package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.state.SharePrefTool

class JsFannelExecer(
    private val terminalFragment: TerminalFragment
) {

    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun exec_S(){
        ExecJsOrSellHandler.handle(
            terminalFragment,
            currentAppDirPath,
            currentFannelName,
        )
    }
}