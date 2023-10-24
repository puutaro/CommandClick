package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.TextToSpeechIntentSender
import com.puutaro.commandclick.service.TextToSpeechService

class JsTextToSpeech(
    private val terminalFragment: TerminalFragment,
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun speech(
        listFilePath: String,
        extraSettingMapStr: String,
    ) {
        TextToSpeechIntentSender.send(
            terminalFragment,
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


    @JavascriptInterface
    fun save(
        text: String,
        outDir: String,
        atomicName: String
    ) {
//        try {
//            val intent = Intent(terminalFragment.activity, TextToMp3Service::class.java)
//            intent.putExtra(TextToMp3IntentExtra.text.scheme, text)
//            intent.putExtra(TextToMp3IntentExtra.outDir.scheme, outDir)
//            intent.putExtra(TextToMp3IntentExtra.atomicName.scheme, atomicName)
//            context?.startForegroundService(intent)
//        } catch (e: Exception) {
//            Toast.makeText(
//                terminalFragment.context,
//                e.toString(),
//                Toast.LENGTH_LONG
//            ).show()
//        }
    }
}
