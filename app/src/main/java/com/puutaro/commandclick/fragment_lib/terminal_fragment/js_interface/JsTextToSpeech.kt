package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.intent.TextToSpeechIntentExtra
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

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
        transMode: String,
        onTrack: String,
        speed: String,
        pitch: String
    ) {
        try {
            val sharePref = terminalFragment.activity?.getPreferences(Context.MODE_PRIVATE)
            val currentAppDirName = SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            ).let {
                File(it).name
            }
            val fannelRawName = SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_script_file_name
            ).replace(
                Regex("\\.[a-zA-Z0-9]*$"),
                ""
            )
            val intent = Intent(terminalFragment.activity, TextToSpeechService::class.java)
            intent.putExtra(TextToSpeechIntentExtra.listFilePath.scheme, listFilePath)
            intent.putExtra(TextToSpeechIntentExtra.playMode.scheme, playMode)
            intent.putExtra(TextToSpeechIntentExtra.onRoop.scheme, onRoop)
            intent.putExtra(TextToSpeechIntentExtra.playNumber.scheme, playNumber)
            intent.putExtra(TextToSpeechIntentExtra.transMode.scheme, transMode)
            intent.putExtra(TextToSpeechIntentExtra.onTrack.scheme, onTrack)
            intent.putExtra(TextToSpeechIntentExtra.speed.scheme, speed)
            intent.putExtra(TextToSpeechIntentExtra.pitch.scheme, pitch)
            intent.putExtra(TextToSpeechIntentExtra.currentAppDirName.scheme, currentAppDirName)
            intent.putExtra(TextToSpeechIntentExtra.scriptRawName.scheme, fannelRawName)
            context?.startForegroundService(intent)
        } catch (e: Exception) {
            Toast.makeText(
                terminalFragment.context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
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