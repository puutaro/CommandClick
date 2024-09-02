package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import java.lang.ref.WeakReference

class JsUrlHistoryLauncher(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun launch(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        UrlHistoryButtonEvent.invoke(terminalFragment)
    }

}