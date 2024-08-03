package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.proccess.history.UrlCaptureHistoryTool
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.abs

object UrlCaptureWatcher {
    fun watch(
        terminalFragment: TerminalFragment
    ): Job {
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        terminalFragment.lifecycleScope
        var previousYPosition = 0
        var previousUrl = String()
        var isSameCapture = false
        return terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO){
                    while(true) {
                        delay(300)
                        if(
                            !terminalFragment.isVisible
                            || terminalFragment.view == null
                            || terminalFragment.view?.height == 0
                        ) continue
                        val progress = withContext(Dispatchers.Main) {
                            terminalWebView.progress
                        }
                        if(progress < 100) {
                            continue
                        }
                        val url = withContext(Dispatchers.Main) {
                            terminalWebView.url
                        }
                        if(
                            url.isNullOrEmpty()
                            || !EnableUrlPrefix.isHttpPrefix(url)
                            || url.contains("/maps/")
                        ) continue
                        if(
                            url != previousUrl
                        ){
                            previousYPosition = 0
                            previousUrl = url
                            delay(2000)
                            continue
                        }
                        val yPosition = withContext(Dispatchers.Main) {
                            terminalWebView.scrollY
                        }
                        val yPosiDiff = abs(yPosition - previousYPosition)
                        if(yPosiDiff != 0) {
                            previousYPosition = yPosition
                            isSameCapture = false
                            continue
                        }
                        if(isSameCapture) continue
                        previousYPosition = yPosition
                        val capture = withContext(Dispatchers.Main) {
                            BitmapTool.getLowScreenShotFromView(terminalFragment.view)
                        } ?: continue
                        UrlCaptureHistoryTool.insertToHistory(
                            terminalFragment.currentAppDirPath,
                            url,
                            capture
                        )
                        isSameCapture = true

                    }
                }
            }
        }
    }
}