package com.puutaro.commandclick.util.state

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting

object SharePreferenceMethod {

    fun getStringFromSharePreference(
        sharedPref: SharedPreferences?,
        sharePreferenceSetting: SharePrefferenceSetting
    ): String {
        val defaultStrValue = sharePreferenceSetting.defalutStr
        if(sharedPref == null) return defaultStrValue
        return sharedPref.getString(
            sharePreferenceSetting.name,
            defaultStrValue
        ) ?: defaultStrValue
    }

    fun putAllSharePreference(
        sharedPref: SharedPreferences?,
        currentAppDirPath: String,
        currentScriptFileName: String,
        onShortcutValue: String,
        currentFannelState: String,

    ){
        val sharePrefMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to currentAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to currentScriptFileName,
            SharePrefferenceSetting.on_shortcut.name
                    to onShortcutValue,
            SharePrefferenceSetting.current_fannel_state.name
                    to currentFannelState,
        )
        putSharePreference (
            sharedPref,
            sharePrefMap
        )
    }


    fun putSharePreference (
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

    fun makeReadSharePreferenceMap(
        startUpPref: SharedPreferences?
    ): Map<String, String> {
        val sharedCurrentAppPath = getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )

        val sharedCurrentShellFileName = getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.current_fannel_name
        )

        val sharedOnShortcut = getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.on_shortcut
        )

        return mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to sharedCurrentAppPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to sharedCurrentShellFileName,
            SharePrefferenceSetting.on_shortcut.name
                    to sharedOnShortcut,
        )
    }

    fun getReadSharePreffernceMap(
        readSharePreferenceMap: Map<String, String>,
        sharePrefferenceSetting: SharePrefferenceSetting
    ): String {
        return try {
            readSharePreferenceMap.get(
                sharePrefferenceSetting.name
            ) ?: sharePrefferenceSetting.defalutStr
        } catch (e: Exception){
            sharePrefferenceSetting.defalutStr
        }
    }
}