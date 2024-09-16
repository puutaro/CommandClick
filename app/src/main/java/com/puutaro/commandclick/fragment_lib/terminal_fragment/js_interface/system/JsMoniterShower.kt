package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.lang.ref.WeakReference

class JsMonitorShower(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun show(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        TerminalShowByTerminalDo.show(
            editFragment,
        )
    }
}