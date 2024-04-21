package com.puutaro.commandclick.util.file

import com.blankj.utilcode.util.ToastUtils
import java.io.File

object NoFileChecker {

    private val throughMark = "-"
    private val blankListMark = "Let's press sync button at right bellow"

    fun isNoFile(
        parentDirPath: String,
        selectedItem: String,
        message: String = "No file"
    ): Boolean {
        val isNoFile = selectedItem == throughMark
                || selectedItem.trim() == blankListMark
                || !File(parentDirPath, selectedItem).isFile
        if(isNoFile){
            noFileToast(message)
        }
        return isNoFile
    }

    private fun noFileToast(
        message: String = "No file"
    ){
        ToastUtils.showShort(message)
    }
}