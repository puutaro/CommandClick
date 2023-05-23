package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.FormJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ListJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.MultiSelectJsDialog
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class JsDialog(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

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
        return try {
            formJsDialog.create(
                title,
                formSettingVariables,
                formCommandVariables
            )
        } catch(e: Exception){
            Toast.makeText(
                context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
            return String()
        }
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