package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WebViewMenuMapType
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File
import java.lang.ref.WeakReference

object HtmlLauncher{

    fun launch(
        intent: Intent,
        terminalFragment: TerminalFragment,
    ){
        try {
            execLaunch(
                intent,
                terminalFragment,
            )
        } catch (e: Exception){
            LogSystems.stdWarn(e.toString())
            return
        }
    }

    fun execLaunch(
        intent: Intent,
        terminalFragment: TerminalFragment,
    ) {
        val context = terminalFragment.context
        val binding = terminalFragment.binding
        val editFilePath = intent.getStringExtra(
            BroadCastIntentSchemeTerm.HTML_LAUNCH.scheme
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
        val latestUrlTitleFilterCode = intent.getStringExtra(
            BroadCastIntentExtraForHtml.LATST_URL_TITLE_FILTER_CODE.scheme
        ) ?: "(function(){return latestUrlTitleSrc;})();"
        val onDialog = intent.getStringExtra(
            BroadCastIntentExtraForHtml.ON_DIALOG.scheme
        ) ?: "false"
        val extraJsPathList = intent.getStringExtra(
            BroadCastIntentExtraForHtml.EXTRA_JS_PATH_LIST.scheme
        ) ?: String()
        val extraLabel = intent.getStringExtra(
            BroadCastIntentExtraForHtml.EXTRA_LABEL.scheme
        ) ?: String()
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
                    Regex("const latestUrlTitle =.*"),
                    "const latestUrlTitle = ${latestUrlTitleFilterCode}"
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
                .replace(
                    "CMDCLICK_EXTRA_JS_PATH_LIST",
                    extraJsPathList,
                )
                .replace(
                    Regex("const extraLabel =.*"),
                    "const extraLabel = \"${extraLabel}\";",
                )
        FileSystems.writeFile(
            htmlFilePath,
            htmlContents
        )
        if(
            onDialog == "true"
        ) {
            val caption = WebViewMenuMapType.caption.name
            val iconName = WebViewMenuMapType.iconName.name
            val dismissType = WebViewMenuMapType.dismissType.name
            val menuMapStrListStr= listOf(
                    "${dismissType}=click?${caption}=cancel?${iconName}=cancel"
            ).joinToString("?")
            JsDialog(WeakReference(terminalFragment)).webView_S(
                htmlFilePath,
                String(),
                menuMapStrListStr,
                String(),
                String()
            )
        } else binding.terminalWebView.loadUrl(htmlFilePath)
    }
}