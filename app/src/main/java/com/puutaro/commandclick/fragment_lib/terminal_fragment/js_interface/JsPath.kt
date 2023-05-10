package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment

class JsPath(
    terminalFragment: TerminalFragment
) {

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
}