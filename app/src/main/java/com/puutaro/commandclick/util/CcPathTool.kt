package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath

object CcPathTool {
    fun makeFannelDirName(
        fannelName: String
    ): String {
        return fannelName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
    }

    fun getMainAppDirPath(
        currentSubFannelPath: String
    ): String {
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val fannelDirListLength = 2
        val pathListStartAppDirName = currentSubFannelPath.replace(
            "${cmdclickAppDirPath}/",
            ""
        ).split("/")
        if (
            pathListStartAppDirName.size < fannelDirListLength
        ) {
            LogSystems.stdErr("fannel dir not found: ${currentSubFannelPath}")
            return String()
        }
        val currentAppDirName =
            pathListStartAppDirName.first()
        return listOf(
            cmdclickAppDirPath,
            currentAppDirName,
        ).joinToString("/")
    }

    fun getMainFannelFilePath(
        currentSubFannelPath: String
    ): String {
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val fannelDirListLength = 2
        val pathListStartAppDirName = currentSubFannelPath.replace(
            "${cmdclickAppDirPath}/",
            ""
        ).split("/")
        if (
            pathListStartAppDirName.size < fannelDirListLength
        ) {
            LogSystems.stdErr("fannel dir not found: ${currentSubFannelPath}")
            return String()
        }
        val js_suffix = UsePath.JS_FILE_SUFFIX
        val currentAppDirName =
            pathListStartAppDirName.first()
        val mainFannelName =
            pathListStartAppDirName.take(2).last().let {
                if(it.endsWith(js_suffix)) return@let it
                it.removeSuffix("Dir") + js_suffix
            }
        return listOf(
            cmdclickAppDirPath,
            currentAppDirName,
            mainFannelName,
        ).joinToString("/")
    }

    fun getMainFannelDirPath(
        currentSubFannelPath: String
    ): String {
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val fannelDirListLength = 2
        val pathListStartAppDirName = currentSubFannelPath.replace(
            "${cmdclickAppDirPath}/",
            ""
        ).split("/")
        if (
            pathListStartAppDirName.size < fannelDirListLength
        ) {
            LogSystems.stdErr("fannel dir not found: ${currentSubFannelPath}")
            return String()
        }
        val currentAppDirName =
            pathListStartAppDirName.first()
        val mainFannelDirName =
            pathListStartAppDirName.take(2).last().let{
                if(
                    it.endsWith(UsePath.JS_FILE_SUFFIX)
                ) return@let CcPathTool.makeFannelDirName(it)
                it
            }
        return listOf(
            cmdclickAppDirPath,
            currentAppDirName,
            mainFannelDirName,
        ).joinToString("/")
    }
}