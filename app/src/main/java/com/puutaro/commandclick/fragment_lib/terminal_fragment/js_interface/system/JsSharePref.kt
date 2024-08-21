package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool

class JsSharePref(
    terminalFragment: TerminalFragment
) {
    val fannelInfoMap = terminalFragment.fannelInfoMap

    @JavascriptInterface
    fun getFannelName(): String {
        val fannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        return fannelName
    }

    @JavascriptInterface
    fun getAppDirPath(): String {
//        val getAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        return UsePath.cmdclickDefaultAppDirPath
    }

    @JavascriptInterface
    fun getState(): String {
        val currentSate = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        return currentSate
    }
}