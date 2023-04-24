package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class KillConfirmDialogForEdit {
    companion object {
        fun show(
            editFragment: EditFragment,
            currentAppDirPath: String,
            shellScriptName: String,
            currentMonitorFileName: String,
        ){


            val context = editFragment.context
            val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
            terminalViewModel.onDisplayUpdate = true
            val alertDialog = AlertDialog.Builder(editFragment.context)
                .setTitle(
                    "Kill bellow shell path process, ok?"
                )
                .setMessage("\tpath: ${shellScriptName}")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    val factExecCmd =
                        "ps aux | grep \"${shellScriptName}\" " +
                                " | grep -v grep |  awk '{print \$2}' | xargs -I{} kill {} "
                    val outputPath = "${UsePath.cmdclickMonitorDirPath}/${currentMonitorFileName}"
                    val execCmd = " touch \"${shellScriptName}\"; " +
                            "echo \"### \$(date \"+%Y/%m/%d-%H:%M:%S\") ${factExecCmd}\" " +
                            ">> \"${outputPath}\";" + "${factExecCmd} >> \"${outputPath}\"; "
                    ExecBashScriptIntent.ToTermux(
                        CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE,
                        context,
                        execCmd
                    )
                })
                .setNegativeButton("NO", null)
                .show()
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context?.getColor(R.color.black) as Int
            )
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                context.getColor(R.color.black)
            )
            alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        }
    }
}