package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object ListIndexDuplicate {
    fun isTsvDetect(
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


    fun isFileDetect(
//        parentDirPath: String,
        fileName: String
    ): Boolean {
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        if (
            File(cmdclickDefaultAppDirPath, fileName).isFile
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