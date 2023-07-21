package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.FormJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.GridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ImageJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ListJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.AsciiArtJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.MultiSelectGridViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.MultiSelectJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.MultiSelectOnlyImageGridViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.MultiSelectSpannableJsDialog
import com.puutaro.commandclick.util.QuoteTool

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

    val multiSelectGridViewJsDialog = MultiSelectGridViewJsDialog(
        terminalFragment
    )

    val multiSelectOnlyImageGridViewJsDialog = MultiSelectOnlyImageGridViewJsDialog(
        terminalFragment
    )

    val multiSelectSpannableJsDialog = MultiSelectSpannableJsDialog(
        terminalFragment
    )

    val gridJsDialog = GridJsDialog(
        terminalFragment
    )

    val asciiArtJsDialog = AsciiArtJsDialog(
        terminalFragment
    )

    val imageJsDialog = ImageJsDialog(
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
    fun getFormValue(
        targetVariableName: String,
        contentsTabSepaListCon: String
    ): String {
        val renameDirNameKeyValue = contentsTabSepaListCon
            .split("\t").filter {
            it.contains(targetVariableName)
        }.firstOrNull() ?: return String()
        if(renameDirNameKeyValue.isEmpty()) return String()
        return renameDirNameKeyValue
            .split("=")
            .lastOrNull()
            .let { QuoteTool.trimBothEdgeQuote(it) }
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

    @JavascriptInterface
    fun gridDialog(
        title: String,
        message: String,
        imagePathListTabSepaStr: String
    ): String {
        return gridJsDialog.create(
            title,
            message,
            imagePathListTabSepaStr
        )
    }

    @JavascriptInterface
    fun multiSelectGridDialog(
        title: String,
        message: String,
        imagePathListTabSepaStr: String
    ): String {
        return multiSelectGridViewJsDialog.create(
            title,
            message,
            imagePathListTabSepaStr
        )
    }

    @JavascriptInterface
    fun multiSelectOnlyImageGridDialog(
        title: String,
        message: String,
        imagePathListTabSepaStr: String
    ): String {
        return multiSelectOnlyImageGridViewJsDialog.create(
            title,
            message,
            imagePathListTabSepaStr
        )
    }

    @JavascriptInterface
    fun multiSelectSpannableGridDialog(
        title: String,
        message: String,
        imagePathListTabSepaStr: String
    ): String {
        return multiSelectSpannableJsDialog.create(
            title,
            message,
            imagePathListTabSepaStr
        )
    }

    @JavascriptInterface
    fun asciiArtDialog(
        title: String,
        imagePath: String
    ){
       asciiArtJsDialog.create(
           title,
           imagePath
       )
    }

    @JavascriptInterface
    fun imageDialog(
        title: String,
        imageSrcFilePath: String
    ){
        imageJsDialog.create(
            title,
            imageSrcFilePath
        )
    }
}