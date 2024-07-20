package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFannelConSaver(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
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
    fun save(){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ScriptFileSaver(editFragment).save()
    }
}