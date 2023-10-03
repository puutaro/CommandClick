package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object KillConfirmDialogForEdit {

    private var killConfirmDialog: Dialog? = null

    fun show(
        editFragment: EditFragment,
        currentAppDirPath: String,
        fannelName: String,
        currentMonitorFileName: String,
    ){
        val context = editFragment.context
            ?: return
        if(
            !fannelName.endsWith(UsePath.SHELL_FILE_SUFFIX)
        ) {
            AppProcessManager.killDialog(
                editFragment,
                currentAppDirPath,
                fannelName
            )
            return
        }
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
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

        val confirmOkButton =
            killConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            killConfirmDialog?.dismiss()
            val factExecCmd =
            "ps aux | grep \"${fannelName}\" " +
                    " | grep -v grep |  awk '{print \$2}' | xargs -I{} kill {} "
            val outputPath = "${UsePath.cmdclickMonitorDirPath}/${currentMonitorFileName}"
            val execCmd = " touch \"${fannelName}\"; " +
                    "echo \"### \$(date \"+%Y/%m/%d-%H:%M:%S\") ${factExecCmd}\" " +
                    ">> \"${outputPath}\";" + "${factExecCmd} >> \"${outputPath}\"; "
            ExecBashScriptIntent.ToTermux(
                CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE,
                context,
                execCmd
            )
        }

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
}