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
        val replaceVariableTsvCon = SetReplaceVariabler.getReplaceVariablesTsv(
            context,
            currentPath
        )
        return replaceVariableTsvCon
    }

    @JavascriptInterface
    fun getValue(
        tsvCon: String,
        targetKey: String,
    ): String {
        val targetReplaceVariableValueStr = SetReplaceVariabler.getReplaceVariablesValue(
            tsvCon,
            targetKey
        )
        return targetReplaceVariableValueStr
    }

}