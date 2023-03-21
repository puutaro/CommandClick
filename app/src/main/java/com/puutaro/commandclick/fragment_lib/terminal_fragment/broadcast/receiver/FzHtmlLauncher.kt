package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.common.variable.BroadCastIntentExtraForFzHtml
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.databinding.TerminalFragmentBinding
import com.puutaro.commandclick.util.FileSystems
import java.io.File


object FzHtmlLauncher {
    fun launch(
        intent: Intent,
        context: Context,
        binding: TerminalFragmentBinding,
        currentAppDirPath: String,
    ) {
        val editPathSource = intent.getStringExtra(
            BroadCastIntentScheme.FZHTML_LAUNCH.scheme
        ) ?: return
        if(
            editPathSource.isEmpty()
        ) return
        val htmlFileName = "${editPathSource}.html"

        val promptMessage = intent.getStringExtra(
            BroadCastIntentExtraForFzHtml.TEMPLATE_PROMPT_MESSAGE.scheme
        ) ?: return
        Toast.makeText(
            context,
            "fz ${promptMessage}",
            Toast.LENGTH_LONG
        ).show()
        val itemClickJs = intent.getStringExtra(
            BroadCastIntentExtraForFzHtml.ITEM_CLICK_JAVASCRIPT.scheme
        ) ?: return
        val itemLongClickJs = intent.getStringExtra(
            BroadCastIntentExtraForFzHtml.ITEM_LONG_CLICK_JAVASCRIPT.scheme
        ) ?: String()
        val realTimeListSetJs = intent.getStringExtra(
            BroadCastIntentExtraForFzHtml.REAL_TIME_LIST_SET_JAVASCRIPT.scheme
        ) ?: return
        val htmlContentsSource = FileSystems.readFromAssets(
            context,
            "html/fizzy_search_templete.html"
        )
        if (
            htmlContentsSource.isEmpty()
        ) return
        val htmlContents =
            htmlContentsSource
                .replace(
                    BroadCastIntentExtraForFzHtml.TEMPLATE_PROMPT_MESSAGE.name,
                    promptMessage
                )
                .replace(
                    BroadCastIntentExtraForFzHtml.ITEM_CLICK_JAVASCRIPT.name,
                    itemClickJs
                )
                .replace(
                    BroadCastIntentExtraForFzHtml.ITEM_LONG_CLICK_JAVASCRIPT.name,
                    itemLongClickJs
                )
                .replace(
                    BroadCastIntentExtraForFzHtml.REAL_TIME_LIST_SET_JAVASCRIPT.name,
                    realTimeListSetJs
                )
        val htmlFilePath = "${currentAppDirPath}/${htmlFileName}"
        if(
            File(htmlFilePath).isFile
        ) return
        FileSystems.writeFile(
            currentAppDirPath,
            htmlFileName,
            htmlContents
        )
        binding.terminalWebView.loadUrl(htmlFilePath)
    }

}