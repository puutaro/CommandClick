package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadCastSenderSchemaForCommon
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender

class JsBroadcast(
    terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context

    @JavascriptInterface
    fun send(
        broadCastMapStr: String
    ){
        val kyeSepalator = "|"
        val broadcastMap = mapOf<String, String>(
            BroadCastSenderSchemaForCommon.extras.name to broadCastMapStr
        )
        BroadcastSender.send(
            context,
            broadcastMap,
            kyeSepalator
        )
    }
}