package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object SettingFannelConHandlerForEdit {

    fun handle(
        editFragment: EditFragment
    ): List<String> {
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val currentFannelPath =
            getFannelPath(editFragment)

        val currentScriptContentsList = editFragment.currentFannelConList
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
        editFragment: EditFragment,
    ): String {
        val readSharePreferenceMap =
            editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        return File(currentAppDirPath, currentFannelName).absolutePath
    }
}