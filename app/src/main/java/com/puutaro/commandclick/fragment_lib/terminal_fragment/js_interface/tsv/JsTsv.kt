package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.tsv

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.tsv.TsvTool

class JsTsv(
    terminalFragment: TerminalFragment,
) {

    private val towColumnNum = 2

    @JavascriptInterface
    fun getFirstValue(
        path: String
    ): String {
        return TsvTool.getFirstValue(
            path,
        )
    }

    @JavascriptInterface
    fun getFirstKey(
        path: String
    ): String {
        return TsvTool.getFirstKey(
            path
        )
    }

    @JavascriptInterface
    fun getFirstLine(
        path: String,
    ): String {
        return TsvTool.getFirstLine(
            path,
        )
    }

    @JavascriptInterface
    fun getSecondRow(
        con: String,
    ): String {
        return TsvTool.getSecondRow(
            con
        )
    }

    @JavascriptInterface
    fun getFirstRow(
        con: String,
    ): String {
        return TsvTool.getFirstRow(
            con
        )
    }
}