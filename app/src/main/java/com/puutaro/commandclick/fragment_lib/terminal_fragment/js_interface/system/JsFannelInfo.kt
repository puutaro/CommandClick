package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsFannelInfo(
    terminalFragment: WeakReference<TerminalFragment>
) {

    companion object {
        val fannelInfoMapSeparator = '|'
    }

    @JavascriptInterface
    fun make(
        currentFannelName: String = String(),
        currentFannelState: String = String()
    ): String {
        return listOf(
            "${FannelInfoSetting.current_fannel_name.name}=${currentFannelName}",
            "${FannelInfoSetting.current_fannel_state.name}=${currentFannelState}",
        ).joinToString(fannelInfoMapSeparator.toString())
    }
}