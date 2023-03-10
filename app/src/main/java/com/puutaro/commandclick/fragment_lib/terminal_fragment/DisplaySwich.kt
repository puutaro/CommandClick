package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.*
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.HtmlDescriber
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*


class DisplaySwich {
    companion object {
        fun update (
            terminalFragment: TerminalFragment,
            terminalViewModel: TerminalViewModel,
        ): Job {
            try {
                terminalFragment.displayUpdateCoroutineJob?.cancel()
            } catch (e: Exception){
                println("not cancel")
            }
            return monitorOutput(
                terminalFragment,
                terminalViewModel
            )
        }
    }
}

private fun monitorOutput(
    terminalFragment: TerminalFragment,
    terminalViewModel: TerminalViewModel,
): Job {
    return terminalFragment.lifecycleScope.launch {
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED){
            var terminalContents = withContext(Dispatchers.IO){
                val readText = ReadText(
                    UsePath.cmdclickMonitorDirPath,
                    currentMonitorFileName,
                )
                readText.readTextForHtml()
            }
            withContext(Dispatchers.IO){
                terminalFragment.firstDisplayUpdateRunner = firstUpdateWebView(
                    terminalFragment,
                    terminalContents,
                    terminalViewModel,
                )
                if(terminalFragment.firstDisplayUpdate) {
                    registerRunner(
                        terminalViewModel,
                        terminalFragment.firstDisplayUpdateRunner,
                        terminalFragment.terminalViewhandler
                    )
                }
            }
            withContext(Dispatchers.IO){
                while (true) {
                    delay(200)
                    if(!terminalViewModel.onDisplayUpdate) continue
                    val currentMonitorFileNameLast = terminalViewModel.currentMonitorFileName
                    val readTextLast = ReadText(
                        UsePath.cmdclickMonitorDirPath,
                        currentMonitorFileNameLast,
                    )
                    val secondTerminalContents = readTextLast.readTextForHtml()
                    if(
                        terminalContents
                        == secondTerminalContents
                        && terminalViewModel.launchUrlList.isEmpty()
                    ){
                        continue
                    }
                    terminalContents = secondTerminalContents
                    terminalFragment.lastDisplayUpdateRunner = updateWebView(
                        terminalFragment,
                        terminalContents,
                        terminalViewModel,
                    )

                    registerRunner(
                        null,
                        terminalFragment.lastDisplayUpdateRunner,
                        terminalFragment.terminalViewhandler
                    )
                }
            }
        }
    }
}


private fun firstUpdateWebView(
    terminalFragment: TerminalFragment,
    terminalContents: String,
    terminalViewModel: TerminalViewModel
): Runnable {
    return Runnable {
        firstSetWebView(
            terminalFragment,
            terminalContents,
            terminalViewModel,
        )
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
                terminalFragment.terminalColor,
                terminalFragment.terminalFontColor,
                text,
                terminalViewModel.onBottomScrollbyJs
            ),
            "text/html",
            "utf-8",
            null
        );
    } catch(e: Exception){
        return
    }
}


private fun updateWebView(
    terminalFragment: TerminalFragment,
    terminalContents: String,
    terminalViewModel: TerminalViewModel
): Runnable {
    val launchUrl = terminalViewModel.launchUrlList.getOrNull(0)
    if(terminalViewModel.launchUrlList.size > 0) {
        terminalViewModel.launchUrlList.removeAt(0)
    }
    return Runnable {
        setWebView(
            terminalFragment,
            terminalContents,
            terminalViewModel,
            launchUrl
        )
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
                terminalFragment.terminalColor,
                terminalFragment.terminalFontColor,
                text,
                terminalViewModel.onBottomScrollbyJs
            ),
            "text/html",
            "utf-8",
            null
        );
    } catch(e: Exception){
        return
    }
}


private fun registerRunner(
    terminalViewModel: TerminalViewModel? = null,
    displayUpdateRunner: Runnable?,
    trminalViewhandler: Handler
){
    if(
        terminalViewModel != null
        && terminalViewModel.readlinesNum
        == ReadLines.LONGTH
    ) return
    val DisplayUpdateRunner = displayUpdateRunner as Runnable
    trminalViewhandler.post (
        DisplayUpdateRunner
    )
}
