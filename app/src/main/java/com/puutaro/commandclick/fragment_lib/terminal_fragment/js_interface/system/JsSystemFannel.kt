package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsSystemFannel(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
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

        val terminalFragment = terminalFragmentRef.get()
            ?: return
        SystemFannelLauncher.launch(
            terminalFragment,
//            appDirPath,
            fannelName
        )
    }
}