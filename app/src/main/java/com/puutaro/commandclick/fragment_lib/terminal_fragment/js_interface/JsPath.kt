package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CcPathTool
import java.io.File
import java.lang.ref.WeakReference

class JsPath(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun compPrefix(
        path: String,
        prefix: String
    ): String {
        val pathByCompPrefix = UsePath.compPrefix(
            path,
            prefix
        )
        return pathByCompPrefix
    }

    @JavascriptInterface
    fun compExtend(
        path: String,
        extend: String
    ): String {
        val pathByCompExtend = UsePath.compExtend(
            path,
            extend
        )
        return pathByCompExtend
    }

    @JavascriptInterface
    fun checkExtend(
        path: String,
        extendTabSeparateStr: String
    ): Boolean {
        val extendList = extendTabSeparateStr.split("&")
        return extendList.any {
            path.endsWith(it)
        }
    }

    @JavascriptInterface
    fun checkPrefix(
        name: String,
        prefixTabSeparateStr: String
    ): Boolean {
        val prefixList = prefixTabSeparateStr.split("\t")
        val isExist = prefixList.any {
            name.startsWith(it)
        }
        return isExist
    }

    @JavascriptInterface
    fun removeExtend(
        fileName: String,
        extendListCon: String
    ): String {
        val extendList = extendListCon.split('&')
        var removeSuffixFileName = fileName
        extendList.forEach {
            removeSuffixFileName = removeSuffixFileName.removeSuffix(it)
        }
        return removeSuffixFileName
    }

    @JavascriptInterface
    fun removePrefix(
        fileName: String,
        prefixListCon: String
    ): String {
        val prefixList = prefixListCon.split('&')
        var removePrefixFileName = fileName
        prefixList.forEach {
            removePrefixFileName = removePrefixFileName.removePrefix(it)
        }
        return removePrefixFileName
    }

    @JavascriptInterface
    fun trimAllExtend(
        path: String
    ): String {
        val pathByTrimAllExtend = CcPathTool.trimAllExtend(path)
        return pathByTrimAllExtend
    }

    @JavascriptInterface
    fun dirname(
        path: String,
    ): String {
        val parentDirPath = File(path).parent ?: String()
        return parentDirPath
    }

    @JavascriptInterface
    fun basename(
        path: String,
    ): String {
        val onlyBaseName = File(path).name
        return onlyBaseName
    }

    @JavascriptInterface
    fun echoPath(pathType: String): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context

        val appFileDirPath = when(pathType) {
            PathType.appFiles.name
            -> return "${context?.filesDir}"
            else -> String()
        }
        return appFileDirPath
    }

    @JavascriptInterface
    fun getFannelPath(path: String): String {
        val fannelPath = CcPathTool.getMainFannelFilePath(
            path
        )
        return fannelPath
    }

    @JavascriptInterface
    fun getFannelDirPath(path: String): String {
        val fannelDirPath = CcPathTool.getMainFannelDirPath(
            path
        )
        return fannelDirPath
    }

    @JavascriptInterface
    fun convertUbuntuPath(path: String): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context
        val ubuntuPath = CcPathTool.convertUbuntuPath(
            context,
            path
        )
        return ubuntuPath
    }
}

private enum class PathType {
    appFiles
}