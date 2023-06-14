package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FileSystems
import java.io.File

object CopyAppDirEventForEdit {

    fun invoke(
        editFragment: EditFragment,
        currentAppDirPath: String,
        scriptScriptName: String,
    ){
        val context = editFragment.context
        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Input, destination App dir name"
            )
            .setMessage("\tcurrent app dir name: ${scriptScriptName}")
            .setView(editText)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                execCopyAppDir(
                    editFragment,
                    currentAppDirPath,
                    scriptScriptName,
                    editText
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

    private fun execCopyAppDir(
        editFragment: EditFragment,
        currentAppDirPath: String,
        scriptScriptName: String,
        editText: EditText
    ){
        val context = editFragment.context
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
                "/${scriptScriptName.removeSuffix(
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
    }
}
