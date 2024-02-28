package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler

class JsReplaceVariables(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    @JavascriptInterface
    fun getTsv(
        currentPath: String,
    ): String {
        return SetReplaceVariabler.getReplaceVariablesTsv(
            context,
            currentPath
        )
    }

    @JavascriptInterface
    fun getValue(
        tsvCon: String,
        targetKey: String,
    ): String {
        return SetReplaceVariabler.getReplaceVariablesValue(
            tsvCon,
            targetKey
        )
    }

}