package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.qr.Scanner


class JsQr(
    private val terminalFragment: TerminalFragment
) {
    val scanner = Scanner(
        terminalFragment,
        terminalFragment.currentAppDirPath
    )
    @JavascriptInterface
    fun scanFromImage(
        qrImagePath: String
    ): String {
        return scanner.scanFromImage(
            qrImagePath
        )
    }
}