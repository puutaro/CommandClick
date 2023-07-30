package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import java.io.File

object AppHistoryManager {

    private val jsSuffix = UsePath.JS_FILE_SUFFIX
    fun makeAppHistoryFileNameForInit (
        currentAppDirPath: String,
        currentScriptFileName: String = String()
    ): String {
        val currentAppDirName = File(currentAppDirPath).name
        if(
            currentScriptFileName.isEmpty()
        ) return "${currentAppDirName}__${UsePath.JS_FILE_SUFFIX}"
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
        ).joinToString().let {
            if(
                it == jsSuffix
            ) return@let String()
            it
        }
    }
}