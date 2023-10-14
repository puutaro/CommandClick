package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.manager.TxtHtmlLauncher
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix

object BroadcastHtmlReceiveHandler {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val binding = terminalFragment.binding
        val urlStr = intent.getStringExtra(
            BroadCastIntentScheme.ULR_LAUNCH.scheme
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
    }
}