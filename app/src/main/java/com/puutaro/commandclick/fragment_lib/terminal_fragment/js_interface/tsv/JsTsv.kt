package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.tsv

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsCon
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool

class JsTsv(
    private val terminalFragment: TerminalFragment,
) {

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
    fun getSr(
        path: String,
    ): String {
        val con = ReadText(
            path
        ).readText()
        return TsvTool.getSecondRow(
            con
        )
    }

    @JavascriptInterface
    fun getSrFromThis(
        path: String,
        thisLine: String,
    ): String {
        return execGetSecondRowBySortFromThis(
            path,
            thisLine,
        )
    }


    @JavascriptInterface
    fun getFr(
        path: String,
    ): String {
        val con = ReadText(
            path
        ).readText()
        return TsvTool.getFirstRow(
            con
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
    fun getSecondRowBySortFromThis(
        path: String,
        thisLine: String,
    ): String {
        return execGetSecondRowBySortFromThis(
            path,
            thisLine,
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

    @JavascriptInterface
    fun getKeyValue(
        path: String,
        key: String,
    ): String {
        return TsvTool.getKeyValue(
            path,
            key,
        )
    }

    @JavascriptInterface
    fun getKeyValueFromCon(
        con: String,
        key: String,
    ): String {
        val twoColumnNum = 2
        return TsvTool.filterByColumnNum(
            con.split("\n"),
            twoColumnNum
        ).firstOrNull {
            it.startsWith("${key}\t")
        }?.split("\t")
            ?.lastOrNull()
            ?: String()
    }

    private fun execGetSecondRowBySortFromThis(
        path: String,
        thisLine: String,
    ): String {
        val con = ReadText(
            path
        ).readText()
        val secondColCon = TsvTool.getSecondRow(
            con
        )
        return JsCon(terminalFragment).sortFromThis(
            secondColCon,
            thisLine
        )
    }
}