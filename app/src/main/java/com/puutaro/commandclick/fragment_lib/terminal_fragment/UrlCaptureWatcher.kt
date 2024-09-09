package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.proccess.history.url_history.UrlCaptureHistoryTool
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.abs
import java.time.LocalDateTime

object UrlCaptureWatcher {

    private var captureJob: Job? = null

    fun exit(){
        captureJob?.cancel()
    }

    fun watch(
        terminalFragment: TerminalFragment,
    ) {
        exit()
        captureJob = CoroutineScope(Dispatchers.IO).launch {
            if(
                terminalFragment.view?.height == 0
            ) return@launch
            val terminalWebView = withContext(Dispatchers.IO) {
                try {
                    terminalFragment.binding.terminalWebView
                } catch (e: Exception){
                    null
                }
            } ?: return@launch
            val pxWidth = withContext(Dispatchers.IO){
                ScreenSizeCalculator.pxWidth(terminalFragment)
            }
            val previousUrl = withContext(Dispatchers.Main) {
                terminalWebView.url
            }
            var previousYPosition = 0
            var prevCaptureTime = LocalDateTime.parse("2020-02-15T21:30:50")
            var isSameCapture = false
            withContext(Dispatchers.IO){
                while(true) {
                    delay(300)
                    val isNotWatch = withContext(Dispatchers.Main) isWatch@ {
                        if (
                            !terminalFragment.isVisible
                            || terminalFragment.view == null
                            || terminalFragment.view?.height == 0
                        ) return@isWatch true
                        false
                    }
                    if(
                        isNotWatch
                    ) {
                        return@withContext
                    }
                    val url = withContext(Dispatchers.Main) {
                        terminalWebView.url
                    }
                    if(
                        url.isNullOrEmpty()
                        || !EnableUrlPrefix.isHttpPrefix(url)
                        || url.contains("/maps/")
                        || url == "${WebUrlVariables.monitorUrlPath}/"
                        || url != previousUrl

                    ) {
//                        withContext(Dispatchers.Main){
//                            ToastUtils.showShort("exit1")
//                        }
                        return@withContext
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
                    previousYPosition = yPosition
                    if(
                        isSameCapture
                    ) continue
                    val curCaptureTime = LocalDateTime.now()
                    if(
                        LocalDatetimeTool.getDurationSec(prevCaptureTime, curCaptureTime) < 2
                    ){
                        continue
                    }
                    prevCaptureTime = curCaptureTime
                    val captureView = withContext(Dispatchers.Main){
                        terminalFragment.binding.terminalWebViewFrameLayout
                    }
//                    withContext(Dispatchers.Main){
//                        ToastUtils.showShort("shot")
//                    }
                    val title = withContext(Dispatchers.Main){
                        terminalWebView.title
                    }
                    val saveOk = CaptureSaver.save(
                        captureView,
                        url,
                        pxWidth,
                    )
                    if(
                        !saveOk
                    ) continue
                    isSameCapture = true
//
                }
            }
        }
    }

    private object CaptureSaver {

        suspend fun save(
            terminalWebViewFrameLayout: View?,
            url: String,
            pxWidth: Int,
        ): Boolean {
            val capture = withContext(Dispatchers.Main) {
                BitmapTool.getLowScreenShotFromView(terminalWebViewFrameLayout)
            } ?: let {
                return false
            }
            UrlCaptureHistoryTool.insertToHistory(
                url,
                capture,
                pxWidth,
            )
            return true
        }
    }
}