package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFannelExecer(
    private val terminalFragment: TerminalFragment
) {

    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )

    @JavascriptInterface
    fun exec_S(){
        ExecJsOrSellHandler.handle(
            terminalFragment,
            currentAppDirPath,
            currentFannelName,
        )
    }
}