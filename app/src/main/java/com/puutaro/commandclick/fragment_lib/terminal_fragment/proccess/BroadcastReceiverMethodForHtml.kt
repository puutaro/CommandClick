package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import java.io.InputStream

object BroadcastReceiverMethodForHtml{

    fun launchHtml(
        intent: Intent,
        context: Context,
        binding: TerminalFragmentBinding,
        currentAppDirPath: String,
    ) {
        val title = intent.getStringExtra(
            BroadCastIntentScheme.HTML_LAUNCH.scheme
        ) ?: return
        val htmlFileName = title
            .lowercase()
            .replace(" ", "_")
            .replace("　", "_") + ".html"
        val editFilePath = intent.getStringExtra(
            BroadCastIntentExtraForHtml.EDIT_PATH.scheme
        ) ?: return
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
        binding.terminalWebView.loadUrl("${currentAppDirPath}/${htmlFileName}")
    }

}