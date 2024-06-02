package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsText(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun trimNewLine(con: String): String{
        return con.split("\n").map {
            it.trim()
        }.filter {
            it.isNotEmpty()
        }.joinToString("\n")
    }

    @JavascriptInterface
    fun take(
        con: String,
        separator: String,
        takeNum: Int,
    ): String{
        return con.split(separator)
            .take(takeNum)
            .joinToString(separator)
    }

    @JavascriptInterface
    fun distinct(
        con: String,
        separator: String,
    ): String {
        return con.split(separator)
            .sorted()
            .distinct()
            .joinToString(separator)
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
        return transpose(tsvMatrix).map {
            it.joinToString("\t")
        }.joinToString("\n")
    }

    fun <T> transpose(list: List<List<T>>): List<List<T>> =
        list.first().mapIndexed { index, _ ->
            list.map { row -> row[index] }
        }
}
