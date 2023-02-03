package com.puutaro.commandclick.util

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting

class SharePreffrenceMethod {
    companion object {

        fun getStringFromSharePreffrence(
            sharedPref: SharedPreferences?,
            sharePrefferenceSetting: SharePrefferenceSetting
        ): String {
            val defaultStrValue = sharePrefferenceSetting.defalutStr
            if(sharedPref == null) return defaultStrValue
            return sharedPref.getString(
                sharePrefferenceSetting.name,
                defaultStrValue
            ) ?: defaultStrValue
        }


        fun putSharePreffrence (
            sharedPref: SharedPreferences?,
            sharedPrefKeyValeuMap: Map<String, String>
        ){
            if(sharedPref == null) return
            with(sharedPref.edit()) {
                sharedPrefKeyValeuMap.forEach { currentKey, currentValue ->
                    putString(
                        currentKey,
                        currentValue
                    )
                }
                commit()
            }
        }

        fun makeReadSharePreffernceMap(
            startUpPref: SharedPreferences?
        ): Map<String, String> {
            val sharedCurrentAppPath = getStringFromSharePreffrence(
                startUpPref,
                SharePrefferenceSetting.current_app_dir
            )

            val sharedCurrentShellFileName = getStringFromSharePreffrence(
                startUpPref,
                SharePrefferenceSetting.current_shell_file_name
            )

            val sharedOnShortcut = getStringFromSharePreffrence(
                startUpPref,
                SharePrefferenceSetting.on_shortcut
            )

            return mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to sharedCurrentAppPath,
                SharePrefferenceSetting.current_shell_file_name.name
                        to sharedCurrentShellFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to sharedOnShortcut,
            )
        }

        fun getReadSharePreffernceMap(
            readSharePreffernceMap: Map<String, String>,
            sharePrefferenceSetting: SharePrefferenceSetting
        ): String {
            return try {
                readSharePreffernceMap.get(
                    sharePrefferenceSetting.name
                ) ?: sharePrefferenceSetting.defalutStr
            } catch (e: Exception){
                sharePrefferenceSetting.defalutStr
            }
        }
    }
}