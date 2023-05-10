package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

object AppHistoryManager {
    fun makeAppHistoryFileNameForInit (
        currentAppDirPath: String,
        currentScriptFileName: String = String()
    ): String {
        val currentAppDirName = File(currentAppDirPath).name
        if(
            currentScriptFileName.isEmpty()
        ) return "${currentAppDirName}__${CommandClickScriptVariable.JS_FILE_SUFFIX}"
        return "${currentAppDirName}__${currentScriptFileName}"
    }


    fun getAppDirNameFromAppHistoryFileName(
        currentAppHistoryFileName: String
    ): String {
        return currentAppHistoryFileName.split(
            "__"
        ).firstOrNull() ?: CommandClickScriptVariable.EMPTY_STRING
    }

    fun getScriptFileNameFromAppHistoryFileName(
        currentAppHistoryFileName: String
    ): String {
        val appHistoryList = currentAppHistoryFileName.split("__")
        val appHistoryListLength = appHistoryList.size
        if(appHistoryListLength <= 1) return CommandClickScriptVariable.EMPTY_STRING
        if(appHistoryListLength == 2 && appHistoryList.last().isEmpty()) {
            return CommandClickScriptVariable.EMPTY_STRING
        }
        return appHistoryList.slice(
            1..appHistoryListLength - 1
        ).joinToString()
    }

    fun updateHomeFannelLastModify(
        homeFannelHistoryName: String,
        currentAppDirPath: String
    ){
        val fannelHistoryList = FileSystems.filterSuffixJsFiles(
            UsePath.cmdclickAppHistoryDirAdminPath
        )
        if(
            homeFannelHistoryName.isEmpty()
            || !fannelHistoryList.contains(
                homeFannelHistoryName
            )
        ) {
            updateFannelHistoryLastModify(
                currentAppDirPath
            )
            return
        }
        val updateAppDirPath = UsePath.cmdclickAppDirPath + "/" + getAppDirNameFromAppHistoryFileName(
            homeFannelHistoryName
        )
        val updateScriptName = getScriptFileNameFromAppHistoryFileName(
            homeFannelHistoryName
        ).let {
            if(
                it == CommandClickScriptVariable.EMPTY_STRING
            ) return@let String()
            it
        }
        updateFannelHistoryLastModify(
            updateAppDirPath,
            updateScriptName
        )
    }

    private fun updateFannelHistoryLastModify(
        currentAppDirPath: String,
        currentScriptFileName: String = String()
    ){
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            val updateAppHisotryFileName = makeAppHistoryFileNameForInit(
                currentAppDirPath,
                currentScriptFileName
            )
            FileSystems.updateLastModified(
                UsePath.cmdclickAppHistoryDirAdminPath,
                updateAppHisotryFileName
            )
        }
    }
}