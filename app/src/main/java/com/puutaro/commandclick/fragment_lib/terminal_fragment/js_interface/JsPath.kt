package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
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
}

private enum class PathType {
    appFiles
}