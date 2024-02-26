package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelPrefGetter
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsAction(
    private val terminalFragment: TerminalFragment,
) {

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
        val readSharePreferenceMap = FannelPrefGetter.getReadSharePreferenceMap(
            terminalFragment,
            mainOrSubFannelPath
        )
        JsActionHandler.handle(
            terminalFragment,
            readSharePreferenceMap,
            mainOrSubFannelPath,
            jsActionPairListCon
        )
    }
}