package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

class JsPath(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context

    @JavascriptInterface
    fun compPrefix(
        path: String,
        prefix: String
    ): String {
        return UsePath.compPrefix(
            path,
            prefix
        )
    }

    @JavascriptInterface
    fun compExtend(
        path: String,
        extend: String
    ): String {
        return UsePath.compExtend(
            path,
            extend
        )
    }

    @JavascriptInterface
    fun checkExtend(
        path: String,
        extendTabSeparateStr: String
    ): Boolean {
        val extendList = extendTabSeparateStr.split("\t")
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
        return prefixList.any {
            name.startsWith(it)
        }
    }

    @JavascriptInterface
    fun removeExtend(
        path: String,
        extend: String
    ): String {
        return path.removeSuffix(extend)
    }

    @JavascriptInterface
    fun removePrefix(
        path: String,
        prefix: String
    ): String {
        return path.removePrefix(prefix)
    }

    @JavascriptInterface
    fun trimAllExtend(
        path: String
    ): String {
        return path.replace(
            Regex("\\.[a-zA-Z0-9]*$"),
            ""
        )
    }

    @JavascriptInterface
    fun dirname(
        path: String,
    ): String {
        return File(path).parent ?: String()
    }

    @JavascriptInterface
    fun basename(
        path: String,
    ): String {
        return File(path).name
    }

    @JavascriptInterface
    fun echoPath(pathType: String): String {
        return when(pathType) {
            PathType.appFiles.name
            -> return "${context?.filesDir}"
            else -> String()
        }
    }

    @JavascriptInterface
    fun getFannelPath(path: String): String {
        return CcPathTool.getMainFannelFilePath(path)
    }

    @JavascriptInterface
    fun getFannelDirPath(path: String): String {
        return CcPathTool.getMainFannelDirPath(path)
    }

    @JavascriptInterface
    fun convertUbuntuPath(path: String): String {
        if(
            context == null
        ) return String()
        if(
            !path.startsWith("/")
        ) return path
        if(
            path.startsWith("/storage")
        ) return path

        val ubuntuFiles = UbuntuFiles(context)
        val filesOneRootfsPathPrfix = ubuntuFiles.filesOneRootfs.absolutePath
        return "${filesOneRootfsPathPrfix}${path}"
    }
}

private enum class PathType {
    appFiles
}