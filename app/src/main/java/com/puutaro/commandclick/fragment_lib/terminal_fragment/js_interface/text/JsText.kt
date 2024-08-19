package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsText(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun removeAllQuote(
        con: String
    ): String {

        /*
       ## Description

       Remove all quote (double quote ,single quote, back quote)
       */
        return con.replace(Regex("[\"'`]"), String())
    }

    @JavascriptInterface
    fun trimNewLine(con: String): String{

        /*
        ## Description

        Remove blank line
        */

        val conByTrimNewLine = con.split("\n").map {
            it.trim()
        }.filter {
            it.isNotEmpty()
        }.joinToString("\n")
        return conByTrimNewLine
    }

    @JavascriptInterface
    fun countLines(
        lines: String,
        separator: String,
    ): Int {

        /*
        ## Description

        Count line num
        */
        val linesSize = lines.split(separator).size
        return linesSize
    }

    @JavascriptInterface
    fun reverse(
        lines: String,
        separator: String,
    ): String {

        /*
        ## Description

        Reverse contents
        */

        val reverseCon = lines
            .split(separator)
            .reversed()
            .joinToString(separator)
        return reverseCon
    }

    @JavascriptInterface
    fun take(
        con: String,
        separator: String,
        takeNum: Int,
    ): String{

        /*
        ## Description

        Take first ${takeNum} element
        */

        val takenCon = con.split(separator)
            .take(takeNum)
            .joinToString(separator)
        return takenCon
    }

    @JavascriptInterface
    fun takeLast(
        con: String,
        separator: String,
        takeLastNum: Int,
    ): String{

        /*
        ## Description

        Take last ${takeNum} element
        */

        val lastTakenCon = con.split(separator)
            .reversed()
            .take(takeLastNum)
            .joinToString(separator)
        return lastTakenCon
    }

    @JavascriptInterface
    fun distinct(
        con: String,
        separator: String,
    ): String {

        /*
        ## Description

        Distinct contents
        */

        val distinctCon = con.split(separator)
            .sorted()
            .distinct()
            .joinToString(separator)
        return distinctCon
    }

    @JavascriptInterface
    fun trans(
        tsvStr: String
    ): String {

        /*
        ## Description

        Trans contents
        */

        val tsvMatrix = tsvStr
            .split("\n")
            .map {
                it.split("\t")
            }
        val transCon = transpose(tsvMatrix).map {
            it.joinToString("\t")
        }.joinToString("\n")
        return transCon
    }

    fun <T> transpose(list: List<List<T>>): List<List<T>> =
        list.first().mapIndexed { index, _ ->
            list.map { row -> row[index] }
        }
}
