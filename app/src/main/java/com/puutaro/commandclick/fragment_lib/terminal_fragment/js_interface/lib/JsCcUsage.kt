package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

class JsCcUsage(
    private val terminalFragment: TerminalFragment
) {
//    private val fannelInfoMap = terminalFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )

    @JavascriptInterface
    fun launch_S(){
        val webSearcherName = SystemFannel.webSearcher
        ExecJsLoad.execExternalJs(
            terminalFragment,
//            currentAppDirPath,
            webSearcherName,
            listOf(WebUrlVariables.commandClickUsageUrl),
        )
    }
}