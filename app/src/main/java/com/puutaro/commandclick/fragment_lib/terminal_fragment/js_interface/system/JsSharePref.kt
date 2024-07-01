package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.SharePrefTool

class JsSharePref(
    terminalFragment: TerminalFragment
) {
    val readSharePreferenceMap = terminalFragment.readSharePreferenceMap

    @JavascriptInterface
    fun getFannelName(): String {
        val fannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        return fannelName
    }

    @JavascriptInterface
    fun getAppDirPath(): String {
        val getAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        return getAppDirPath
    }

    @JavascriptInterface
    fun getState(): String {
        val currentSate = SharePrefTool.getCurrentStateName(
            readSharePreferenceMap
        )
        return currentSate
    }
}