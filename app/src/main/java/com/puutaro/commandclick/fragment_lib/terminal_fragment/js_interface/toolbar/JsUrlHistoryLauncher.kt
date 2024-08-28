package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent

class JsUrlHistoryLauncher(
    terminalFragment: TerminalFragment
) {

    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(terminalFragment)

    @JavascriptInterface
    fun launch(){
        urlHistoryButtonEvent.invoke()
    }

}