package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsCcUsage(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun launch_S(){
        val terminalFragment = terminalFragmentRef.get() ?: return

        val webSearcherName = SystemFannel.webSearcher
        ExecJsLoad.execExternalJs(
            terminalFragment,
//            currentAppDirPath,
            webSearcherName,
            listOf(WebUrlVariables.commandClickUsageUrl),
        )
    }
}