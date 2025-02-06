package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object SettingFannelConHandlerForEdit {

    fun handle(
        editFragment: EditFragment
    ): List<String> {
        val fannelInfoMap = editFragment.fannelInfoMap
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val currentFannelPath =
            getFannelPath(editFragment)

        val currentScriptContentsList = editFragment.currentFannelConList
        return execMakeSettingFanenlConList(
            fannelInfoMap,
            setReplaceVariableMap,
            currentFannelPath,
            currentScriptContentsList,
        )
    }

    private fun execMakeSettingFanenlConList(
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        currentFannelPath: String,
        currentScriptContentsList: List<String>,
    ): List<String> {
        val settingFannelPath = FannelStateRooterManager.getSettingFannelPath(
            fannelInfoMap,
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
        val fannelInfoMap =
            editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        return File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath
    }
}