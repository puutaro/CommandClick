package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.Intent

object IntentExtra {

    fun add (
        intent: Intent,
        extraString: String,
        convertNumberType: ConvertNumberType
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
        convertNumberType: ConvertNumberType
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