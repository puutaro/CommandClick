package com.puutaro.commandclick.util.Intent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentLauncher {

    fun send(
        context: Context?,
        action: String,
        uriString: String,
        extraListStrTabSepa: String,
        extraListIntTabSepa: String,
        extraListLongTabSepa: String,
        extraListFloatTabSepa: String,
        frag: Int? = null
    ){
        val intent = Intent()
        if(
            action.isNotEmpty()
        ) intent.action = Intent.ACTION_INSERT

        frag?.let {
            intent.flags = it
        }

        val eventUri = Uri.parse(uriString)
        if(
            uriString.isNotEmpty()
        ) intent.data = eventUri
        add(
            intent,
            extraListStrTabSepa,
            ConvertNumberType.String
        )
        add(
            intent,
            extraListIntTabSepa,
            ConvertNumberType.Int
        )
        add(
            intent,
            extraListLongTabSepa,
            ConvertNumberType.Long
        )
        add(
            intent,
            extraListFloatTabSepa,
            ConvertNumberType.Float
        )
        context?.startActivity(intent)
    }

    private fun add (
        intent: Intent,
        extraString: String,
        convertNumberType: ConvertNumberType,
    ){
        try {
            execAdd (
                intent,
                extraString,
                convertNumberType
            )
        } catch (e: Exception){
            return
        }
    }


    private fun execAdd (
        intent: Intent,
        extraString: String,
        convertNumberType: ConvertNumberType,
    ){
        val extraStringList = extraString.split("\t")
        extraStringList.forEach {
            if (!extraString.contains("\t")) return@forEach
            val currentKeyValue = it.split("=")
            val key = currentKeyValue.firstOrNull() ?: return@forEach
            val value = when (convertNumberType) {
                ConvertNumberType.String -> {
                    currentKeyValue.lastOrNull() ?: return@forEach
                }
                ConvertNumberType.Int -> {
                    currentKeyValue.lastOrNull()?.toInt() ?: return@forEach
                }
                ConvertNumberType.Long -> {
                    currentKeyValue.lastOrNull()?.toLong() ?: return@forEach
                }
                ConvertNumberType.Float -> {
                    currentKeyValue.lastOrNull()?.toFloat() ?: return@forEach
                }
            }
            intent.putExtra(key, value)
        }
    }


    enum class ConvertNumberType {
        String,
        Int,
        Long,
        Float,
    }
}