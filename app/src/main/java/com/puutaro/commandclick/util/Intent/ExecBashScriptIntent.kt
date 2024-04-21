package com.puutaro.commandclick.util.Intent

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.termux.shared.termux.TermuxConstants


object ExecBashScriptIntent {

    fun ToTermux(
        context: Context?,
        execCmd: String,
        backgroundExec: Boolean = true,
    ) {
        try {
            val intent = execToTermux(
                execCmd,
                backgroundExec,
            )
            context?.startService(intent)
        } catch(e: Exception) {
            ToastUtils.showLong(
                "failure intent to Termux \n Does add permission or start it?"
            )
        }
    }
}


private fun  execToTermux(
    execCmd: String,
    backgroundExec: Boolean
): Intent {
    val intent = Intent()
    intent.setClassName(
        TermuxConstants.TERMUX_PACKAGE_NAME,
        TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE_NAME
    )

    intent.setAction(TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND)
    intent.action = TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND
    intent.putExtra(
        TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.EXTRA_COMMAND_PATH,
        "/data/data/com.termux/files/usr/bin/bash"
    )
    intent.putExtra(
        TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.EXTRA_ARGUMENTS,
        arrayOf("-c", execCmd)
    )
    intent.putExtra(
        TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.EXTRA_WORKDIR,
        "/data/data/com.termux/files/home"
    )
    intent.putExtra(TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.EXTRA_BACKGROUND, backgroundExec)
    return intent
}