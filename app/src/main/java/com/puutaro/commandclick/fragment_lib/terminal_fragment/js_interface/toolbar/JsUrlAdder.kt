package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.HistoryUrlContents

class JsUrlAdder(
    terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
        fannelInfoMap
    )

    @JavascriptInterface
    fun add_S(
        urlStringOrMacro: String,
        onSearchBtn: String,
    ){
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val targetFragmentInstance = TargetFragmentInstance()
        val editFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecJsLoad.execExternalJs(
            editFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.savePageUrlDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
            ),
        )
    }
}