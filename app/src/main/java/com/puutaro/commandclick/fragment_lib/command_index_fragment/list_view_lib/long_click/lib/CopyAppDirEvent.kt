package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class CopyAppDirEvent(
    cmdIndexCommandIndexFragment: CommandIndexFragment,
    private val currentAppDirPath: String,
    private val shellScriptName: String,
    private val cmdListAdapter: ArrayAdapter<String>,
) {

    private val context = cmdIndexCommandIndexFragment.context
    private val binding = cmdIndexCommandIndexFragment.binding
    private val cmdListView = binding.cmdList

    fun invoke(){
        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Input, destination App dir name"
            )
            .setMessage("\tcurrent app dir name: ${shellScriptName}")
            .setView(editText)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                execCopyAppDir(editText)
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

    private fun execCopyAppDir(
        editText: EditText
    ){
        val destiDirNameEditable = editText.text
        if(destiDirNameEditable.isNullOrEmpty()) return
        val destiDirNameSource = destiDirNameEditable.toString()
        val jsFileSuffix = CommandClickScriptVariable.JS_FILE_SUFFIX
        val destiDirName = if(
            destiDirNameSource.endsWith(jsFileSuffix)
        ){
            destiDirNameSource.removeSuffix(jsFileSuffix)
        } else destiDirNameSource
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val sourceAppDirPath = cmdclickAppDirPath +
                "/${shellScriptName.removeSuffix(
                    CommandClickScriptVariable.JS_FILE_SUFFIX
                )}"
        val destiAppDirPath = "${cmdclickAppDirPath}/${destiDirName}"
        val destiAppFileName = destiDirName + jsFileSuffix
        if(
            File(
                currentAppDirPath,
                destiAppFileName
            ).isFile
        ) {
            Toast.makeText(
                context,
                "Exist: ${destiDirName}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        FileSystems.createFiles(
            currentAppDirPath,
            destiAppFileName
        )
        FileSystems.copyDirectory(
            sourceAppDirPath,
            destiAppDirPath,
        )

        CommandListManager.execListUpdate(
            currentAppDirPath,
            cmdListAdapter,
            cmdListView,
        )
    }
}
