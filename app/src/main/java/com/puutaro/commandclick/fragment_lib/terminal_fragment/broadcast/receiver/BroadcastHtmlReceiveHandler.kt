package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForUrl
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.manager.TxtHtmlLauncher
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object BroadcastHtmlReceiveHandler {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val binding = terminalFragment.binding
        val urlStr = intent.getStringExtra(
            BroadCastIntentSchemeTerm.ULR_LAUNCH.scheme
        ) ?: return
        if(
            LoadUrlPrefixSuffix.judgeTextFile(urlStr)
        ) {
            TxtHtmlLauncher.launch(
                terminalFragment,
                urlStr,
            )
            return
        }

        if(
            !LoadUrlPrefixSuffix.judge(urlStr)
        ) return

        binding.terminalWebView.loadUrl(urlStr)
        execPageFinishedLoadCon(
            terminalFragment,
            intent,
        )
    }

    private fun execPageFinishedLoadCon(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val binding = terminalFragment.binding
        val pageFinishedLoadCon = intent.getStringExtra(
            BroadCastIntentExtraForUrl.PAGE_FINISHED_LOAD_CON.scheme
        )
        val beforeDelayMiliSec = intent.getStringExtra(
            BroadCastIntentExtraForUrl.BEFORE_DELAY_MILI_SEC.scheme
        )?.let {
            try{ it.toLong() } catch(e: Exception){0L}
        } ?: 0L
        if(
            pageFinishedLoadCon.isNullOrEmpty()
        ) return
        val appUrlSystemDirPath =
            "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val urlLoadFinished = UsePath.urlLoadFinished
        CoroutineScope(Dispatchers.IO).launch {
            val previousChecksum = withContext(Dispatchers.IO) {
                FileSystems.checkSum(
                    File(
                        appUrlSystemDirPath,
                        urlLoadFinished
                    ).absolutePath
                )
            }
            withContext(Dispatchers.IO) {
                for (i in 1..20) {
                    val updateChecksum = FileSystems.checkSum(
                        File(
                            appUrlSystemDirPath,
                            urlLoadFinished
                        ).absolutePath
                    )
                    if (
                        updateChecksum != previousChecksum
                    ) {
                        withContext(Dispatchers.IO){
                            delay(beforeDelayMiliSec)
                        }
                        withContext(Dispatchers.Main) {
                            binding.terminalWebView.loadUrl(
                                pageFinishedLoadCon
                            )
                        }
                        break
                    }
                    delay(100)
                }
            }
        }
    }
}