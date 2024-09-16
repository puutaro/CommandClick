package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsSharePref(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun getFannelName(): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val fannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        return fannelName
    }

    @JavascriptInterface
    fun getAppDirPath(): String {
//        val getAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        return UsePath.cmdclickDefaultAppDirPath
    }

    @JavascriptInterface
    fun getState(): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentSate = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        return currentSate
    }
}