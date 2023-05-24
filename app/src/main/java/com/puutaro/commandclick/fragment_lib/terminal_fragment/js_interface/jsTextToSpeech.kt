package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.TextToMp3IntentExtra
import com.puutaro.commandclick.common.variable.TextToSpeechIntentExtra
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.TextToMp3Service
import com.puutaro.commandclick.service.TextToSpeechService

class JsTextToSpeech(
    private val terminalFragment: TerminalFragment,
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun speech(
        listFilePath: String,
        playMode: String,
        onRoop: String,
        playNumber: String,
        englishMode: String,
        onTrack: String,
        speed: String,
        pitch: String
    ) {
        try {
            val intent = Intent(terminalFragment.activity, TextToSpeechService::class.java)
            intent.putExtra(TextToSpeechIntentExtra.listFilePath.scheme, listFilePath)
            intent.putExtra(TextToSpeechIntentExtra.playMode.scheme, playMode)
            intent.putExtra(TextToSpeechIntentExtra.onRoop.scheme, onRoop)
            intent.putExtra(TextToSpeechIntentExtra.playNumber.scheme, playNumber)
            intent.putExtra(TextToSpeechIntentExtra.englishMode.scheme, englishMode)
            intent.putExtra(TextToSpeechIntentExtra.onTrack.scheme, onTrack)
            intent.putExtra(TextToSpeechIntentExtra.speed.scheme, speed)
            intent.putExtra(TextToSpeechIntentExtra.pitch.scheme, pitch)
            context?.startService(intent)
        } catch (e: Exception) {
            Toast.makeText(
                terminalFragment.context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @JavascriptInterface
    fun stop(){
        context?.stopService(
            Intent(terminalFragment.activity, TextToSpeechService::class.java)
        )
    }

    @JavascriptInterface
    fun save(
        text: String,
        outDir: String,
        atomicName: String
    ) {
        try {
            val intent = Intent(terminalFragment.activity, TextToMp3Service::class.java)
            intent.putExtra(TextToMp3IntentExtra.text.scheme, text)
            intent.putExtra(TextToMp3IntentExtra.outDir.scheme, outDir)
            intent.putExtra(TextToMp3IntentExtra.atomicName.scheme, atomicName)
            context?.startForegroundService(intent)
        } catch (e: Exception) {
            Toast.makeText(
                terminalFragment.context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}