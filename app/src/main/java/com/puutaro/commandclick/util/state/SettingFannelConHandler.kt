package com.puutaro.commandclick.util.state

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object SettingFannelConHandler {

    fun handle(
        fragment: Fragment
    ): List<String> {
        val readSharePreferenceMap = when(fragment){
            is EditFragment -> fragment.readSharePreferenceMap
            is TerminalFragment -> fragment.readSharePreferenceMap
            else -> return emptyList()
        }
        val setReplaceVariableMap = when(fragment){
            is EditFragment -> fragment.setReplaceVariableMap
            is TerminalFragment -> fragment.setReplaceVariableMap
            else -> return emptyList()
        }
        val currentFannelPath =
            getFannelPath(fragment)

        val currentScriptContentsList = when(fragment){
            is EditFragment -> fragment.currentFannelConList
            is TerminalFragment -> ReadText(
                currentFannelPath
            ).textToList()
            else -> return emptyList()
        }
        return execMakeSettingFanenlConList(
            readSharePreferenceMap,
            setReplaceVariableMap,
            currentFannelPath,
            currentScriptContentsList,
        )
    }

    private fun execMakeSettingFanenlConList(
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        currentFannelPath: String,
        currentScriptContentsList: List<String>,
    ): List<String> {
        val settingFannelPath = FannelStateRooterManager.getSettingFannelPath(
            readSharePreferenceMap,
            setReplaceVariableMap,
        )
        val isMainFannelPath =
            settingFannelPath == currentFannelPath
        return when(
            isMainFannelPath
        ){
            true -> currentScriptContentsList
            else -> ReadText(
                settingFannelPath
            ).textToList()
        }
    }

    private fun getFannelPath(
        fragment: Fragment,
    ): String {
        val readSharePreferenceMap = when(fragment){
            is EditFragment -> fragment.readSharePreferenceMap
            is TerminalFragment -> fragment.readSharePreferenceMap
            else -> return String()
        }
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        return File(currentAppDirPath, currentFannelName).absolutePath
    }
}