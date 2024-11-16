package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.lifecycle.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.HtmlDescriber
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.TxtHtmlDescriber
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import java.io.File


object DisplaySwitch {

    private const val termUpdateMiliTime = 800L
    fun update (
        terminalFragment: TerminalFragment,
        terminalViewModel: TerminalViewModel,
    ): Job {
        val tag = terminalFragment.tag
        val isIndexTerminal = !tag.isNullOrEmpty()
                && tag == terminalFragment.context?.getString(R.string.index_terminal_fragment)
        return when(isIndexTerminal) {
            true -> urlLaunch(
                terminalFragment,
                terminalViewModel,
            )
            else -> monitorOutput(
                terminalFragment,
                terminalViewModel,
                termUpdateMiliTime
            )
        }
    }
}

private fun urlLaunch(
    terminalFragment: TerminalFragment,
    terminalViewModel: TerminalViewModel,
): Job {
    terminalFragment.firstDisplayUpdate = false
    return CoroutineScope(Dispatchers.IO).launch {
        terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED){
            withContext(Dispatchers.IO){
                while (true) {
                    delay(100)
                    if(!terminalViewModel.onDisplayUpdate) continue
                    if(
                        terminalViewModel.launchUrl.isNullOrEmpty()
                    ) continue
                    val launchUrl = terminalViewModel.launchUrl
                    terminalViewModel.launchUrl = null
                    withContext(Dispatchers.Main) {
                        setWebView(
                            terminalFragment,
                            String(),
                            terminalViewModel,
                            launchUrl
                        )
                    }
                }
            }
        }
    }
}

private fun monitorOutput(
    terminalFragment: TerminalFragment,
    terminalViewModel: TerminalViewModel,
    termUpdateMilitime: Long,
): Job {
    val tag = terminalFragment.tag
    val isIndexTerminal = !tag.isNullOrEmpty()
            && tag == terminalFragment.context?.getString(R.string.index_terminal_fragment)
    return CoroutineScope(Dispatchers.IO).launch {
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED){
            var terminalContents = withContext(Dispatchers.IO){
                val readText = ReadText(
                    File(
                        UsePath.cmdclickMonitorDirPath,
                        currentMonitorFileName,
                    ).absolutePath
                )
                readText.readTextForHtml()
            }
            withContext(Dispatchers.Main){
                if(
                    isIndexTerminal
                    || !terminalFragment.firstDisplayUpdate
                ) return@withContext
                firstSetWebView(
                    terminalFragment,
                    terminalContents,
                    terminalViewModel
                )
                terminalFragment.firstDisplayUpdate = false
            }
            var previousMilliTime = -1000L
            withContext(Dispatchers.IO){
                while (true) {
                    delay(termUpdateMilitime)
                    if(!terminalViewModel.onDisplayUpdate) continue
                    val currentTime= System.currentTimeMillis()
                    val diffTime = currentTime - previousMilliTime
                    if(
                        diffTime < 800
                    ) continue
                    previousMilliTime = currentTime
                    val currentMonitorFileNameLast = terminalViewModel.currentMonitorFileName
                    val readTextLast = ReadText(
                        File(
                            UsePath.cmdclickMonitorDirPath,
                            currentMonitorFileNameLast,
                        ).absolutePath
                    )
                    val secondTerminalContents =
                        readTextLast
                            .readTextForHtml()
                    if(
                        terminalContents
                        == secondTerminalContents
                        && terminalViewModel.launchUrl.isNullOrEmpty()
                    ) continue
                    terminalContents = secondTerminalContents
                    val launchUrl = terminalViewModel.launchUrl
                    terminalViewModel.launchUrl = null
                    withContext(Dispatchers.Main) {
                        setWebView(
                            terminalFragment,
                            terminalContents,
                            terminalViewModel,
                            launchUrl
                        )
                    }
                }
            }
        }
    }
}


private fun firstSetWebView(
    terminalFragment: TerminalFragment,
    text: String,
    terminalViewModel: TerminalViewModel,
) {
    try {
        val webView = terminalFragment.binding.terminalWebView
        webView.loadDataWithBaseURL(
            "",
            HtmlDescriber.make(
                terminalFragment,
                text,
                terminalViewModel
            ),
            "text/html",
            "utf-8",
            WebUrlVariables.monitorUrlPath
        )
    } catch(e: Exception){
        return
    }
}

private fun setWebView(
    terminalFragment: TerminalFragment,
    text: String,
    terminalViewModel: TerminalViewModel,
    launchUrl: String? = null
) {
    try {
        val webView = terminalFragment.binding.terminalWebView
        if(
            !launchUrl.isNullOrEmpty()
            && LoadUrlPrefixSuffix.judgeTextFile(launchUrl)
        ) {
            webView.loadDataWithBaseURL(
                "",
                TxtHtmlDescriber.make(
                    terminalFragment,
                    launchUrl,
                ),
                "text/html",
                "utf-8",
                null
            )
            return
        }
        if(
            !launchUrl.isNullOrEmpty()
            && LoadUrlPrefixSuffix.judge(launchUrl)
        ){
            webView.loadUrl(
                launchUrl
            )
            return
        }
        webView.loadDataWithBaseURL(
            "",
            HtmlDescriber.make(
                terminalFragment,
                text,
                terminalViewModel
            ),
            "text/html",
            "utf-8",
            WebUrlVariables.monitorUrlPath
        )
    } catch(e: Exception){
        return
    }
}
