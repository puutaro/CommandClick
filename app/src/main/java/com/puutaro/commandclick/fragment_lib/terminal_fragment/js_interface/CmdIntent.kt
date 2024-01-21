package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class CmdIntent(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()


    @JavascriptInterface
    fun run(
        execCmdSource: String
    ){
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