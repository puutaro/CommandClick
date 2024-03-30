package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.judge

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText

class JsBeforeInfo(
    terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun match(
        beforeInfoPath: String,
        curInfo: String,
    ): Boolean {
        val beforeInfo = ReadText(
            beforeInfoPath
        ).readText()
        val isEqual =
            beforeInfo == curInfo
        FileSystems.writeFile(
            beforeInfoPath,
            curInfo
        )
        return isEqual
    }

    @JavascriptInterface
    fun misMatch(
        beforeInfoPath: String,
        curInfo: String,
    ): Boolean {
        val beforeInfo = ReadText(
            beforeInfoPath
        ).readText()
        val isEqual =
            beforeInfo != curInfo
        FileSystems.writeFile(
            beforeInfoPath,
            curInfo
        )
        return isEqual
    }
}