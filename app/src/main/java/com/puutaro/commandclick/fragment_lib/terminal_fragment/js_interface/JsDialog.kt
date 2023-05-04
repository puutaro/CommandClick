package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.FormJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ListJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.MultiSelectJsDialog
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class JsDialog(
    terminalFragment: TerminalFragment
) {

    val listJsDialog = ListJsDialog(
        terminalFragment,
    )

    val formJsDialog = FormJsDialog(
        terminalFragment
    )
    val multiSelectJsDialog = MultiSelectJsDialog(
        terminalFragment
    )

    @JavascriptInterface
    fun listDialog(
        title: String,
        message: String,
        listSource: String
    ): String {
        return listJsDialog.create(
            title,
            message,
            listSource
        )
    }

    @JavascriptInterface
    fun formDialog(
        title: String,
        formSettingVariables: String,
        formCommandVariables: String
    ): String{
        return formJsDialog.create(
            title,
            formSettingVariables,
            formCommandVariables
        )
    }

    @JavascriptInterface
    fun multiListDialog(
        title: String,
        currentItemListStr: String,
        preSelectedItemListStr: String,
    ): String {
        return multiSelectJsDialog.create(
            title,
            currentItemListStr,
            preSelectedItemListStr,
        )
    }
}