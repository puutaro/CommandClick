package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ConfirmDialogforKill {

    fun show(
        cmdIndexCommandIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        shellScriptName: String,
        currentMonitorFileName: String,
        cmdListView: RecyclerView
    ){


        val context = cmdIndexCommandIndexFragment.context
        val terminalViewModel: TerminalViewModel by cmdIndexCommandIndexFragment.activityViewModels()
        terminalViewModel.onDisplayUpdate = true
        val alertDialog = AlertDialog.Builder(cmdIndexCommandIndexFragment.context)
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

                CommandListManager.execListUpdateForCmdIndex(
                    currentAppDirPath,
                    cmdListView,
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