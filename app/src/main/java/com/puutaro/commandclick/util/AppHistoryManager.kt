package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import java.io.File

class AppHistoryManager {
    companion object {

        fun makeAppHistoryFileNameForInit (
            currentAppDirPath: String,
            currentShellFileName: String = String()
        ): String {
            val currentAppDirName = File(currentAppDirPath).name
            if(
                currentShellFileName.isEmpty()
            ) return "${currentAppDirName}__${CommandClickShellScript.JS_FILE_SUFFIX}"
            return "${currentAppDirName}__${currentShellFileName}"
        }


        fun getAppDirNameFromAppHistoryFileName(
            currentAppHistoryFileName: String
        ): String {
            return currentAppHistoryFileName.split(
                "__"
            ).firstOrNull() ?: CommandClickShellScript.EMPTY_STRING
        }

        fun getShellFileNameFromAppHistoryFileName(
            currentAppHistoryFileName: String
        ): String {
            val appHistoryList = currentAppHistoryFileName.split("__")
            val appHistoryListLength = appHistoryList.size
            if(appHistoryListLength <= 1) return CommandClickShellScript.EMPTY_STRING
            if(appHistoryListLength == 2 && appHistoryList.last().isEmpty()) {
                return CommandClickShellScript.EMPTY_STRING
            }
            return appHistoryList.slice(
                1..appHistoryListLength - 1
            ).joinToString()
        }

    }
}