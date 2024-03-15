package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.MediaPlayerIntentSender
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

class JsMusic(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val currentFannelName = terminalFragment.currentFannelName

    @JavascriptInterface
    fun play(
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        val currentAppDirName = File(currentAppDirPath).name
        val fannelRawName = CcPathTool.trimAllExtend(currentFannelName)
        MediaPlayerIntentSender.send(
            context,
            currentAppDirName,
            fannelRawName,
            listFilePath,
            extraSettingMapStr,
        )
    }

    @JavascriptInterface
    fun stop(){
        context?.stopService(
            Intent(terminalFragment.activity, TextToSpeechService::class.java)
        )
    }
}