package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.proccess.history.UrlCaptureHistoryTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.abs

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
            val terminalWebView = withContext(Dispatchers.IO) {
                terminalFragment.binding.terminalWebView
            }
            val previousUrl = withContext(Dispatchers.Main) {
                terminalWebView.url
            }
            var previousYPosition = 0
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
                    if(
                        isSameCapture
                    ) continue
                    previousYPosition = yPosition
                    val saveOk = CaptureSaver.save(
                        terminalFragment.view,
                        terminalFragment.currentAppDirPath,
                        url,
                    )
                    if(
                        !saveOk
                    ) continue
                    isSameCapture = true

                }
            }
        }
    }

    object CaptureSaver {

        private var isSaving = false
        suspend fun save(
            terminalFragmentView: View?,
            currentAppDirPath: String,
            url: String,
        ): Boolean {
            if(
                isSaving
            ) return false
            isSaving = true
            val capture = withContext(Dispatchers.Main) {
                BitmapTool.getLowScreenShotFromView(terminalFragmentView)
            } ?: return false
            UrlCaptureHistoryTool.insertToHistory(
                currentAppDirPath,
                url,
                capture
            )
            isSaving = false
            return true
        }
    }
}