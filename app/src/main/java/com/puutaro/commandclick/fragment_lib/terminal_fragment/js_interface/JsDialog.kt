package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.FormJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ListJsDialog

class JsDialog(
    terminalFragment: TerminalFragment
) {


    val listJsDialog = ListJsDialog(
        terminalFragment,
    )

    val formJsDialog = FormJsDialog(
        terminalFragment
    )

    @JavascriptInterface
    fun listDialog(
        listSource: String
    ): String {
        return listJsDialog.create(
            listSource
        )
    }

    @JavascriptInterface
    fun formDialog(
        formSource: String
    ): String{
        return formJsDialog.create(formSource)
    }
}