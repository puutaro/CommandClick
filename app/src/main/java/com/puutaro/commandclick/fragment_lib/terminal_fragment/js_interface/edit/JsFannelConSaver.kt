package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.lang.ref.WeakReference

class JsFannelConSaver(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun save(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
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
        ScriptFileSaver.save(editFragment)
    }
}