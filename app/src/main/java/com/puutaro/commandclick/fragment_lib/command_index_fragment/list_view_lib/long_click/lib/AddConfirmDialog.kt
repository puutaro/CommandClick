package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.CommandListManager
import com.puutaro.commandclick.util.FileSystems


class AddConfirmDialog {
    companion object {
        fun show(
            cmdIndexFragment: CommandIndexFragment,
            cmdListAdapter: ArrayAdapter<String>,
            currentAppDirPath: String,
            cmdListView: ListView,
        ) {

            val context = cmdIndexFragment.context
            val editText = EditText(context)
            editText.inputType = InputType.TYPE_CLASS_TEXT
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(
                    "Input create app directory name"
                )
                .setView(editText)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    val inputShellFileName = editText.text.toString()
                    val shellFileSuffix = CommandClickShellScript.SHELL_FILE_SUFFIX
                    val isShellSuffix = inputShellFileName.endsWith(shellFileSuffix)
                    val shellFileName = if (isShellSuffix) {
                        inputShellFileName
                    } else {
                        inputShellFileName + shellFileSuffix
                    }

                    CommandClickShellScript.makeAppDirAdminFile(
                        UsePath.cmdclickAppDirAdminPath,
                        shellFileName
                    )
                    val createAppDirName = if (
                        isShellSuffix
                    ) {
                        inputShellFileName.removeSuffix(shellFileSuffix)
                    } else {
                        inputShellFileName
                    }
                    FileSystems.createDirs(
                        "${UsePath.cmdclickAppDirPath}/${createAppDirName}"
                    )
                    CommandListManager.execListUpdate(
                        currentAppDirPath,
                        cmdListAdapter,
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
}