package com.puutaro.commandclick.util.state

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

object SharePrefTool {

    fun getReadSharePrefMap(
        fragment: Fragment,
        mainOrSubFannelPath: String?,
    ): Map<String, String> {
        if(
            mainOrSubFannelPath.isNullOrEmpty()
        ){
            return when(fragment){
                is CommandIndexFragment -> fragment.readSharePreferenceMap
                is EditFragment -> fragment.readSharePreferenceMap
                is TerminalFragment -> fragment.readSharePreferenceMap
                else -> mapOf()
            }
        }
        val currentAppDirPath = CcPathTool.getMainAppDirPath(
            mainOrSubFannelPath
        )
        val currentFannelName = File(
            CcPathTool.getMainFannelFilePath(mainOrSubFannelPath)
        ).name
        return mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to currentAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to currentFannelName,
        )

    }

    fun getCurrentAppDirPath(
        readSharePreferenceMap: Map<String, String>,
    ): String {
        return getValFromReadSharePrefMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
    }

    fun getCurrentFannelName(
        readSharePreferenceMap: Map<String, String>?,
    ): String {
        if(
            readSharePreferenceMap.isNullOrEmpty()
        ) return String()
        return getValFromReadSharePrefMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )
    }

    fun getCurrentStateName(
        readSharePreferenceMap: Map<String, String>?,
    ): String {
        if(
            readSharePreferenceMap.isNullOrEmpty()
        ) return String()
        return getValFromReadSharePrefMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_state
        )
    }

    fun getOnShortcut(
        readSharePreferenceMap: Map<String, String>?,
    ): String {
        if(
            readSharePreferenceMap.isNullOrEmpty()
        ) return String()
        return getValFromReadSharePrefMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        )
    }

    fun getReplaceVariableMap(
        fragment: Fragment,
        subFannelPath: String?
    ): Map<String, String>? {
        val context = fragment.context
        if(
            subFannelPath.isNullOrEmpty()
        ) return when(fragment){
            is EditFragment -> fragment.setReplaceVariableMap
            is TerminalFragment -> fragment.setReplaceVariableMap
            else -> mapOf()
        }
        return SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            subFannelPath
        )
    }

    fun getStringFromSharePref(
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

    fun putAllSharePref(
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
        putSharePref (
            sharedPref,
            sharePrefMap
        )
    }


    fun putSharePref (
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
        val sharedCurrentAppPath = getStringFromSharePref(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )

        val sharedCurrentShellFileName = getStringFromSharePref(
            startUpPref,
            SharePrefferenceSetting.current_fannel_name
        )

        val sharedOnShortcut = getStringFromSharePref(
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

    private fun getValFromReadSharePrefMap(
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