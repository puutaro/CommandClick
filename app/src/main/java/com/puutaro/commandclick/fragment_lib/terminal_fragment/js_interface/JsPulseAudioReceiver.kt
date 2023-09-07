package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.common.variable.PulseServerIntentExtra
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.PulseReceiverService



class JsPulseAudioReceiver(
    terminalFragment: TerminalFragment
) {

    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val pulseReceiverService = PulseReceiverService::class.java

    @JavascriptInterface
    fun start(
        pcIpv4Address: String,
    ) {
        val intent = Intent(
            activity,
            pulseReceiverService
        )
        intent.putExtra(
            PulseServerIntentExtra.serverAddress.schema,
            pcIpv4Address
        )
        context?.let {
            ContextCompat.startForegroundService(context, intent)
        }
    }

    @JavascriptInterface
    fun stop(){
        context?.stopService(
            Intent(
                activity,
                pulseReceiverService
            )
        )
    }
}