package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher

class JsSystemFannel(
    private val terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun launch_S(
        appDirPath: String,
        fannelName: String,
    ){
        /*
        ## Description

        Launch system {fannel}(https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel)

        */

        SystemFannelLauncher.launch(
            terminalFragment,
//            appDirPath,
            fannelName
        )
    }
}