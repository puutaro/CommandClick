package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.TextToSpeechIntentSender
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

class JsTextToSpeech(
    private val terminalFragment: TerminalFragment,
) {
    private val context = terminalFragment.context
    private val sharePref = terminalFragment.activity?.getPreferences(Context.MODE_PRIVATE)

    @JavascriptInterface
    fun speech(
        listFilePath: String,
        extraSettingMapStr: String,
    ) {
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
