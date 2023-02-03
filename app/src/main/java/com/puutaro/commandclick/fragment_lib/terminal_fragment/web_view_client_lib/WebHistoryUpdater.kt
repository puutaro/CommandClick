package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.webkit.WebView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.net.URLDecoder


class WebHistoryUpdater {
    companion object {
        fun webHistoryUpdater(
            terminalFragment: TerminalFragment,
            terminalViewModel: TerminalViewModel,
            webView: WebView?,
            url: String?,
        ) {
            val activity = terminalFragment.activity
            val context = terminalFragment.context
            val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
            val commandIndexFragment =
                TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                    activity,
                    cmdIndexFragmentTag
                )
            if(
                commandIndexFragment?.isVisible != true
            ) return
            val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
            val currentAppDirName = FileSystems.filterSuffixShellFiles(
                cmdclickAppDirAdminPath,
                "on"
            ).firstOrNull()?.removeSuffix(
                CommandClickShellScript.SHELL_FILE_SUFFIX
            ) ?: return
            if (terminalViewModel.readlinesNum == ReadLines.SHORTH) return

            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            val currentAppDirPath = "${cmdclickAppDirPath}/${currentAppDirName}"
            val ulrTitle = webView?.title ?: "-"
            val escapeStr = WebUrlVariables.escapeStr
            if (ulrTitle.endsWith("\t${escapeStr}")) return

            val urlCheckResult = EnableUrlPrefix.check(url)
            if(!urlCheckResult) return
            val searchViewTextSource = if(
                url?.startsWith(WebUrlVariables.queryUrl) == true
            ) {
                queryUrlToText(url)
            } else url ?:return
            val searchviewText = if(
                searchViewTextSource.startsWith(escapeStr)
            ) {
                String()
            } else searchViewTextSource
            val listener = terminalFragment.context as? TerminalFragment.OnSearchTextChangeListener
            listener?.onSearchTextChange(
                searchviewText,
            )
            if(searchviewText.isEmpty()) return
            val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
            val takeHistoryNum = 500
            val updatingHistory = "${ulrTitle}\t${url}\n" + ReadText(
                currentAppDirPath,
                cmdclickUrlHistoryFileName
            ).txetToList().take(takeHistoryNum).joinToString("\n")
            FileSystems.writeFile(
                currentAppDirPath,
                cmdclickUrlHistoryFileName,
                updatingHistory
            )

            registerUrlHistoryTitle(
                terminalFragment,
                currentAppDirPath,
                ulrTitle
            )

            val autoCompUpdateListner = terminalFragment.context as? TerminalFragment.OnAutoCompUpdateListener
            autoCompUpdateListner?.onAutoCompUpdate(
                currentAppDirPath,
            )
            terminalViewModel.onDisplayUpdate = false
        }

        private fun registerUrlHistoryTitle(
            terminalFragment: TerminalFragment,
            currentAppDirPath: String,
            ulrTitle: String
        ){
            val registerUrlTitle = if(
                terminalFragment.onHistoryUrlTitle !=
                CommandClickShellScript.CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE
            ) ulrTitle
            else String()
            FileSystems.writeFile(
                currentAppDirPath,
                UsePath.cmdclickFirstHistoryTitle,
                registerUrlTitle
            )
        }

    }
}


internal fun queryUrlToText(
    url: String
): String {
    val removedUrl = url.removePrefix(
        WebUrlVariables.queryUrl
    )
    val decordedUrl = URLDecoder.decode(removedUrl, "utf-8")
    val decordedUrlLength = decordedUrl.length

    val anpasadPosi = makeStrPosi(
        decordedUrl,
        decordedUrlLength,
        '&'
    )
    val sharpPosi = makeStrPosi(
        decordedUrl,
        decordedUrlLength,
        '#'
    )
    val subStrEndPosi = if(
        anpasadPosi > sharpPosi
    ){
        sharpPosi
    } else {
        anpasadPosi
    }
    return decordedUrl.substring(0, subStrEndPosi)
}


internal fun makeStrPosi(
    targetUrl: String,
    decordedUrlLength: Int,
    posiTargetChar: Char
): Int {
    val escapeStrNum = decordedUrlLength
    val sharpPosiSource = targetUrl.indexOf(posiTargetChar)
    return if (
        sharpPosiSource == -1
    ) {
        escapeStrNum
    } else {
        sharpPosiSource
    }

}