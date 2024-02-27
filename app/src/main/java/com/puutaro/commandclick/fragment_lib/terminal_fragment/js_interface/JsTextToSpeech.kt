package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.TextToSpeechIntentSender
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

class JsTextToSpeech(
    private val terminalFragment: TerminalFragment,
) {
    private val context = terminalFragment.context
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val currentFannelName = terminalFragment.currentFannelName

    @JavascriptInterface
    fun speech(
        listFilePath: String,
        extraSettingMapStr: String,
    ) {
        val currentAppDirName = File(currentAppDirPath).name
        val fannelRawName = CcPathTool.trimAllExtend(currentFannelName)
        TextToSpeechIntentSender.send(
            context,
            currentAppDirName,
            fannelRawName,
            listFilePath,
            extraSettingMapStr,
        )
    }

    @JavascriptInterface
    fun stopService(){
        context?.stopService(
            Intent(terminalFragment.activity, TextToSpeechService::class.java)
        )
    }


    @JavascriptInterface
    fun stop(){
        val textToSpeech = TextToSpeech(context, null)
        textToSpeech.stop()
        textToSpeech.shutdown()

    }
}
