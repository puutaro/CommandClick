package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.AsciiArtJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.CopyJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.FormJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.GridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ImageJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.JsConfirm
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
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

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
    fun prompt(
        title: String,
        message: String,
        suggestVars: String,
    ): String {
        return promptJsDialog.create(
            title,
            message,
            suggestVars,
        )
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
    ): String{
        if(
            formCommandVariables.trim().isEmpty()
        ) return String()
        return try {
            formJsDialog.create(
                title,
                formSettingVariables,
                formCommandVariables
            )
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
        if(
            preSelectedItemListStr.trim().isEmpty()
        ) return String()
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
        return onlyImageGridJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
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
        return onlySpannableGridJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
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
        return multiSelectGridViewJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
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
        return multiSelectOnlyImageGridViewJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
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
        return multiSelectSpannableJsDialog.create(
            title,
            message,
            imagePathListNewlineSepaStr
        )
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
       return asciiArtJsDialog.create(
           title,
           imagePath,
           asciiArtMapCon,
       )
    }

    @JavascriptInterface
    fun imageDialog(
        title: String,
        imageSrcFilePath: String,
        imageDialogMapCon: String,
    ): Boolean {
        return imageJsDialog.create(
            title,
            imageSrcFilePath,
            imageDialogMapCon
        )
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
        return jsConfirm.create(
            title,
            body,
        )
    }
}
