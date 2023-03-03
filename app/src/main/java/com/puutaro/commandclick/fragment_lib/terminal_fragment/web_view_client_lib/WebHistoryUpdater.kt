package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder


class WebHistoryUpdater {
    companion object {

        fun webHistoryUpdater(
            terminalFragment: TerminalFragment,
            webView: WebView?,
            url: String?,
        ) {
            val activity = terminalFragment.activity
            val context = terminalFragment.context
            val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
            val cmdVariableEditFragmentTag = context?.getString(
                    R.string.cmd_variable_edit_fragment
                )
            val commandIndexFragment =
                TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                    activity,
                    cmdIndexFragmentTag
                )
            val cmdVariableEditFragment =
                TargetFragmentInstance().getFromFragment<EditFragment>(
                    activity,
                    cmdVariableEditFragmentTag
                )
            if(
                commandIndexFragment?.isVisible != true
                && cmdVariableEditFragment?.isVisible != true
            ) return

            val currentAppDirPath = terminalFragment.currentAppDirPath
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
            val searchViewText = if(
                searchViewTextSource.startsWith(escapeStr)
            ) {
                String()
            } else searchViewTextSource
            updateSearchViewString(
                terminalFragment,
                commandIndexFragment,
                searchViewText,
            )
            if(
                searchViewText.isEmpty()
            ) return
            val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
            val takeHistoryNum = 500
            val updatingHistory = "${ulrTitle}\t${url}\n" + ReadText(
                currentAppDirPath,
                cmdclickUrlHistoryFileName
            ).textToList().take(takeHistoryNum).joinToString("\n")
            FileSystems.writeFile(
                currentAppDirPath,
                cmdclickUrlHistoryFileName,
                updatingHistory
            )

            terminalFragment.registerUrlHistoryTitleCoroutineJob = terminalFragment.lifecycleScope.launch {
                terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    withContext(Dispatchers.IO) {
                        registerUrlHistoryTitle(
                            terminalFragment,
                            currentAppDirPath,
                            ulrTitle
                        )
                    }
                }
            }

            autoCompUpdater(
                terminalFragment,
                commandIndexFragment,
                currentAppDirPath,
            )
        }

        private fun autoCompUpdater(
            terminalFragment: TerminalFragment,
            commandIndexFragment: CommandIndexFragment?,
            currentAppDirPath: String,
        ){
            if(
                commandIndexFragment == null
            ) return
            if(
                !commandIndexFragment.isVisible
            ) return
            val autoCompUpdateListner = terminalFragment.context as? TerminalFragment.OnAutoCompUpdateListener
            autoCompUpdateListner?.onAutoCompUpdate(
                currentAppDirPath,
            )
        }

        private fun updateSearchViewString(
            terminalFragment: TerminalFragment,
            commandIndexFragment: CommandIndexFragment?,
            searchViewText: String,
        ) {
            if(
                commandIndexFragment == null
            ) return
            if(
                !commandIndexFragment.isVisible
            ) return
            val listener = terminalFragment.context as? TerminalFragment.OnSearchTextChangeListener
            listener?.onSearchTextChange(
                searchViewText,
            )
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
            if(
                registerUrlTitle.endsWith(
                    CommandClickShellScript.JS_FILE_SUFFIX
                )
                || registerUrlTitle.endsWith(
                    CommandClickShellScript.JSX_FILE_SUFFIX
                )
            ) return
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
