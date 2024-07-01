package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsText(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun trimNewLine(con: String): String{
        /*
        desctiontion aa
        bb
        cc
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
        val linesSize = lines.split(separator).size
        return linesSize
    }

    @JavascriptInterface
    fun reverse(
        lines: String,
        separator: String,
    ): String {
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
