package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.BroadcastHandlerForTerm
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import java.lang.ref.WeakReference

class JsPinFannel(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun update(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeTerm.FANNEL_PIN_BAR_UPDATE.action,
        )
    }
}