package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment

class JsToast(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun short(
        contents: String,
    ) {
        Toast.makeText(
            context,
            contents,
            Toast.LENGTH_SHORT
        ).show()
    }

    @JavascriptInterface
    fun long(
        contents: String,
    ) {
        Toast.makeText(
            context,
            contents,
            Toast.LENGTH_LONG
        ).show()
    }
}