package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler
import androidx.lifecycle.*
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.HtmlDescriber
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
                terminalFragment.jobCoroutine?.cancel()
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
                terminalFragment.firstDisplayUpdateRunner = updateWebView(
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


private fun updateWebView(
    terminalFragment: TerminalFragment,
    terminalContents: String,
    terminalViewModel: TerminalViewModel
): Runnable {
    return Runnable {
        setWebView(
            terminalFragment,
            terminalContents,
            terminalViewModel
        )
    }
}


private fun setWebView(
    terminalFragment: TerminalFragment,
    text: String,
    terminalViewModel: TerminalViewModel
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

//private fun bottomTo(
//    terminalFragment: TerminalFragment,
//    binding: TerminalFragmentBinding
//) {
//    try {
//        val scrollViewTop = terminalFragment.binding.terminalFragment
//        scrollViewTop.post {
////            binding.terminalTextView.setTextIsSelectable(false)
//            Thread.sleep(10)
////            binding.terminalFragment.scrollTo(0, 100000)
////            binding.terminalFragment.scrollTo(0, view.getContentHeight());
////            scrollViewTop.fullScroll(View.FOCUS_DOWN)
////            binding.terminalTextView.setTextIsSelectable(true)
//        }
//    } catch (e: Exception){
//       return
//    }
//}


private fun takeLastLines(
    contensts: String,
    readlinesNum: Int
): String {
    return contensts.lines()
        .takeLast(readlinesNum)
        .joinToString("\n")
}