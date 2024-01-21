package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.substituteVariableListFromHolder
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ScriptPreWordReplacer


class EditedTextContents(
    private val editFragment: EditFragment,
) {

    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val currentAppDirPath = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_app_dir.name
    ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
    private val currentScriptFileName = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_fannel_name.name
    ) ?: SharePrefferenceSetting.current_fannel_name.defalutStr
    private val binding = editFragment.binding
    private val scriptContentsLister = ScriptContentsLister(
        binding.editLinearLayout
    )

    fun updateByCommandVariables(
        scriptContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
    ): List<String> {
        return if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            || editFragment.existIndexList
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
    ){
        if(
            lastScriptContentsList.isEmpty()
        ) return

        val submitScriptContentsList = CommentOutLabelingSection.commentOut(
            lastScriptContentsList,
            currentScriptFileName
        )

        FileSystems.writeFile(
            currentAppDirPath,
            currentScriptFileName,
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
            currentAppDirPath,
            currentScriptFileName
        )
    }

    private fun howUpdateLastModify(
        submitScriptContentsList: List<String>
    ): Boolean {
        val settingVariableList = substituteVariableListFromHolder(
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
