package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.NetworkTool

class JsNetTool(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun getIpv4(): String {
        /*
        Get IPV4 ADDRESS
        */
        val ipV4Address = NetworkTool.getIpv4Address(context)
        return ipV4Address
    }
}