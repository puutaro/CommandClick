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
        return SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
    }

    @JavascriptInterface
    fun getAppDirPath(): String {
        return SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
    }

    @JavascriptInterface
    fun getState(): String {
        return SharePrefTool.getCurrentStateName(
            readSharePreferenceMap
        )
    }
}