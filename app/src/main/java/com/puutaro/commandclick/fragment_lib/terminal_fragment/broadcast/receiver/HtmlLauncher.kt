package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.util.FileSystems
import java.io.File
import java.io.InputStream

object HtmlLauncher{

    fun launch(
        intent: Intent,
        context: Context,
        binding: TerminalFragmentBinding,
        currentAppDirPath: String,
    ) {
        val editFilePath = intent.getStringExtra(
            BroadCastIntentScheme.HTML_LAUNCH.scheme
        ) ?: return
        val title =
            editFilePath
                .split('/')
                .lastOrNull()
                ?: return
        val htmlFileName = title
            .replace(" ", "_")
            .replace("　", "_") + ".html"
        val htmlFilePath = "${currentAppDirPath}/${htmlFileName}"
        if(
            File(
                htmlFilePath
            ).isFile
        ) {
            binding.terminalWebView.loadUrl(htmlFilePath)
            return
        }
        val srcFilePath = intent.getStringExtra(
            BroadCastIntentExtraForHtml.SCR_PATH.scheme
        ) ?: String()
        val onClickSort = intent.getStringExtra(
            BroadCastIntentExtraForHtml.ON_CLICK_SORT.scheme
        ) ?: "false"
        val filterCode = intent.getStringExtra(
            BroadCastIntentExtraForHtml.FILTER_CODE.scheme
        ) ?: "true"
        val fis2: InputStream =
            context.assets?.open(
                "html/edit_urls_template.html"
            ) ?: return
        val htmlContents = try {
            fis2.bufferedReader().use {
                it
                    .readText()
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
                        Regex("CommandClickFilterBoolean"),
                        filterCode
                    )
            }
        }catch(e: Exception) {
            return
        }
        FileSystems.writeFile(
            currentAppDirPath,
            htmlFileName,
            htmlContents
        )
        binding.terminalWebView.loadUrl(htmlFilePath)
    }

}