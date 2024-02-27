package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.qr_libs.ExecQr
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsExecQr(
    terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = SharePrefTool.getCurrentStateName(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun exec_S(
        clickFileName: String,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecQr.exec(
            editFragment,
            clickFileName,
        )
    }
}