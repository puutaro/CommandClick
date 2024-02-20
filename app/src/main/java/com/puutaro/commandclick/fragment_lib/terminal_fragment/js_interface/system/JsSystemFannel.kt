package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher

class JsSystemFannel(
    private val terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun launch(
        appDirPath: String,
        fannelName: String,
    ){
        SystemFannelLauncher.launch(
            terminalFragment,
            appDirPath,
            fannelName
        )
    }
}