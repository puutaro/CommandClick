package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsCmdIntent(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun run_S(
        execCmdSource: String
    ){
        /*
        Run command by termux

        ### Example
        JsCmdIntent.run_S(
            "bash \"$[bash script path}\"
        )

        - Enable `> /dev/null` or `> /dev/null 2>&1`

        */
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

        val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val execCmd = if(
            execCmdSource.endsWith("> /dev/null")
            || execCmdSource.endsWith("> /dev/null 2>&1")
        ) "${execCmdSource};"
        else "$execCmdSource >> \"${outputPath}\""
        ExecBashScriptIntent.ToTermux(
            context,
            execCmd,
            true
        )
    }
}