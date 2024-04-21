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
        ToastUtils.showShort(cmdStr)
        return LinuxCmd.execCommand(
            context,
            listOf("sh", "-c", cmdStr).joinToString("\t")
        )
    }

    fun getOutput(cmd: String): String {
        if(
            context == null
        ) return String()
        return BusyboxExecutor(
            context,
            UbuntuFiles(context)
        ).getCmdOutput(cmd)
    }
}