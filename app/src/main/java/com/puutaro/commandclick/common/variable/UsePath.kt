package com.puutaro.commandclick.common.variable

import android.os.Environment


class UsePath {
    companion object {
        val rootPath = Environment.getExternalStorageDirectory().absolutePath +
                "/" + Environment.DIRECTORY_DOCUMENTS
        val cmdclickDirName = "cmdclick"
        val cmdclickDirPath = "$rootPath/$cmdclickDirName"
        val cmdclickConfDirName = "conf"
        val cmdclickConfRelativePath = "$cmdclickDirName/$cmdclickConfDirName"
        val cmdclickConfDirPath =  "$rootPath/$cmdclickConfRelativePath"
        val cmdclickAppDirAdminName = "AppDirAdmin"
        val cmdclickAppDirAdminRelativePath = "$cmdclickConfRelativePath/$cmdclickAppDirAdminName"
        val cmdclickAppDirAdminPath =  "$rootPath/$cmdclickAppDirAdminRelativePath"
        val cmdclickAppHistoryDirName = "AppHistoryDir"
        val cmdclickAppHistoryDirRelativePath = "$cmdclickConfRelativePath/$cmdclickAppHistoryDirName"
        val cmdclickAppHistoryDirAdminPath =  "$rootPath/$cmdclickAppHistoryDirRelativePath"
        val cmdclickConfigDirName = "AppConfigDir"
        val cmdclickConfigDirRelativePath = "$cmdclickConfRelativePath/$cmdclickConfigDirName"
        val cmdclickConfigDirPath =  "$rootPath/$cmdclickConfigDirRelativePath"
        val cmdclickConfigFileName = "cmdclickConfig${CommandClickShellScript.SHELL_FILE_SUFFIX}"
        val cmdclickButtonExecShellFileName = "cmdclickButtonExec${CommandClickShellScript.SHELL_FILE_SUFFIX}"
        val cmdclickInternetButtonExecShellFileName = "internetButtonExec${CommandClickShellScript.SHELL_FILE_SUFFIX}"
        val cmdclickConfigFileRelativePath = "$cmdclickConfigDirRelativePath/$cmdclickConfigFileName"
        val cmdclickConfigFilePath =  "$rootPath/$cmdclickConfigFileRelativePath"
        val cmdclickNonitorDirName = "monitor"
        val cmdclickMonitorRelativeDirPath = "$cmdclickConfRelativePath/$cmdclickNonitorDirName"
        val cmdclickMonitorDirPath =  "$rootPath/$cmdclickMonitorRelativeDirPath"
        val RootDirPathByTermux = "\$HOME/storage/shared"
        val cmdclickMonitorFileNameSuffix = "term"
        val cmdClickMonitorFileName_1 = "${cmdclickMonitorFileNameSuffix}_1"
        val cmdClickMonitorFileName_2 = "${cmdclickMonitorFileNameSuffix}_2"
        val cmdClickMonitorFileName_3 = "${cmdclickMonitorFileNameSuffix}_3"
        val cmdClickMonitorFileName_4 = "${cmdclickMonitorFileNameSuffix}_4"
        val cmdclickAppDirParentName = "AppDir"
        val cmdclickAppDirRelativePath = "${cmdclickDirName}/${cmdclickAppDirParentName}"
        val cmdclickAppDirPath =  "$rootPath/$cmdclickAppDirRelativePath"
        val cmdclickDefaultAppDirName = "default"
        val cmdclickDefaultAppRelativePath = "$cmdclickAppDirRelativePath/$cmdclickDefaultAppDirName"
        val cmdclickDefaultAppDirPath =  "$rootPath/$cmdclickDefaultAppRelativePath"
        val cmdclickUrlHistoryFileName = "cmdclickUrlHistory"
        val cmdclickStartupShellName = "cmdclick_startup.sh"
        val cmdclickEndShellName = "cmdclick_end.sh"
        val cmdclickFirstHistoryTitle = "firstHistoryTitle"
        val urlLoadFinished = "urlLoadFinished"


        fun makeOmitPath(
            path: String
        ): String {
            val pathList = path.split("/")
            return pathList.lastOrNull().toString()

        }


        fun makeTermuxPathByReplace(
            path: String
        ): String {
            return path.replace(
                rootPath,
                RootDirPathByTermux
            )
        }
    }
}