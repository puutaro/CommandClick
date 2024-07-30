package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ListIndexDetector {
    fun isTsvDuplicate(
        tsvPath: String,
        fileNameOrTitle: String,
        fileNameOrCon: String
    ): Boolean{
        val alreadyTitleAndConList =
            TsvTool.flattenKeyValue(tsvPath)
        if(
            alreadyTitleAndConList.contains(fileNameOrTitle)
        ){
            alreadyExistToast(fileNameOrTitle)
            return true
        }
        if(
            alreadyTitleAndConList.contains(fileNameOrCon)
        ){
            alreadyExistToast(fileNameOrCon)
            return true
        }
        return false
    }


    fun isFileDuplication(
        parentDirPath: String,
        fileName: String
    ): Boolean {
        if (
            FileSystems.sortedFiles(
                parentDirPath
            ).contains(fileName)
        ){
            alreadyExistToast(fileName)
            return true
        }
        return false
    }

    private fun alreadyExistToast(con: String){
        CoroutineScope(Dispatchers.Main).launch{
            ToastUtils.showLong("Already exist: ${con}")
        }
    }
}