package com.puutaro.commandclick.proccess.history.fannel_history

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import java.io.File

object FannelHistoryManager {

    private val jsSuffix = UsePath.JS_FILE_SUFFIX
    fun makeAppHistoryFileNameForInit (
//        currentAppDirPath: String,
        currentScriptFileName: String? = null
    ): String {
        val currentAppDirName = File(UsePath.cmdclickDefaultAppDirPath).name
        if(
            currentScriptFileName.isNullOrEmpty()
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

    fun getFannelNameFromAppHistoryFileName(
        currentAppHistoryFileName: String
    ): String {
        val appHistoryList =
            currentAppHistoryFileName.split("__")
        val appHistoryListLength = appHistoryList.size
        if(
            appHistoryListLength <= 1
        ) return CommandClickScriptVariable.EMPTY_STRING
        if(
            appHistoryListLength == 2
            && appHistoryList.last().isEmpty()
        ) return CommandClickScriptVariable.EMPTY_STRING
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