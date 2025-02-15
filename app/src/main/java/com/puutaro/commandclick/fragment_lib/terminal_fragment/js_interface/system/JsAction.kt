package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsAction(
    private val terminalFragmentRef: WeakReference<TerminalFragment,>
) {

    @JavascriptInterface
    fun execByPath_S(
        jsActionPath: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            terminalFragment.context,
            jsActionPath,
        )
        val jsActionPairListCon = SettingFile.readFromList(
            ReadText(jsActionPath).textToList(),
            jsActionPath,
            setReplaceVariableMap
        )
//        val jsActionPairListCon = ReadText(
//            jsActionPath
//        ).readText()
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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val fannelInfoMap = FannelInfoTool.getFannelInfoMap(
            terminalFragment,
            mainOrSubFannelPath
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                terminalFragment.context,
                mainOrSubFannelPath
            )
        JsActionHandler.handle(
            terminalFragment,
            fannelInfoMap,
            mainOrSubFannelPath,
            setReplaceVariableMap,
            jsActionPairListCon
        )
    }
}