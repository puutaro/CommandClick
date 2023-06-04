package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.RecordToTextService


class JsRecordToText(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun start() {
        val intent = Intent(
            terminalFragment.activity, RecordToTextService::class.java
        )
        Toast.makeText(
            context,
            "aaa",
            Toast.LENGTH_LONG
        ).show()
        context?.startForegroundService(intent)
    }

    @JavascriptInterface
    fun stop(){
        context?.stopService(
            Intent(terminalFragment.activity, RecordToTextService::class.java)
        )
    }
}