package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SettingFannelConHandlerForEdit
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

class ScriptFileSaver(
    private val editFragment: EditFragment,
) {
    private val readSharePreferenceMap = editFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    fun save(){
        val editedTextContents = EditedTextContents(
            editFragment,
        )
        val isSettingEdit = !IsCmdEdit.judge(editFragment)
        val editedShellContentsList = when(
            isSettingEdit
        ) {
            true -> {
                val settingFannelConList = SettingFannelConHandlerForEdit.handle(
                    editFragment
                )
                editedTextContents.updateBySettingVariables(
                    settingFannelConList,
                    editFragment.recordNumToMapNameValueInSettingHolder,
                )
            }

            else -> {
                val currentFannelConList = ReadText(
                    File(currentAppDirPath, currentFannelName).absolutePath
                ).textToList()
                editedTextContents.updateByCommandVariables(
                    currentFannelConList,
                    editFragment.recordNumToMapNameValueInCommandHolder,
                )
            }
        }
        editedTextContents.save(
            editedShellContentsList,
            isSettingEdit
        )
    }
}
