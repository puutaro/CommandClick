package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.util.state.FannelPrefGetter
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFannelConSaver(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = FannelPrefGetter.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = FannelPrefGetter.getCurrentStateName(
        readSharePreferenceMap
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