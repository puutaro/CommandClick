package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.state.SharePrefTool

class JsCcUsage(
    private val terminalFragment: TerminalFragment
) {
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun launch_S(){
        val webSearcherName = UrlFileSystems.Companion.FirstCreateFannels.WebSearcher.str +
                UsePath.JS_FILE_SUFFIX
        ExecJsLoad.execExternalJs(
            terminalFragment,
            currentAppDirPath,
            webSearcherName,
            listOf(WebUrlVariables.commandClickUsageUrl),
        )
    }
}