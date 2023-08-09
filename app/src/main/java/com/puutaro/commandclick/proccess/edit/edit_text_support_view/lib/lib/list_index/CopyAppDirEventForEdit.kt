package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FileSystems
import java.io.File

object CopyAppDirEventForEdit {

    fun execCopyAppDir(
        editFragment: EditFragment,
        currentAppDirPath: String,
        scriptScriptName: String,
        editText: AppCompatEditText
    ){
        val context = editFragment.context
        val destiDirNameEditable = editText.text
        if(destiDirNameEditable.isNullOrEmpty()) return
        val destiDirNameSource = destiDirNameEditable.toString()
        val jsFileSuffix = UsePath.JS_FILE_SUFFIX
        val destiDirName = if(
            destiDirNameSource.endsWith(jsFileSuffix)
        ){
            destiDirNameSource.removeSuffix(jsFileSuffix)
        } else destiDirNameSource
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val sourceAppDirPath = cmdclickAppDirPath +
                "/${scriptScriptName.removeSuffix(
                    UsePath.JS_FILE_SUFFIX
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
