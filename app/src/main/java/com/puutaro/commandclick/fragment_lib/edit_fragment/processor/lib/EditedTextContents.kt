package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.CommandClickVariables.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.substituteVariableListFromHolder
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File


class EditedTextContents(
    binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>
) {

    private val currentAppDirPath = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_app_dir.name
    ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
    private val currentScriptFileName = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_script_file_name.name
    ) ?: SharePrefferenceSetting.current_script_file_name.defalutStr
    private val scriptContentsLister = ScriptContentsLister(
        binding.editLinearLayout
    )

    fun updateByCommandVariables(
        scriptContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
    ): List<String> {
        return if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            || editFragment.existIndexList
        ) {
            scriptContentsList
        } else {
            this.scriptContentsLister.update(
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

        val updateScriptFileName = makeUpdateScriptFileName(
            submitScriptContentsList,
            currentScriptFileName
        )

        FileSystems.writeFile(
            currentAppDirPath,
            updateScriptFileName,
            submitScriptContentsList.joinToString("\n")
        )
        if(
            updateScriptFileName.lowercase()
            == currentScriptFileName.lowercase()
        ) {
            judgeAndUpdateWeekAgoLastModify(
                submitScriptContentsList

            )
            return
        }
        val sharePref =
            editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to updateScriptFileName
            )
        )
        FileSystems.moveFileWithDir(
            File("$currentAppDirPath/$currentScriptFileName"),
            File("$currentAppDirPath/$updateScriptFileName"),
            true
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
        val fannelDirName = CcPathTool.makeFannelDirName(
            currentScriptFileName
        )
        val settingVariableList = substituteVariableListFromHolder(
            submitScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )?.joinToString("\n")?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                currentScriptFileName,
            )
        }?.split("\n")
        return !(
                CommandClickVariables.substituteCmdClickVariable(
                    settingVariableList,
                    CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
                ) == SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
                )
    }

    private fun makeUpdateScriptFileName(
        lastScriptContentsList: List<String>,
        currentScriptFileName: String
    ): String {
        val substituteSettingVariableList = substituteVariableListFromHolder(
            lastScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
        )
        val updateScriptFileNameSource = substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.SCRIPT_FILE_NAME
        ) ?: currentScriptFileName
        val scriptFileSuffix = when(editFragment.languageType){
            LanguageTypeSelects.SHELL_SCRIPT -> UsePath.SHELL_FILE_SUFFIX
            else -> UsePath.JS_FILE_SUFFIX
        }
        return UsePath.compExtend(
            updateScriptFileNameSource,
            scriptFileSuffix
        )
    }

    private fun makeFunnelDirPath(
        curentAppDirPath: String,
        scriptFileName: String,
    ): String {
        val selectedFannelDirName = CcPathTool.makeFannelDirName(
            scriptFileName
        )
        return curentAppDirPath + "/" + selectedFannelDirName
    }

}
