package com.puutaro.commandclick.util

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles

object CcPathTool {
    fun makeFannelDirName(
        fannelNameSrc: String
    ): String {
        if(
            judgeEmpty(fannelNameSrc)
        ) return String()
        val lastIndex = fannelNameSrc.lastIndexOf('.')
        val fannelName = if (lastIndex != -1) {
            fannelNameSrc.substring(0, lastIndex)
        } else fannelNameSrc
        return "${fannelName}Dir"
    }

    fun trimAllExtend(
        fileName: String
    ): String {
        val lastIndex = fileName.lastIndexOf('.')
        return if (lastIndex != -1) {
            fileName.substring(0, lastIndex)
        } else fileName
    }


    fun makeFannelRawName(
        fannelNameSrc: String
    ): String {
        return trimAllExtend(
            fannelNameSrc
        )
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
                ) return@let makeFannelDirName(it)
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

    fun convertUbuntuPath(
        context: Context?,
        path: String
    ): String {
        if (
            context == null
        ) return String()
        if (
            !path.startsWith("/")
        ) return path
        if (
            path.startsWith("/storage")
        ) return path

        val ubuntuFiles = UbuntuFiles(context)
        val filesOneRootfsPathPrfix = ubuntuFiles.filesOneRootfs.absolutePath
        return "${filesOneRootfsPathPrfix}${path}"
    }

    fun getCurrentScriptFileName(
        readSharePreffernceMap: Map<String, String>
    ): String {
        val currentScriptFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        if(
            currentScriptFileName.isEmpty()
            || currentScriptFileName == "-"
            || currentScriptFileName == CommandClickScriptVariable.EMPTY_STRING
        ) return UsePath.cmdclickStartupJsName
        return currentScriptFileName
    }

    private fun judgeEmpty(
        fannelName: String,
    ): Boolean {
        return fannelName.isEmpty()
                || fannelName == "-"
                || fannelName == CommandClickScriptVariable.EMPTY_STRING

    }
}