package com.puutaro.commandclick.util.file

import android.content.Context
import android.widget.Toast
import java.io.File

object NoFileChecker {

    private val throughMark = "-"
    private val blankListMark = "Let's press sync button at right bellow"

    fun isNoFile(
        context: Context?,
        parentDirPath: String,
        selectedItem: String,
        message: String = "No file"
    ): Boolean {
        val isNoFile = selectedItem == throughMark
                || selectedItem.trim() == blankListMark
                || !File(parentDirPath, selectedItem).isFile
        if(isNoFile){
            noFileToast(
                context,
                message,
            )
        }
        return isNoFile
    }

    private fun noFileToast(
        context: Context?,
        message: String = "No file"
    ){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}