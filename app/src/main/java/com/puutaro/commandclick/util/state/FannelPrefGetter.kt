package com.puutaro.commandclick.util.state

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

object FannelPrefGetter {
    fun getReadSharePreferenceMap(
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
        val currentAppDirPath = CcPathTool.getMainAppDirPath(mainOrSubFannelPath)
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
        return SharePreferenceMethod.getReadSharePreffernceMap(
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
        return SharePreferenceMethod.getReadSharePreffernceMap(
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
        return SharePreferenceMethod.getReadSharePreffernceMap(
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
        return SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        )
    }

    fun getReplaceVariableMap(
        fragment: Fragment,
        subFannelPath: String?
    ): Map<String, String>? {
        if(
            subFannelPath.isNullOrEmpty()
        ) return when(fragment){
            is EditFragment -> fragment.setReplaceVariableMap
            is TerminalFragment -> fragment.setReplaceVariableMap
            else -> mapOf()
        }
        return SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            subFannelPath
        )


    }
}