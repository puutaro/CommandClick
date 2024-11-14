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
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.DragSortJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.EditListDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.GridJsDialogV2
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.JsConfirmV2
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ListJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ListJsDialogV2
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectGridViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectOnlyImageGridViewJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.MultiSelectSpannableJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.OnlyImageGridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.OnlySpannableGridJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptWithListDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.QrScanJsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.TitleJsDialog
import com.puutaro.commandclick.util.dialog.DialogObject
import com.puutaro.commandclick.util.str.QuoteTool
import java.lang.ref.WeakReference

class JsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    private val promptJsDialog = PromptJsDialog(
        terminalFragmentRef
    )

    private val jsConfirm = JsConfirmV2(
        terminalFragmentRef
    )

    private val listJsDialog = ListJsDialog(
        terminalFragmentRef,
    )

    private val formJsDialog = FormJsDialog(
        terminalFragmentRef
    )

    private val multiSelectJsDialog = MultiSelectJsDialog(
        terminalFragmentRef
    )

    private val multiSelectGridViewJsDialog = MultiSelectGridViewJsDialog(
        terminalFragmentRef
    )

    private val multiSelectOnlyImageGridViewJsDialog = MultiSelectOnlyImageGridViewJsDialog(
        terminalFragmentRef
    )

    private val multiSelectSpannableJsDialog = MultiSelectSpannableJsDialog(
        terminalFragmentRef
    )

    private val gridJsDialog = GridJsDialog(
        terminalFragmentRef
    )

    private val onlyImageGridJsDialog = OnlyImageGridJsDialog(
        terminalFragmentRef
    )

    private val onlySpannableGridJsDialog = OnlySpannableGridJsDialog(
        terminalFragmentRef
    )

    private val asciiArtJsDialog = AsciiArtJsDialog(
        terminalFragmentRef
    )

    private val imageJsDialog = ImageJsDialog(
        terminalFragmentRef
    )

//    private val webViewJsDialog = WebViewJsDialog(
//        terminalFragment
//    )

    private val qrScanJsDialog = QrScanJsDialog(
        terminalFragmentRef
    )

    private val debugJsAlert = DebugJsAlert(
        terminalFragmentRef
    )

    private val editListDialog = EditListDialog(
        terminalFragmentRef
    )

    private val promptWithListDialog = PromptWithListDialog(
        terminalFragmentRef,
    )

    private val dragSortJsDialog = DragSortJsDialog(
        terminalFragmentRef
    )

    private val gridDialogV2 = GridJsDialogV2(
        terminalFragmentRef
    )

    @JavascriptInterface
    fun listDialog(
        title: String,
        message: String,
        listSource: String
    ): String {

        /*
        ### About listSource
        list item string separated by newline

        - Enable icon specify by tab second field
        - ref [icon](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md)

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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
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
        currentItemListNewlineSepaStr: String,
        preSelectedItemListNewlineSepaStr: String,
    ): String {
        if(
            preSelectedItemListNewlineSepaStr.trim().isEmpty()
        ) return String()
        val selectedLine = multiSelectJsDialog.create(
            title,
            currentItemListNewlineSepaStr,
            preSelectedItemListNewlineSepaStr,
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
        webViewConfigMapCon: String,
//        menuMapStrListStr: String,
//        longPressMenuMapListStr: String,
//        extraMapCon: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.pocketWebViewManager?.show(
            urlStr,
            currentFannelPath,
            webViewConfigMapCon,
//            menuMapStrListStr,
//            longPressMenuMapListStr,
//            extraMapCon,
        )
    }

    @JavascriptInterface
    fun webViewDismiss_S(){

        /*
        This function is used to dismiss [pocket webview](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#highlight-search
        */
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        terminalFragment.pocketWebViewManager?.stopWebView()
    }

    @JavascriptInterface
    fun copyDialog_S(
        title: String,
        contents: String,
        scrollBottom: Boolean
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
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
        currentFannelPath: String,
        callBackJsPath: String,
        menuMapStrListStr: String,
    ) {
        qrScanJsDialog.create(
            title,
            currentFannelPath,
            callBackJsPath,
            menuMapStrListStr
        )
    }

    @JavascriptInterface
    fun confirm(
        title: String,
        message: String,
    ): Boolean {
        val isOk = jsConfirm.create(
            title,
            message,
        )
        return isOk
    }

//    @JavascriptInterface
//    fun confirmOld(
//        title: String,
//        body: String,
//    ): Boolean {
//        val isOk = jsConfirm.create(
//            title,
//            body,
//        )
//        return isOk
//    }

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

    @JavascriptInterface
    fun editList(
        fannelInfoCon: String,
        listIndexConfigPath: String
    ){
        editListDialog.create(
            fannelInfoCon,
            listIndexConfigPath,
        )
    }

    @JavascriptInterface
    fun promptWithList(
        fannelPath: String,
        title: String,
        promptConfigMapCon: String,
    ): String {
        return promptWithListDialog.create(
            fannelPath,
            title,
            promptConfigMapCon,
        )
    }

    @JavascriptInterface
    fun list(
        fannelPath: String,
        title: String,
        listIconTsvCon: String,
        promptConfigMapCon: String,
    ): String {
        return ListJsDialogV2.launch(
            promptWithListDialog,
            fannelPath,
            title,
            listIconTsvCon,
            promptConfigMapCon,
        )
    }

    @JavascriptInterface
    fun title(
        title: String,
    ){
        TitleJsDialog.launch(
            promptWithListDialog,
            title,
        )
    }

    @JavascriptInterface
    fun dragSort(
        title: String,
        dragSortFilePath: String
    ){
        dragSortJsDialog.create(
            title,
            dragSortFilePath
        )
    }

    @JavascriptInterface
    fun grid(
        title: String,
        imagePathListCon: String,
        configMapCon: String,
    ): String {
        return gridDialogV2.create(
            title,
            imagePathListCon,
            configMapCon,
        )
    }
}
