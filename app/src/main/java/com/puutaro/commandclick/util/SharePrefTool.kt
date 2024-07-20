package com.puutaro.commandclick.util

import android.content.SharedPreferences

object SharePrefTool {
    fun putSharePref (
        sharePref: SharedPreferences?,
        sharedPrefKeyValeuMap: Map<String, String>
    ){
        if(
            sharePref == null
        ) return
        with(sharePref.edit()) {
            sharedPrefKeyValeuMap.forEach { currentKey, currentValue ->
                putString(
                    currentKey,
                    currentValue
                )
            }
            commit()
        }
    }

    fun removeSharePref (
        sharePref: SharedPreferences?,
        key: String
    ){
        if(
            sharePref == null
        ) return
        with(sharePref.edit()) {
            remove(key)
            commit()
        }
    }
}