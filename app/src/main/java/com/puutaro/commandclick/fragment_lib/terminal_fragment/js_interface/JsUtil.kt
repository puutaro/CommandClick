package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment

class JsUtil(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun sleep(sleepMiriTime: Int){
        Thread.sleep(sleepMiriTime.toLong())
    }

    @JavascriptInterface
    fun copyToClipboard(text: String?) {
        val clipboard: ClipboardManager? =
            terminalFragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("cmdclick", text)
        clipboard?.setPrimaryClip(clip)
    }
}