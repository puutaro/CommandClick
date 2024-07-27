package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.AsciiArtJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.CopyJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.FormJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.GridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ImageJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.JsConfirm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.DebugJsAlert
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ListJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectGridViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectOnlyImageGridViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectSpannableJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.OnlyImageGridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.OnlySpannableGridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.QrScanJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WebViewJsDialog
import com.puutaro.commandclick.util.dialog.DialogObject
import com.puutaro.commandclick.util.str.QuoteTool

class JsDialog(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    private val promptJsDialog = PromptJsDialog(
        terminalFragment
    )

    private val jsConfirm = JsConfirm(
        terminalFragment
    )

    private val listJsDialog = ListJsDialog(
        terminalFragment,
    )

    private val formJsDialog = FormJsDialog(
        terminalFragment
    )

    private val multiSelectJsDialog = MultiSelectJsDialog(
        terminalFragment
    )

    private val multiSelectGridViewJsDialog = MultiSelectGridViewJsDialog(
        terminalFragment
    )

    private val multiSelectOnlyImageGridViewJsDialog = MultiSelectOnlyImageGridViewJsDialog(
        terminalFragment
    )

    private val multiSelectSpannableJsDialog = MultiSelectSpannableJsDialog(
        terminalFragment
    )

    private val gridJsDialog = GridJsDialog(
        terminalFragment
    )

    private val onlyImageGridJsDialog = OnlyImageGridJsDialog(
        terminalFragment
    )

    private val onlySpannableGridJsDialog = OnlySpannableGridJsDialog(
        terminalFragment
    )

    private val asciiArtJsDialog = AsciiArtJsDialog(
        terminalFragment
    )

    private val imageJsDialog = ImageJsDialog(
        terminalFragment
    )

    private val webViewJsDialog = WebViewJsDialog(
        terminalFragment
    )

    private val qrScanJsDialog = QrScanJsDialog(
        terminalFragment
    )

    private val debugJsAlert = DebugJsAlert(
        terminalFragment
    )

    @JavascriptInterface
    fun listDialog(
        title: String,
        message: String,
        listSource: String
    ): String {

        /*
        ## About listSource
        list item string separated by newline

        - Enable icon specify by tab second field

        ```js.js
        listSource=`
            ${item1}\t{icon name1}
            ${item2}\t{icon name2}
            3${item3}\t{icon name3}
            .
            .
            .
        `
        ```

        */
        val selectedItem = listJsDialog.create(
            title,
            message,
            listSource
        )
        return selectedItem
    }

    @JavascriptInterface
    fun prompt(
        title: String,
        message: String,
        suggestVars: String,
    ): String {
        val promptStr = promptJsDialog.create(
            title,
            message,
            suggestVars,
        )
        return promptStr
    }

    @JavascriptInterface
    fun textDialog_S(
        title: String,
        contents: String,
        scrollBottom: Boolean
    ) {
        DialogObject.simpleTextShow(
            context,
            title,
            contents,
            scrollBottom
        )
    }

    @JavascriptInterface
    fun formDialog(
        title: String,
        formSettingVariables: String,
        formCommandVariables: String
    ): String {
        if(
            formCommandVariables.trim().isEmpty()
        ) return String()
        try {
            val formValues = formJsDialog.create(
                title,
                formSettingVariables,
                formCommandVariables
            )
            return formValues
        } catch(e: Exception){
            ToastUtils.showLong(e.toString())
            return String()
        }
    }

    @JavascriptInterface
    fun getFormValue(
        targetVariableName: String,
        contentsNewlineSepaListCon: String
    ): String {
        val renameDirNameKeyValue = contentsNewlineSepaListCon
            .split("\n").filter {
            it.contains(targetVariableName)
        }.firstOrNull() ?: return String()
        if(
            renameDirNameKeyValue.isEmpty()
        ) return String()
        val targetFormValue = renameDirNameKeyValue
            .split("=")
            .lastOrNull()
            .let { QuoteTool.trimBothEdgeQuote(it) }
        return targetFormValue
    }

    @JavascriptInterface
    fun multiListDialog(
        title: String,
        currentItemListStr: String,
        preSelectedItemListStr: String,
    ): String {
        if(
            preSelectedItemListStr.trim().isEmpty()
        ) return String()
        val selectedLine = multiSelectJsDialog.create(
            title,
            currentItemListStr,
            preSelectedItemListStr,
        )
        return selectedLine
    }

    @JavascriptInterface
    fun gridDialog(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String
    ): String {
        if(
            imagePathListNewlineSepaStr.trim().isEmpty()
        ) return String()
        return gridJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
    }

    @JavascriptInterface
    fun onlyImageGridDialog(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String
    ): String {
        if(
            imagePathListNewlineSepaStr.trim().isEmpty()
        ) return String()
        val selectedLine =  onlyImageGridJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
        return selectedLine
    }

    @JavascriptInterface
    fun onlySpannableGridDialog(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String
    ): String {
        if(
            imagePathListNewlineSepaStr.trim().isEmpty()
        ) return String()
        val selectedLine =  onlySpannableGridJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
        return selectedLine
    }

    @JavascriptInterface
    fun multiSelectGridDialog(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String
    ): String {
        if(
            imagePathListNewlineSepaStr.trim().isEmpty()
        ) return String()
        val selectedLine = multiSelectGridViewJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
        return selectedLine
    }

    @JavascriptInterface
    fun multiSelectOnlyImageGridDialog(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String
    ): String {
        if(
            imagePathListNewlineSepaStr.trim().isEmpty()
        ) return String()
        val selectedLine = multiSelectOnlyImageGridViewJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
        return selectedLine
    }

    @JavascriptInterface
    fun multiSelectSpannableGridDialog(
        title: String,
        message: String,
        imagePathListNewlineSepaStr: String
    ): String {
        if(
            imagePathListNewlineSepaStr.trim().isEmpty()
        ) return String()
        val selectedLine = multiSelectSpannableJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
        return selectedLine
    }

    @JavascriptInterface
    fun asciiArtDialog(
        title: String,
        imagePath: String,
        asciiArtMapCon: String,
    ): Boolean {
        if(
            imagePath.trim().isEmpty()
        ) return false
        val createdAsciiArtPath = asciiArtJsDialog.create(
           title,
           imagePath,
           asciiArtMapCon,
       )
        return createdAsciiArtPath
    }

    @JavascriptInterface
    fun imageDialog(
        title: String,
        imageSrcFilePath: String,
        imageDialogMapCon: String,
    ): Boolean {
        val createdImagePath = imageJsDialog.create(
            title,
            imageSrcFilePath,
            imageDialogMapCon
        )
        return createdImagePath
    }

    @JavascriptInterface
    fun webView_S(
        urlStr: String,
        currentFannelPath: String,
        menuMapStrListStr: String,
        longPressMenuMapListStr: String,
        extraMapCon: String,
    ){
        webViewJsDialog.create(
            urlStr,
            currentFannelPath,
            menuMapStrListStr,
            longPressMenuMapListStr,
            extraMapCon,
        )
    }

    @JavascriptInterface
    fun webViewDismiss_S(){
        webViewJsDialog.dismiss()
    }

    @JavascriptInterface
    fun copyDialog_S(
        title: String,
        contents: String,
        scrollBottom: Boolean
    ){
        CopyJsDialog.create(
            title,
            terminalFragment,
            contents,
            scrollBottom
        )
    }

    @JavascriptInterface
    fun qrScan_S(
        title: String,
        currentScriptPath: String,
        callBackJsPath: String,
        menuMapStrListStr: String,
    ) {
        qrScanJsDialog.create(
            title,
            currentScriptPath,
            callBackJsPath,
            menuMapStrListStr
        )
    }

    @JavascriptInterface
    fun confirm(
        title: String,
        body: String,
    ): Boolean {
        val isOk = jsConfirm.create(
            title,
            body,
        )
        return isOk
    }

    @JavascriptInterface
    fun dAlert(
        title: String,
        con: String,
    ): String {
        if(
            con.isEmpty()
        ) return String()
        val conArg = debugJsAlert.create(
            title,
            con,
        )
        return conArg
    }
}
