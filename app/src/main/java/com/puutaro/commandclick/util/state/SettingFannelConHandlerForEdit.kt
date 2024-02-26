package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object SettingFannelConHandlerForEdit {

    fun handle(
        editFragment: EditFragment
    ): List<String> {
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
//            when(fragment){
//            is EditFragment -> fragment.readSharePreferenceMap
//            is TerminalFragment -> fragment.readSharePreferenceMap
//            else -> return emptyList()
//        }
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
//            when(fragment){
//            is EditFragment -> fragment.setReplaceVariableMap
//            is TerminalFragment -> fragment.setReplaceVariableMap
//            else -> return emptyList()
//        }
        val currentFannelPath =
            getFannelPath(editFragment)

        val currentScriptContentsList = editFragment.currentFannelConList
//            when(fragment){
//            is EditFragment -> fragment.currentFannelConList
//            is TerminalFragment -> ReadText(
//                currentFannelPath
//            ).textToList()
//            else -> return emptyList()
//        }
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
//            when(editFragment){
//            is EditFragment -> editFragment.readSharePreferenceMap
//            is TerminalFragment -> editFragment.readSharePreferenceMap
//            else -> return String()
//        }
        val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = FannelPrefGetter.getCurrentFannelName(
            readSharePreferenceMap
        )
        return File(currentAppDirPath, currentFannelName).absolutePath
    }
}