package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsText(
    terminalFragment: TerminalFragment
) {

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
