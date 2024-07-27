package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.shell.LinuxCmd

class JsLinux(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

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
        val cmdOutput = LinuxCmd.execCommand(
            context,
            listOf("sh", "-c", cmdStr).joinToString("\t")
        )
        return cmdOutput
    }
}