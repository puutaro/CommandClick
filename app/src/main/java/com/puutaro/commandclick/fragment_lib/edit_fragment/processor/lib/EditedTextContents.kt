package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.extractValListFromHolder
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.edit_tool.CcEditComponent
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import java.io.File


class EditedTextContents(
    private val editFragment: EditFragment,
) {

    private val readSharePreffernceMap = editFragment.readSharePreferenceMap
    private val currentAppDirPath = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_app_dir.name
    ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
    private val currentScriptFileName = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_fannel_name.name
    ) ?: SharePrefferenceSetting.current_fannel_name.defalutStr
    private val scriptContentsLister = ScriptContentsLister(
        CcEditComponent.makeEditLinearLayoutList(editFragment)
    )

    fun updateByCommandVariables(
        scriptContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
    ): List<String> {
        return if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
//            || editFragment.existIndexList
        ) {
            scriptContentsList
        } else {
            scriptContentsLister.update(
                recordNumToMapNameValueInCommandHolder,
                scriptContentsList,
                EditTextIdForEdit.COMMAND_VARIABLE.id
            )
        }
    }

    fun updateBySettingVariables(
        editedScriptContentsList: List<String>,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null
    ): List<String> {
        return if (recordNumToMapNameValueInSettingHolder == null) {
            editedScriptContentsList
        } else {
            scriptContentsLister.update(
                recordNumToMapNameValueInSettingHolder,
                editedScriptContentsList,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
        }
    }

    fun save(
        lastScriptContentsList: List<String>,
        isSettingEdit: Boolean
    ){
        if(
            lastScriptContentsList.isEmpty()
        ) return

        val submitScriptContentsList = CommentOutLabelingSection.commentOut(
            lastScriptContentsList,
            currentScriptFileName
        )
        val settingFannelPath = when(
            isSettingEdit
        ){
            true -> FannelStateRooterManager.getSettingFannelPath(
                editFragment.readSharePreferenceMap,
                editFragment.setReplaceVariableMap
            )
            else -> File(
                currentAppDirPath,
                currentScriptFileName,
            ).absolutePath
        }
        FileSystems.writeFile(
            settingFannelPath,
            submitScriptContentsList.joinToString("\n")
        )
        judgeAndUpdateWeekAgoLastModify(
            submitScriptContentsList

        )
    }

    private fun judgeAndUpdateWeekAgoLastModify(
        submitScriptContentsList: List<String>
    ){
        if(
            howUpdateLastModify(
                submitScriptContentsList
            )
        ) return
        FileSystems.updateWeekPastLastModified(
            File(
                currentAppDirPath,
                currentScriptFileName
            ).absolutePath
        )
    }

    private fun howUpdateLastModify(
        submitScriptContentsList: List<String>
    ): Boolean {
        val settingVariableList = extractValListFromHolder(
            submitScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )?.joinToString("\n")?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptFileName,
            )
        }?.split("\n")
        return substituteCmdClickVariable(
                    settingVariableList,
                    CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
                ) != SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
    }

}
