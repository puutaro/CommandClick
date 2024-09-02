package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsReplaceVariables(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun getTsv(
        currentPath: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context

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