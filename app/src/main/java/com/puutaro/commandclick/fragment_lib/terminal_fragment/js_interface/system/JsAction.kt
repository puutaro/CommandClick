package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool

class JsAction(
    private val terminalFragment: TerminalFragment,
) {
    private val context = terminalFragment.context

    @JavascriptInterface
    fun execByPath_S(
        jsActionPath: String,
    ){
        val jsActionPairListCon = ReadText(
            jsActionPath
        ).readText()
        if(
            jsActionPairListCon.isEmpty()
        ) return
        exec_S(
            jsActionPairListCon,
            jsActionPath,
        )
    }

    @JavascriptInterface
    fun exec_S(
        jsActionPairListCon: String,
        mainOrSubFannelPath: String,
    ){
        val readSharePreferenceMap = SharePrefTool.getReadSharePrefMap(
            terminalFragment,
            mainOrSubFannelPath
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                context,
                mainOrSubFannelPath
            )
        JsActionHandler.handle(
            terminalFragment,
            readSharePreferenceMap,
            mainOrSubFannelPath,
            setReplaceVariableMap,
            jsActionPairListCon
        )
    }
}