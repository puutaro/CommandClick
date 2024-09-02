package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsLinux(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun runCmd(
        cmdStr: String
    ): String {
        /*
        Run cmd by native android shell

        ### Example

        ```js.js
        jsLinux.runCmd(
           "ls"
        )
        ```

        ### Example js action version

        ```js.js
        var=runCmd
           ?func=jsLinux.runCmd
           ?args=
                cmdStr="ls"
        ```

        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context


        val cmdOutput = LinuxCmd.execCommand(
            context,
            listOf("sh", "-c", cmdStr).joinToString("\t")
        )
        return cmdOutput
    }
}