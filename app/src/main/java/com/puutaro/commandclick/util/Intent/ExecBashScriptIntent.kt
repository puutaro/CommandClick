package com.puutaro.commandclick.util.Intent

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.termux.shared.termux.TermuxConstants


class ExecBashScriptIntent {
    companion object {
        fun ToTermux(
            runShell: String,
            context: Context?,
            execCmd: String,
            backgroundExec: Boolean = true,
        ) {
            try {
                val serviceIntent = Intent()
                serviceIntent.component = ComponentName(
                    TermuxConstants.TERMUX_PACKAGE_NAME,
                    TermuxConstants.TERMUX_APP.TERMUX_SERVICE_NAME
                )
                context?.startService(serviceIntent)
            } catch (e: Exception){
                Log.d("no", "runcommand service no start")
            }
            try {
                val intent = ToTermux(
                    runShell,
                    execCmd,
                    backgroundExec,
                )
                context?.startService(intent)
            } catch(e: Exception) {
                Toast.makeText(
                    context,
                    "failure intent to Termux \n Does add permission or start it?",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}


private fun  ToTermux(
    runShell: String,
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
        "/data/data/com.termux/files/usr/bin/${runShell}"
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