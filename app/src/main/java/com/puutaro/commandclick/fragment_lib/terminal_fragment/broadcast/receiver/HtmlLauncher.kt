package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.WebViewMenuMapType
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import java.io.File

object HtmlLauncher{

    fun launch(
        intent: Intent,
        context: Context,
        terminalFragment: TerminalFragment,
    ) {
        val binding = terminalFragment.binding
        val editFilePath = intent.getStringExtra(
            BroadCastIntentScheme.HTML_LAUNCH.scheme
        ) ?: return
        val title =
            editFilePath
                .replace(Regex("\\.[a-zA-Z0-9]*$"), "")
                .split('/')
                .lastOrNull()
                ?: return
        val parentDir = File(editFilePath).parent
            ?: return
        FileSystems.createDirs(parentDir)
        val htmlFileName = title
            .replace(" ", "_")
            .replace("　", "_") + ".html"
        val htmlFilePath = "${parentDir}/${htmlFileName}"
        val srcFilePath = intent.getStringExtra(
            BroadCastIntentExtraForHtml.SCR_PATH.scheme
        ) ?: String()
        val onClickSort = intent.getStringExtra(
            BroadCastIntentExtraForHtml.ON_CLICK_SORT.scheme
        ) ?: "false"
        val onSotableJs = intent.getStringExtra(
            BroadCastIntentExtraForHtml.ON_SORTABLE_JS.scheme
        ) ?: "true"
        val onClickUrl = intent.getStringExtra(
            BroadCastIntentExtraForHtml.ON_CLICK_URL.scheme
        ) ?: "true"
        val filterCode = intent.getStringExtra(
            BroadCastIntentExtraForHtml.FILTER_CODE.scheme
        ) ?: "true"
        val onDialog = intent.getStringExtra(
            BroadCastIntentExtraForHtml.ON_DIALOG.scheme
        ) ?: "false"
        val htmlContentsSource = AssetsFileManager.readFromAssets(
            context,
            "html/edit_urls_template.html"
        )
        if (
            htmlContentsSource.isEmpty()
        ) return
        val htmlContents =
            htmlContentsSource
                .replace(
                    Regex("const CommandClickSiteTitle =.*"),
                    "const CommandClickSiteTitle = \"${title}\""
                )
                .replace(
                    Regex("const editTargetUrlsFilePath =.*"),
                    "const editTargetUrlsFilePath = \"${editFilePath}\""
                )
                .replace(
                    Regex("const addUrlsSourceFilePath =.*"),
                    "const addUrlsSourceFilePath = \"${srcFilePath}\""
                )
                .replace(
                    Regex("const clickSortTop =.*"),
                    "const clickSortTop = ${onClickSort}"
                )
                .replace(
                    Regex("const onSortableJs =.*"),
                    "const onSortableJs = ${onSotableJs}"
                )
                .replace(
                    Regex("const onClickUrl =.*"),
                    "const onClickUrl = ${onClickUrl}"
                )
                .replace(
                    Regex("CommandClickFilterBoolean"),
                    filterCode
                )
                .replace(
                    Regex("const onDialog =.*"),
                    "const onDialog = ${onDialog}"
                )
        FileSystems.writeFile(
            parentDir,
            htmlFileName,
            htmlContents
        )
        if(
            onDialog == "true"
        ) {
            val iconName = WebViewMenuMapType.iconName.name
            val dismissType = WebViewMenuMapType.dismissType.name
            val menuMapStrListStr= listOf(
                    "${dismissType}=click!${iconName}=cancel"
            ).joinToString("!")
            JsDialog(terminalFragment).webView(
                htmlFilePath,
                String(),
                menuMapStrListStr,
                String(),
            )
        } else binding.terminalWebView.loadUrl(htmlFilePath)
    }
}