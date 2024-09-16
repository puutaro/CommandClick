package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryRegister
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsUrlHistory(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun save(
        title: String,
        url: String,
    ){
        UrlHistoryRegister.insert(
//            currentAppDirPath,
            title,
            url,
        )
    }
}