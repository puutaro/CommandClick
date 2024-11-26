package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.tsv

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsCon
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import java.lang.ref.WeakReference

class JsTsv(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
) {

    @JavascriptInterface
    fun getFirstValue(
        path: String
    ): String {
        val firstValue = TsvTool.getFirstValue(
            path,
        )
        return firstValue
    }

    @JavascriptInterface
    fun getFirstKey(
        path: String
    ): String {
        val firstKey = TsvTool.getFirstKey(
            path
        )
        return firstKey
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
        val firstLine = TsvTool.getSecondRow(
            con
        )
        return firstLine
    }

    @JavascriptInterface
    fun getSrFromThis(
        path: String,
        thisLine: String,
    ): String {
        val secondFieldCon = execGetSecondRowBySortFromThis(
            path,
            thisLine,
        )
        return secondFieldCon
    }


    @JavascriptInterface
    fun getFr(
        path: String,
    ): String {
        val con = ReadText(
            path
        ).readText()
        val firstFieldCon = TsvTool.getFirstRow(
            con
        )
        return firstFieldCon
    }

    @JavascriptInterface
    fun getSecondRow(
        con: String,
    ): String {
        val secondFiledCon = TsvTool.getSecondRow(
            con
        )
        return secondFiledCon
    }

    @JavascriptInterface
    fun getSecondRowBySortFromThis(
        path: String,
        thisLine: String,
    ): String {
        val secondFieldCon = execGetSecondRowBySortFromThis(
            path,
            thisLine,
        )
        return secondFieldCon
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
        val firstKeyValue = TsvTool.getKeyValueFromFile(
            path,
            key,
        )
        return firstKeyValue
    }

    @JavascriptInterface
    fun getKeyValueFromCon(
        con: String,
        key: String,
    ): String {
        val twoColumnNum = 2
        val keyValue = TsvTool.filterByColumnNum(
            con.split("\n"),
            twoColumnNum
        ).firstOrNull {
            it.startsWith("${key}\t")
        }?.split("\t")
            ?.lastOrNull()
            ?: String()
        return keyValue
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
        return JsCon(terminalFragmentRef).sortFromThis(
            secondColCon,
            thisLine
        )
    }
}