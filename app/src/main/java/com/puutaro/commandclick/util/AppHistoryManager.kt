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
            val currentShellFileNameRemoveSuffix =
                currentShellFileName.removeSuffix(
                    CommandClickShellScript.SHELL_FILE_SUFFIX
                )
            return "${currentAppDirName}__${currentShellFileNameRemoveSuffix}" +
                    CommandClickShellScript.SHELL_FILE_SUFFIX
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
            val appHistoryFileName = currentAppHistoryFileName
                .removeSuffix(CommandClickShellScript.SHELL_FILE_SUFFIX)
            val appHistoryList = appHistoryFileName.split("__")
            val appHistoryListLength = appHistoryList.size
            if(appHistoryListLength <= 1) return CommandClickShellScript.EMPTY_STRING
            if(appHistoryListLength == 2 && appHistoryList.last().isEmpty()) {
                return CommandClickShellScript.EMPTY_STRING
            }
            return appHistoryList.slice(
                1..appHistoryListLength - 1
            ).joinToString() + CommandClickShellScript.SHELL_FILE_SUFFIX
        }

    }
}