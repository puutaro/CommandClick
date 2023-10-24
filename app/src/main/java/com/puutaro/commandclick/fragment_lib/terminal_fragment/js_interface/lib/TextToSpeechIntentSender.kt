package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.TextToSpeechIntentExtra
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.CmdClickMap
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

object TextToSpeechIntentSender {

    private val keySeparator = "|"

    fun send(
        terminalFragment: TerminalFragment,
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        try {
            exeSend(
                terminalFragment,
                listFilePath,
                extraSettingMapStr,
            )
        }catch (e: Exception){
            Toast.makeText(
                terminalFragment.context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun exeSend(
        terminalFragment: TerminalFragment,
        listFilePath: String,
        extraSettingMapStr: String,
    ){
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
        val textToSpeechIntent = Intent(terminalFragment.activity, TextToSpeechService::class.java)
        val extraSettingMap = CmdClickMap.createMap(
            extraSettingMapStr,
            keySeparator
        ).toMap()

        textToSpeechIntent.putExtra(
            TextToSpeechIntentExtra.listFilePath.scheme,
            listFilePath
        )
        extraSettingMap.get(TextToSpeechShema.playMode.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.playMode.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechShema.onRoop.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.onRoop.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechShema.playNumber.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.playNumber.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechShema.transMode.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.transMode.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechShema.onTrack.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.onTrack.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechShema.speed.name)?.let {
            val floatSpeed = toFloatStr(it)
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.speed.scheme,
                floatSpeed
            )
        }
        extraSettingMap.get(TextToSpeechShema.pitch.name)?.let {
            val floatPitch = toFloatStr(it)
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.pitch.scheme,
                floatPitch
            )
        }
        textToSpeechIntent.putExtra(
            TextToSpeechIntentExtra.currentAppDirName.scheme,
            currentAppDirName
        )
        textToSpeechIntent.putExtra(
            TextToSpeechIntentExtra.scriptRawName.scheme,
            fannelRawName
        )
        terminalFragment.context?.startForegroundService(textToSpeechIntent)
    }

    private fun toFloatStr(
        intStr: String
    ): String {
        return try {
            val floatInt = intStr.toFloat()
            if(floatInt > 1000) return "1000"
            (intStr.toFloat() / 50).toString()
        } catch (e: Exception){
            "1"
        }
    }
}

private enum class TextToSpeechShema {
    playMode,
    onRoop,
    playNumber,
    transMode,
    onTrack,
    speed,
    pitch,
}