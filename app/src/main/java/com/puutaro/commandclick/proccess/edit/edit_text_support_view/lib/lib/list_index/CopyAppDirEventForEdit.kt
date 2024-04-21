package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import android.widget.AutoCompleteTextView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object CopyAppDirEventForEdit {

    fun execCopyAppDir(
        currentAppDirPath: String,
        scriptScriptName: String,
        editText: AutoCompleteTextView
    ){
        val destiDirNameEditable = editText.text
        if(
            destiDirNameEditable.isNullOrEmpty()
        ) return
        val destiDirNameSource = destiDirNameEditable.toString()
        val jsFileSuffix = UsePath.JS_FILE_SUFFIX
        val destiDirName = if(
            destiDirNameSource.endsWith(jsFileSuffix)
        ){
            destiDirNameSource.removeSuffix(jsFileSuffix)
        } else destiDirNameSource
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val sourceAppDirPath = cmdclickAppDirPath +
                "/${scriptScriptName.removeSuffix(jsFileSuffix)}"
        val destiAppDirPath = "${cmdclickAppDirPath}/${destiDirName}"
        val destiAppFileName = destiDirName + jsFileSuffix
        if(
            File(
                currentAppDirPath,
                destiAppFileName
            ).isFile
        ) {
            ToastUtils.showShort("Exist: ${destiDirName}")
            return
        }
        FileSystems.createFiles(
            File(
                currentAppDirPath,
                destiAppFileName
            ).absolutePath
        )
        FileSystems.copyDirectory(
            sourceAppDirPath,
            destiAppDirPath,
        )
    }
}
