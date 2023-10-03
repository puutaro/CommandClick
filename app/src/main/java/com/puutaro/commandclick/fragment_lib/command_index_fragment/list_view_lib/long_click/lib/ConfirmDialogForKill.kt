package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ConfirmDialogForKill {


    private var killConfirmDialog: Dialog? = null

    fun show(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        fannelName: String,
        currentMonitorFileName: String,
        cmdListView: RecyclerView
    ){
        val context = cmdIndexFragment.context
            ?: return
        if(
            !fannelName.endsWith(UsePath.SHELL_FILE_SUFFIX)
        ){
            AppProcessManager.killDialog(
                cmdIndexFragment,
                currentAppDirPath,
                fannelName
            )
            return
        }
        val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
        terminalViewModel.onDisplayUpdate = true


        killConfirmDialog = Dialog(
            context
        )
        killConfirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            killConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text =  "Kill bellow shell path process, ok?"
        val confirmContentTextView =
            killConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = "\tpath: ${fannelName}"
        val confirmCancelButton =
            killConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            killConfirmDialog?.dismiss()
        }

        setOkButton(
            context,
            currentAppDirPath,
            fannelName,
            currentMonitorFileName,
            cmdListView
        )
        killConfirmDialog?.setOnCancelListener {
            killConfirmDialog?.dismiss()
        }
        killConfirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        killConfirmDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        killConfirmDialog?.show()
    }

    private fun setOkButton(
        context: Context,
        currentAppDirPath: String,
        shellScriptName: String,
        currentMonitorFileName: String,
        cmdListView: RecyclerView
    ){
        val confirmOkButton =
            killConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            killConfirmDialog?.dismiss()
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
        }
    }
}