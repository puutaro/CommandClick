package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.TextToSpeechIntentExtra
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.CmdClickMap

object TextToSpeechIntentSender {

    private const val keySeparator = "|"

    fun send(
        context: Context?,
        currentAppDirName: String,
        fannelRawName: String,
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        try {
            exeSend(
                context,
                currentAppDirName,
                fannelRawName,
                listFilePath,
                extraSettingMapStr,
            )
        }catch (e: Exception){
            Toast.makeText(
                context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun exeSend(
        context: Context?,
        currentAppDirName: String,
        fannelRawName: String,
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        val textToSpeechIntent = Intent(context, TextToSpeechService::class.java)
        val extraSettingMap = CmdClickMap.createMap(
            extraSettingMapStr,
            keySeparator
        ).toMap()

        textToSpeechIntent.putExtra(
            TextToSpeechIntentExtra.listFilePath.scheme,
            listFilePath
        )
        extraSettingMap.get(TextToSpeechSchema.playMode.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.playMode.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechSchema.onRoop.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.onRoop.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechSchema.playNumber.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.playNumber.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechSchema.transMode.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.transMode.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechSchema.onTrack.name).let {
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.onTrack.scheme,
                it
            )
        }
        extraSettingMap.get(TextToSpeechSchema.speed.name)?.let {
            val floatSpeed = toFloatStr(it)
            textToSpeechIntent.putExtra(
                TextToSpeechIntentExtra.speed.scheme,
                floatSpeed
            )
        }
        extraSettingMap.get(TextToSpeechSchema.pitch.name)?.let {
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
        context?.startForegroundService(textToSpeechIntent)
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

enum class TextToSpeechSchema {
    playMode,
    onRoop,
    playNumber,
    transMode,
    onTrack,
    speed,
    pitch,
}