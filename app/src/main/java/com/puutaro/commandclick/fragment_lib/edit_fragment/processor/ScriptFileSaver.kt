package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SettingFannelConHandler
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

class ScriptFileSaver(
    private val editFragment: EditFragment,
) {
    private val readSharePreferenceMap = editFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
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
                val settingFannelConList = SettingFannelConHandler.handle(
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
