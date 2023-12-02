package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath

object CcPathTool {
    fun makeFannelDirName(
        fannelNameSrc: String
    ): String {
        val lastIndex = fannelNameSrc.lastIndexOf('.')
        val fannelName = if (lastIndex != -1) {
            fannelNameSrc.substring(0, lastIndex)
        } else fannelNameSrc
        return "${fannelName}Dir"
    }

    fun makeFannelRawName(
        fannelNameSrc: String
    ): String {
        val lastIndex = fannelNameSrc.lastIndexOf('.')
        return if (lastIndex != -1) {
            fannelNameSrc.substring(0, lastIndex)
        } else fannelNameSrc
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

    fun convertIfFunnelRawNamePathToFullPath(
        currentAppDirPath: String,
        path: String
    ): String {
        return path.replace(
            Regex("^([^/])"),
            "${currentAppDirPath}/$1"
        ).replace(
            Regex("\n([^/])"),
            "\n${currentAppDirPath}/$1"
        )
    }
    fun extractCurrentDirPathFromPath(
        path: String
    ): String {
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        return path.replace(
            Regex("^(${cmdclickAppDirPath}/[^/]+).*"),
            "$1"
        ).replace(
            Regex("\n(${cmdclickAppDirPath}/[^/]+).*"),
            "\n$1"
        ).let {
            if(
                !it.startsWith(cmdclickAppDirPath)
            ) return@let String()
            it
        }
    }

    fun convertAppDirPathToLocal(
        path: String,
        currentAppDirPath: String,
        commonDirPath: String? = null
    ): String {
        return when(
            !commonDirPath.isNullOrEmpty()
        ) {
            true -> {
                val commonDirPathRegex = Regex("^${commonDirPath}")
                val commonDirPathRegexNewLine = Regex("\n${commonDirPath}")
                path.replace(
                    commonDirPathRegex,
                    currentAppDirPath
                ).replace(
                    commonDirPathRegexNewLine,
                    "\n$currentAppDirPath"
                )
            }
            else -> {
                val currentAppDirPathRegex = Regex("^${UsePath.cmdclickAppDirPath}/[^/]*")
                val currentAppDirPathRegexNewLine = Regex("\n${UsePath.cmdclickAppDirPath}/[^/]*")
                path.replace(
                    currentAppDirPathRegex,
                    currentAppDirPath
                ).replace(
                    currentAppDirPathRegexNewLine,
                    "\n$currentAppDirPath"
                )
            }
        }
    }
}