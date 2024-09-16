package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ServiceUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsService(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun isRun(className: String): Boolean{
        return ServiceUtils.isServiceRunning(className)
    }
}