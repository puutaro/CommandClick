package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsNetTool(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun getIpv4(): String {
        /*
        Get IPV4 ADDRESS
        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context


        val ipV4Address = NetworkTool.getIpv4Address(context)
        return ipV4Address
    }
}