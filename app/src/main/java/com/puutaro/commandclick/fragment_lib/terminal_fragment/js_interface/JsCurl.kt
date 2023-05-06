package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.Intent.CurlManager


class JsCurl(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun get(
        mainUrl: String,
        queryParameter: String = String(),
        header: String = String(),
        timeout: Int
    ): String {
        return CurlManager.get(
            mainUrl,
            queryParameter,
            header,
            timeout
        )
    }
}