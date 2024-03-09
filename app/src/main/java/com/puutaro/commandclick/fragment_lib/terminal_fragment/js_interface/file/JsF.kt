package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText

class JsF(
    terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun r(path: String): String {
        return ReadText(
            path
        ).readText()
    }

    @JavascriptInterface
    fun w(
        path: String,
        con: String,
    ){
        FileSystems.writeFile(
            path,
            con,
        )
    }
}